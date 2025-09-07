# Proyecto HicScan

## Requisitos previos
- Java 17 
- Maven 
- Sistema operativo compatible: Windows/Linux

## Instalación

1. **Descargar la aplicación**  
   Clona este repositorio o descarga el paquete que contiene la aplicación.

2. **Extraer los archivos necesarios**  
   Antes de ejecutar la aplicación, debes extraer el contenido del archivo:  resourcesCtakes.zip que se encuentra en la raiz del proyecto.
   Al descomprimirlo deberías ver una caperta con la ruta "/root/resources/org/apache/ctakes/dictionary/lookup/fast/sno_rx_16ab" y dos archivos:
   - sno_rx_16ab.script
   - sno_rx_16ab.properties
   Debes de copiar la carpeta resources extraída a la misma ruta dónde clonaste el repositorio.

   Debería de quedarte algo así:
```text
/ruta-de-clonado-de-HicScan
├── resources/           <-- Contenido extraído anteriomente
├── hicscan.jar          <-- Archivo ejecutable de la aplicación
├── src/                 <-- Código fuente
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── README.md            <-- Este archivo
└── pom.xml              <-- Archivo de Maven (si es un proyecto Java)
```
## Diccionario de medicamentos
El diccionario de medicamentos que se utiliza se puede obtener en la web de AGESIC, a través del link https://archivos.agesic.gub.uy/nextcloud/index.php/s/kMe8mgrR7f6F8CQ .

En el proyecto se encuentra en la ruta:
.../src/main/java/uy/com/fing/hicscan/hceanalysis/languageexpansion/resources

De querer actualizarlo debe de sobreescribirlo en la ruta descrita anteriormente, asegurandose de que se mantenga el nombre DiccionarioMedicamentos_38b0e1.xml .

## Errores comunes
### Error: org.apache.ctakes.dictionary.lookup.exception.LookupException: File not found
  Esto ocurre si no has extraído el archivo ctakes-resources.zip en la misma carpeta que la aplicación.
  
  Solución: Asegúrate de que la carpeta resources exista junto a la carpeta clonada.
