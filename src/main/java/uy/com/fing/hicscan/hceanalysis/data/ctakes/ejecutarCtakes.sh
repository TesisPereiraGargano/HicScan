#!/bin/bash

# Parametros:
# $1 = ruta al archivo .piper
# $2 = archivo de texto de entrada 
# $3 = ruta a CTAKES_HOME (opcional - si no esta globalmente definido)
# $4 = key a UMLS
# $5 = ruta donde se va a guardar la salida

# Definir CTAKES_HOME si viene como parametro
if [ -n "$3" ]; then
    export CTAKES_HOME="$3"
fi

# Imprimo la ruta al archivo que se va a procesar
echo "Ejecutando pipeline: $1"

# Imprimo la entrada que va a procesar ctakes
if [ -n "$2" ]; then
    echo "Texto que va a procesar ctakes: $2"
fi

# Me muevo al directorio de cTAKES
cd "$CTAKES_HOME" || { echo "No se pudo acceder a $CTAKES_HOME"; exit 1; }

# Ejecuto el pipeline Piper
if [ -n "$5" ]; then
    ./bin/runPiperFile.sh -i "$2" -o "$5" --key "$4" -p "$1"
fi


# Fin
