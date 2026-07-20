# Arquitectura general de AnalytiCore

## 1. Propósito

AnalytiCore es una plataforma web que permite a un usuario ingresar un texto para obtener un análisis simple de sentimiento y una lista de palabras clave.

El sistema utiliza una arquitectura orientada a servicios y diferentes tecnologías para demostrar la integración entre aplicaciones desarrolladas con React, Python y Java.

## 2. Componentes

### Frontend web

Tecnologías:

* React.
* Vite.
* Axios.
* Nginx.
* Docker.

Responsabilidades:

* Mostrar la interfaz de usuario.
* Recibir el texto que se desea analizar.
* Validar que el texto no esté vacío.
* Enviar el texto al servicio Python.
* Recibir y almacenar temporalmente el `jobId`.
* Consultar periódicamente el estado del trabajo.
* Mostrar el sentimiento y las palabras clave.
* Presentar mensajes de carga y error.

El frontend no se conectará directamente con Java ni con PostgreSQL.

### Servicio de submisión

Tecnologías:

* Python.
* FastAPI.
* SQLAlchemy.
* PostgreSQL.
* Docker.

Responsabilidades:

* Recibir solicitudes del frontend.
* Validar el texto recibido.
* Generar un identificador UUID.
* Crear el trabajo en PostgreSQL.
* Registrar el estado inicial `PENDIENTE`.
* Solicitar al servicio Java que inicie el análisis.
* Devolver el `jobId` al frontend.
* Permitir la consulta del estado y los resultados.
* Gestionar errores de comunicación.

Este servicio será la API pública principal del sistema.

### Servicio de análisis

Tecnologías:

* Java.
* Spring Boot.
* Spring Data JPA.
* PostgreSQL.
* Docker.

Responsabilidades:

* Recibir el `jobId` enviado por Python.
* Buscar el trabajo en PostgreSQL.
* Cambiar su estado a `PROCESANDO`.
* Analizar el sentimiento del texto.
* Extraer las palabras clave.
* Guardar los resultados.
* Cambiar el estado a `COMPLETADO`.
* Cambiar el estado a `ERROR` cuando ocurra una falla.

Este servicio se utilizará únicamente mediante una API REST interna.

### Base de datos

Tecnología:

* PostgreSQL.

Responsabilidades:

* Almacenar el texto enviado.
* Almacenar el estado de cada trabajo.
* Almacenar el sentimiento obtenido.
* Almacenar las palabras clave.
* Almacenar los mensajes de error.
* Mantener las fechas de creación y actualización.

PostgreSQL será la única fuente de verdad del sistema. Los servicios no guardarán trabajos ni resultados permanentemente en memoria.

## 3. Flujo principal

1. El usuario abre la aplicación React.
2. El usuario escribe un texto.
3. React valida que el texto no esté vacío.
4. React envía una petición a Python.
5. Python valida nuevamente el texto.
6. Python genera un UUID que se utilizará como `jobId`.
7. Python guarda el trabajo en PostgreSQL con estado `PENDIENTE`.
8. Python llama al servicio Java mediante una API REST interna.
9. Java busca el trabajo utilizando el `jobId`.
10. Java cambia el estado a `PROCESANDO`.
11. Java realiza el análisis.
12. Java guarda el sentimiento y las palabras clave.
13. Java cambia el estado a `COMPLETADO`.
14. Python devuelve el `jobId` al frontend.
15. React consulta periódicamente el estado.
16. React muestra el resultado cuando el estado es `COMPLETADO`.

## 4. Estados del trabajo

### PENDIENTE

El trabajo fue creado correctamente, pero el análisis todavía no ha comenzado.

### PROCESANDO

El servicio Java está analizando el texto.

### COMPLETADO

El análisis terminó y los resultados están disponibles.

### ERROR

El análisis no pudo completarse debido a un problema técnico.

## 5. Comunicación entre componentes

Toda la comunicación entre los servicios se realizará mediante APIs REST y mensajes JSON.

El flujo autorizado será:

```text
Usuario
   ↓
Frontend React + Nginx
   ↓ API REST pública
Servicio Python
   ↓ API REST interna
Servicio Java
   ↓
PostgreSQL
```

React no podrá comunicarse directamente con Java.

React no podrá conectarse directamente con PostgreSQL.

Python y Java compartirán PostgreSQL, pero cada servicio manejará solamente las operaciones relacionadas con sus responsabilidades.

## 6. Arquitectura limpia

Cada componente estará organizado en cuatro áreas principales:

### Dominio

Contendrá las entidades y reglas principales del negocio.

### Aplicación

Contendrá los casos de uso que coordinan el funcionamiento del sistema.

### Infraestructura

Contendrá las conexiones con PostgreSQL, clientes HTTP, frameworks y servicios externos.

### Presentación

Contendrá las interfaces utilizadas para recibir o mostrar información.

## 7. Servicios sin estado

React, Python y Java no mantendrán permanentemente los trabajos en memoria.

Toda la información necesaria para recuperar el estado de un análisis se almacenará en PostgreSQL.

Esto permitirá reiniciar o reemplazar los servicios sin perder los trabajos ya registrados.

## 8. Configuración externa

Las direcciones de los servicios y los datos de conexión se proporcionarán mediante variables de entorno.

Variables principales:

* `DATABASE_URL`
* `DATABASE_USERNAME`
* `DATABASE_PASSWORD`
* `JAVA_SERVICE_URL`
* `VITE_API_URL`
* `ALLOWED_ORIGINS`

Las contraseñas y direcciones de producción no se escribirán directamente en el código.

## 9. Despliegue

El sistema se desplegará en Render mediante los siguientes recursos:

* Un servicio web para React y Nginx.
* Un servicio web para Python.
* Un servicio web para Java.
* Una base de datos PostgreSQL gestionada.

Cada aplicación tendrá su propia imagen Docker.
