# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/TestChallengeServer.jar  ./TestChallengeServer.jar
COPY preguntas ./preguntas
#Hacemos que el contenedor se ejecute con un usuario no root para mejorar la seguridad 
RUN groupadd --system testchallenge \
    && useradd --system --gid testchallenge --home-dir /app --shell /usr/sbin/nologin testchallenge \
    && chown -R testchallenge:testchallenge /app

EXPOSE 5000
USER testchallenge
ENTRYPOINT ["java", "-jar", "TestChallengeServer.jar"]
CMD ["5000", "/app/preguntas"]

