#!/usr/bin/env bash

# -e: termina el script cuando falla un comando.
# -u: termina si usamos una variable que no existe.
# pipefail: detecta fallos dentro de comandos conectados con |.
set -euo pipefail

# Cuando un patrón con * no encuentra archivos, devuelve cero resultados
# en vez de dejar el patrón escrito literalmente.
#
# Sin nullglob:
#   target/*-jar-with-dependencies.jar
# podría contarse como un elemento aunque no exista.
#
# Con nullglob, el array queda vacío.
shopt -s nullglob

# Carpeta en la que está guardado este script.
# Permite ejecutar el script desde cualquier directorio.
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
TARGET_DIR="$PROJECT_DIR/target"

# Directorios temporales donde se extraerá el JAR completo.
CLIENT_DIR="$TARGET_DIR/client-jar"
SERVER_DIR="$TARGET_DIR/server-jar"

# El cliente se guarda en la raíz del proyecto.
CLIENT_OUTPUT="$PROJECT_DIR/TestChallengeClient.jar"

# El servidor se guarda dentro de target. Por como está la configuración del Dockerfile
SERVER_OUTPUT="$TARGET_DIR/TestChallengeServer.jar"

# Clase principal del servidor.
# El cliente no necesita esta variable ya que viene por defecto en el pom. 
SERVER_MAIN_CLASS="com.testchallenge.server.TestChallengeServer"

echo "========================================"
echo " Generador de JAR cliente y servidor"
echo "========================================"

cd "$PROJECT_DIR"

# ==========================================================
# MAVEN
# ==========================================================

echo
echo "[1/6] Compilando el proyecto con Maven..."

mvn clean package -DskipTests

# Maven incluye la versión en el nombre. Queremos que unicamente haya un jar, con la última versión:
# TestChallenge-1.2-jar-with-dependencies.jar
# TestChallenge-1.3-jar-with-dependencies.jar
#
# Por eso no escribimos una versión fija.
SOURCE_JARS=("$TARGET_DIR"/*-jar-with-dependencies.jar)

# ${#SOURCE_JARS[@]} devuelve el número de elementos del array.
# Solo continuamos si encontramos exactamente un JAR.
if [[ ${#SOURCE_JARS[@]} -ne 1 ]]; then
    echo "Error: se esperaba exactamente un JAR con dependencias." >&2
    echo "JAR encontrados: ${#SOURCE_JARS[@]}" >&2

    if [[ ${#SOURCE_JARS[@]} -gt 0 ]]; then 
        printf '  %s\n' "${SOURCE_JARS[@]}" >&2
    fi

    exit 1
fi

SOURCE_JAR="${SOURCE_JARS[0]}"

echo "JAR base localizado:"
echo "$SOURCE_JAR"

# ==========================================================
# PREPARACIÓN
# ==========================================================

echo
echo "[2/6] Preparando las carpetas temporales..."

# Se eliminan las carpetas anteriores para evitar clases antiguas.
rm -rf "$CLIENT_DIR" "$SERVER_DIR"

mkdir -p "$CLIENT_DIR"
mkdir -p "$SERVER_DIR"

# También eliminamos posibles JAR finales anteriores.
rm -f "$CLIENT_OUTPUT" "$SERVER_OUTPUT"

# ==========================================================
# CLIENTE
# ==========================================================

echo
echo "[3/6] Extrayendo el JAR para el cliente..."

cd "$CLIENT_DIR"

jar -xf "$SOURCE_JAR"

echo
echo "[4/6] Generando TestChallengeClient.jar..."

# No modificamos META-INF/MANIFEST.MF porque Maven ya incluye:
#
# Main-Class: com.testchallenge.client.gui.TestChallengeClient
jar -cfmv "$CLIENT_OUTPUT" META-INF/MANIFEST.MF \
    org/apache/* \
    org/jaudiotagger/* \
    com/google/* \
    com/testchallenge/client/* \
    com/testchallenge/model/* \
    images/* \
    javazoom/*

# ==========================================================
# SERVIDOR
# ==========================================================

echo
echo "[5/6] Extrayendo y configurando el JAR del servidor..."

cd "$SERVER_DIR"

jar -xf "$SOURCE_JAR"

MANIFEST_FILE="META-INF/MANIFEST.MF"

# El manifiesto original apunta al cliente, así que sustituimos:
#
# Main-Class: com.testchallenge.client.gui.TestChallengeClient
#
# por:
#
# Main-Class: com.testchallenge.server.TestChallengeServer
#
# -i.bak modifica el archivo directamente y crea antes una copia:
# META-INF/MANIFEST.MF.bak
#
# Ponemos todo el sed en una sola línea para evitar que una barra invertida
# mal colocada haga que sed crea que debe leer desde stdin.
if grep -q '^Main-Class:' "$MANIFEST_FILE"; then
    sed -i.bak "s|^Main-Class:.*|Main-Class: $SERVER_MAIN_CLASS|" "$MANIFEST_FILE"

    # Borramos la copia de seguridad porque ya no hace falta.
    rm -f "${MANIFEST_FILE}.bak"
else
    # Si por algún motivo no existiera Main-Class, la añadimos.
    printf '\nMain-Class: %s\n' "$SERVER_MAIN_CLASS" >> "$MANIFEST_FILE"
fi

echo
echo "[6/6] Generando TestChallengeServer.jar..."

jar -cfmv "$SERVER_OUTPUT" META-INF/MANIFEST.MF \
    org/apache/* \
    org/jaudiotagger/* \
    com/google/* \
    com/testchallenge/server/* \
    com/testchallenge/model/* \
    images/* \
    javazoom/*

# ==========================================================
# RESULTADO
# ==========================================================

echo
echo "========================================"
echo " Proceso terminado correctamente"
echo "========================================"

echo
echo "JAR del cliente:"
echo "$CLIENT_OUTPUT"

echo
echo "JAR del servidor:"
echo "$SERVER_OUTPUT"

echo
echo "Main-Class del cliente:"
unzip -p "$CLIENT_OUTPUT" META-INF/MANIFEST.MF |
    grep '^Main-Class:'

echo
echo "Main-Class del servidor:"
unzip -p "$SERVER_OUTPUT" META-INF/MANIFEST.MF |
    grep '^Main-Class:'
