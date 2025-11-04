# Proyecto HicScan

## ¿Qué hace la aplicación?

HicScan es una aplicación Spring Boot que procesa Historias Clínicas Electrónicas (HCE) para extraer información clínica relevante. La aplicación permite:

- **Procesar HCE**: Extrae información de pacientes, medicamentos, observaciones y estudios médicos de documentos XML en formato CDA (Clinical Document Architecture)
- **Análisis de texto clínico**: Utiliza Apache cTAKES para procesar texto libre y extraer entidades médicas
- **Detección de incompatibilidades**: A partir de una HCE y una ontología, determina incompatibilidades entre estudios y medicamentos
- **API REST**: Expone endpoints para consultar datos de pacientes, obtener información extendida y recibir recomendaciones basadas en las HCE procesadas

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

## Cómo ejecutar la aplicación

### Opción 1: Usando Maven (Recomendado para desarrollo)

1. **Compilar el proyecto**:
   ```bash
   mvn clean install
   ```

2. **Ejecutar la aplicación**:
   ```bash
   mvn spring-boot:run
   ```

   O en Windows:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

### Opción 2: Ejecutar el JAR compilado

1. **Compilar y empaquetar**:
   ```bash
   mvn clean package
   ```

2. **Ejecutar el JAR generado**:
   ```bash
   java -jar target/hceanalysis-0.0.1-SNAPSHOT.jar
   ```

### Verificar que la aplicación está corriendo

Una vez iniciada, la aplicación estará disponible en:
- **URL base**: http://localhost:8082
- **Endpoint de verificación**: http://localhost:8082/HCE/

Puedes abrir tu navegador y visitar http://localhost:8082/HCE/ para verificar que el servidor está funcionando correctamente. Deberías ver el mensaje: "Servidor funcionando correctamente."

### Configuración

La aplicación utiliza el archivo `src/main/resources/application.properties` para su configuración. Los principales parámetros son:
- **Puerto del servidor**: 8082 (por defecto)
- **URL del triplestore**: http://179.27.97.6:3030
- **URL de OntoForms**: http://179.27.97.6:8081/ontoforms-api/v1/

Si necesitas cambiar estos valores, modifica el archivo `application.properties`.

## Principales Endpoints

La aplicación expone dos grupos principales de endpoints:

### Endpoints de HCE (`/HCE`)

Endpoints para consultar y procesar Historias Clínicas Electrónicas:

- **`GET /HCE/`**
  - Verifica que el servidor está funcionando
  - Retorna: "Servidor funcionando correctamente."

- **`GET /HCE/obtenerDatosPacienteBasico`**
  - Obtiene datos básicos del paciente a partir del ID de la HCE
  - Parámetros: `idPaciente` (en el body como String)
  - Retorna: Datos básicos del paciente o 404 si no existe

- **`GET /HCE/obtenerDatosPacienteExtendido`**
  - Obtiene datos clínicos extendidos de la HCE, incluyendo medicamentos y observaciones
  - Parámetros: `idPaciente` (query parameter)
  - Retorna: Información extendida del paciente con medicamentos y observaciones, o 404 si no existe

- **`POST /HCE/obtenerDatosPacienteExtendidoConRecomendaciones`**
  - Obtiene toda la información del paciente junto con recomendaciones basadas en la HCE y datos del cuestionario
  - Body: JSON con `id` (ID del paciente) y `womanHistoryData` (mapa con información del cuestionario)
  - Retorna: Datos extendidos del paciente con recomendaciones, o 404 si no existe

- **`GET /HCE/obtenerTodosLosPacientesBasicos`**
  - Obtiene una lista de todos los pacientes con HCE cargada en el sistema
  - Retorna: Lista de pacientes básicos con sus IDs

### Endpoints de Configuración (`/hicscan-api/config/v1`)

Endpoints para gestionar ontologías y configuraciones:

- **`GET /hicscan-api/config/v1/ontologies`**
  - Obtiene la lista de todas las ontologías disponibles
  - Retorna: Array de IDs de ontologías

- **`GET /hicscan-api/config/v1/ontologies/{ontoId}/classes`**
  - Obtiene el árbol de clases de una ontología específica
  - Parámetros: `ontoId` (path variable)
  - Retorna: Árbol de clases de la ontología

- **`GET /hicscan-api/config/v1/ontologies/{ontoId}/classes/form`**
  - Obtiene el formulario para modificar propiedades de una clase
  - Parámetros: `ontoId` (path variable), `classUri` (query parameter)
  - Retorna: Lista de descriptores de propiedades con estado del formulario

- **`GET /hicscan-api/config/v1/ontologies/{ontoId}/risk-model/form`**
  - Obtiene el formulario filtrado para un modelo de riesgo específico
  - Parámetros: `ontoId` (path variable), `classUri` (query parameter), `riskModelUri` (query parameter)
  - Retorna: Lista de descriptores de propiedades filtradas por el modelo de riesgo

- **`POST /hicscan-api/config/v1/ontology`**
  - Crea una nueva ontología en el sistema
  - Parámetros: `file` (MultipartFile - archivo de la ontología), `ontologyName` (nombre de la ontología)
  - Retorna: ID y nombre de la ontología creada

- **`POST /hicscan-api/config/v1/ontologies/{ontoId}/configurations/calculated-properties`**
  - Configura una propiedad calculada para una ontología
  - Parámetros: `ontoId` (path variable), body JSON con configuración de propiedad calculada
  - Retorna: 200 OK

- **`DELETE /hicscan-api/config/v1/ontologies/{ontoId}/configurations/calculated-properties`**
  - Elimina la configuración de una propiedad calculada
  - Parámetros: `ontoId` (path variable), body JSON con configuración a eliminar
  - Retorna: 200 OK

- **`POST /hicscan-api/config/v1/ontologies/{ontoId}/configurations/artifice-classes`**
  - Configura una clase artifice para una ontología
  - Parámetros: `ontoId` (path variable), body JSON con configuración de clase artifice
  - Retorna: 200 OK

- **`DELETE /hicscan-api/config/v1/ontologies/{ontoId}/configurations/artifice-classes`**
  - Elimina la configuración de una clase artifice
  - Parámetros: `ontoId` (path variable), body JSON con configuración a eliminar
  - Retorna: 200 OK

## Diccionario de medicamentos
El diccionario de medicamentos que se utiliza se puede obtener en la web de AGESIC, a través del link https://archivos.agesic.gub.uy/nextcloud/index.php/s/kMe8mgrR7f6F8CQ .

En el proyecto se encuentra en la ruta:
.../src/main/java/uy/com/fing/hicscan/hceanalysis/languageexpansion/resources

De querer actualizarlo debe de sobreescribirlo en la ruta descrita anteriormente, asegurandose de que se mantenga el nombre DiccionarioMedicamentos_38b0e1.xml .

## Errores comunes
### Error: org.apache.ctakes.dictionary.lookup.exception.LookupException: File not found
  Esto ocurre si no has extraído el archivo ctakes-resources.zip en la misma carpeta que la aplicación.
  
  Solución: Asegúrate de que la carpeta resources exista junto a la carpeta clonada.
