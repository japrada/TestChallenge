package com.testchallenge.server;

import com.testchallenge.model.Mensaje;
import com.testchallenge.model.TipoMensaje;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class TestChallengeServerConcurrencyTest {

    @TempDir
    Path tempDir;

    @Test
    void testNicknameValidationRejectsInvalidNames() throws Exception {
        TestChallengeServer server = new TestChallengeServer(0, tempDir.toString());
        Method isNicknameValido = TestChallengeServer.class.getDeclaredMethod("isNicknameValido", String.class);
        isNicknameValido.setAccessible(true);

        assertAll(
                () -> assertFalse((boolean) isNicknameValido.invoke(server, (Object) null)),
                () -> assertFalse((boolean) isNicknameValido.invoke(server, "   ")),
                () -> assertFalse((boolean) isNicknameValido.invoke(server, "nickname@server")),
                () -> assertFalse((boolean) isNicknameValido.invoke(server, "nickname,server")),
                () -> assertFalse((boolean) isNicknameValido.invoke(server, "\u0001bad")),
                () -> assertFalse((boolean) isNicknameValido.invoke(server, "a".repeat(33))),
                () -> assertTrue((boolean) isNicknameValido.invoke(server, "usuario01")),
                () -> assertTrue((boolean) isNicknameValido.invoke(server, "usuario_01"))
        );
    }

    @Test
    void testDuplicateNicknameDetectionUsesNicknameEquality() {
        TestChallengeServerThread first = new TestChallengeServerThread("same");
        TestChallengeServerThread second = new TestChallengeServerThread("same");
        TestChallengeServerThread third = new TestChallengeServerThread("other");

        assertEquals(first, second);
        assertNotEquals(first, third);
    }

    @Test
    void testConcurrentConnectionRegistrationAndCleanup() throws Exception {
        TestChallengeServer server = new TestChallengeServer(0, tempDir.toString());
        int clients = 12;
        ExecutorService executor = Executors.newFixedThreadPool(clients);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < clients; i++) {
            final int index = i;
            futures.add(executor.submit(() -> {
                TestChallengeServerThread client = new TestChallengeServerThread("cliente-" + index);
                client.setClientDataSocket(new Socket());
                server.registrarConexion(client);
                return null;
            }));
        }

        for (Future<?> future : futures) {
            future.get(5, TimeUnit.SECONDS);
        }
        assertEquals(clients, server.getClientesConectados().size());

        futures.clear();
        for (TestChallengeServerThread client : server.getClientesConectados()) {
            futures.add(executor.submit(() -> {
                server.registrarDesconexion(client);
                return null;
            }));
        }

        for (Future<?> future : futures) {
            future.get(5, TimeUnit.SECONDS);
        }
        assertTrue(server.getClientesConectados().isEmpty());
        executor.shutdownNow();
    }

    @Test
    void testBroadcastMessageDestinationUsesConnectedClientsOnly() throws Exception {
        TestChallengeServer server = new TestChallengeServer(0, tempDir.toString());
        TestChallengeServerThread sender = new TestChallengeServerThread("alice");
        TestChallengeServerThread bob = new TestChallengeServerThread("bob");
        TestChallengeServerThread charlie = new TestChallengeServerThread("charlie");

        sender.setTestChallengeServer(server);
        bob.setTestChallengeServer(server);
        charlie.setTestChallengeServer(server);

        server.registrarConexion(sender);
        server.registrarConexion(bob);
        server.registrarConexion(charlie);

        ByteArrayOutputStream bobBytes = new ByteArrayOutputStream();
        ByteArrayOutputStream charlieBytes = new ByteArrayOutputStream();
        bob.setClientDataOut(new ObjectOutputStream(bobBytes));
        charlie.setClientDataOut(new ObjectOutputStream(charlieBytes));

        Method reenviarMensajeAlResto = TestChallengeServerThread.class.getDeclaredMethod("reenviarMensajeAlResto", String.class);
        reenviarMensajeAlResto.setAccessible(true);
        reenviarMensajeAlResto.invoke(sender, "hola");

        Mensaje mensajeBob = (Mensaje) new ObjectInputStream(new ByteArrayInputStream(bobBytes.toByteArray())).readObject();
        Mensaje mensajeCharlie = (Mensaje) new ObjectInputStream(new ByteArrayInputStream(charlieBytes.toByteArray())).readObject();

        assertEquals("hola", mensajeBob.getTexto());
        assertEquals("hola", mensajeCharlie.getTexto());
    }

    @Test
    void testTargetsAreDeduplicatedInPrivateMessageExpansion() throws Exception {
        TestChallengeServer server = new TestChallengeServer(0, tempDir.toString());
        TestChallengeServerThread sender = new TestChallengeServerThread("alice");
        sender.setTestChallengeServer(server);

        TestChallengeServerThread bob = new TestChallengeServerThread("bob");
        TestChallengeServerThread charlie = new TestChallengeServerThread("charlie");
        server.registrarConexion(sender);
        server.registrarConexion(bob);
        server.registrarConexion(charlie);

        Method getNicknames = TestChallengeServerThread.class.getDeclaredMethod("getNicknames", String.class);
        getNicknames.setAccessible(true);

        @SuppressWarnings("unchecked")
        Set<String> targets = (Set<String>) getNicknames.invoke(sender, "@bob @bob @charlie");
        assertEquals(new HashSet<>(List.of("bob", "charlie")), targets);
    }

    @Test
    void testConcurrentRankingUpdatesStayReadable() throws Exception {
        TestChallengeServer server = new TestChallengeServer(0, tempDir.toString());
        Map<String, Integer> ranking = server.getRanking();
        int threads = 8;
        int updatesPerThread = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            final int user = i;
            futures.add(executor.submit(() -> {
                start.await(2, TimeUnit.SECONDS);
                for (int j = 0; j < updatesPerThread; j++) {
                    ranking.put("user-" + user, j);
                }
                return null;
            }));
        }

        start.countDown();
        for (Future<?> future : futures) {
            future.get(5, TimeUnit.SECONDS);
        }

        assertTrue(ranking.containsKey("user-0"));
        assertTrue(ranking.containsKey("user-7"));
        executor.shutdownNow();
    }

    @Test
    void testSerializationFilterAllowsKnownTypesAndRejectsUnexpectedClass() {
        ObjectInputFilter filter = SerializationFilters.messagesOnly();

        assertAll(
                () -> assertEquals(ObjectInputFilter.Status.ALLOWED,
                        filter.checkInput(filterInfo(Mensaje.class, 1L, 1L, 128L, 1L))),
                () -> assertEquals(ObjectInputFilter.Status.REJECTED,
                        filter.checkInput(filterInfo(UnsupportedClass.class, 1L, 1L, 128L, 1L)))
        );
    }

    @Test
    void testSocketReadTimeoutCanBeObservedOnHandshakeLikeStream() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            try (Socket client = new Socket("localhost", serverSocket.getLocalPort())) {
                try (Socket accepted = serverSocket.accept()) {
                    accepted.setSoTimeout(100);
                    assertThrows(SocketTimeoutException.class,
                            () -> new ObjectInputStream(accepted.getInputStream()));
                }
            }
        }
    }

    private static ObjectInputFilter.FilterInfo filterInfo(Class<?> serialClass, long depth, long references,
            long streamBytes, long arrayLength) {
        return new DummyFilterInfo(serialClass, depth, references, streamBytes, arrayLength);
    }

    private static class DummyFilterInfo implements ObjectInputFilter.FilterInfo {

        private final Class<?> serialClass;
        private final long depth;
        private final long references;
        private final long streamBytes;
        private final long arrayLength;

        private DummyFilterInfo(Class<?> serialClass, long depth, long references, long streamBytes, long arrayLength) {
            this.serialClass = serialClass;
            this.depth = depth;
            this.references = references;
            this.streamBytes = streamBytes;
            this.arrayLength = arrayLength;
        }

        @Override
        public Class<?> serialClass() {
            return serialClass;
        }

        @Override
        public long depth() {
            return depth;
        }

        @Override
        public long references() {
            return references;
        }

        @Override
        public long streamBytes() {
            return streamBytes;
        }

        @Override
        public long arrayLength() {
            return arrayLength;
        }
    }

    private static final class UnsupportedClass implements Serializable {
        private static final long serialVersionUID = 1L;
    }
}
