# Servicio de Gestión de Clientes

## Este proyecto es una aplicación Spring Boot que proporciona una API REST para gestionar datos de clientes. Permite realizar operaciones CRUD (crear, leer, actualizar y eliminar) para los clientes, así como actualizar datos de forma parcial.

### Prerrequisitos
Antes de comenzar, asegúrate de tener instalados los siguientes requisitos:

- Java 17 o superior
- Maven 3.6 o superior
- Docker (opcional, para ejecutar en contenedores)

### Configuración y Ejecución del Proyecto

1. Clonar el Repositorio
```bash
git clone https://www.github.com/wandersondevops/finances
```

### 2. Ejecutar la Aplicación
Inicia la aplicación con:

```bash
docker compose build
docker compose up
```

## Documentación de la API

La API proporciona endpoints para gestionar clientes a través del ClientController:

### URL Base

```bash
http://localhost:8080/clientes
```

### Obtener Cliente por ID
```bash
URL: /clientes/{clientId}
```

- Método: GET
Descripción: Obtiene los detalles de un cliente específico usando su UUID.
- Respuesta: Objeto JSON del cliente o 404 Not Found si no se encuentra.

```bash
curl -X GET http://localhost:8080/clientes/{clientId}
```
### Crear Clientes

- URL: /clientes
- Método: POST
- Descripción: Crea múltiples clientes en una sola solicitud.
Cuerpo de la Solicitud: Array JSON con objetos de cliente (sin clientId, ya que se generará automáticamente).
Respuesta: Array JSON de los clientes creados.

```bash
curl -X POST http://localhost:8080/clientes -H "Content-Type: application/json" -d '[{"name": "Juan Perez", "age": 30, "gender": "M"}]'
```

### Actualizar Cliente

- URL: /clientes/{clientId}
- Método: PUT
- Descripción: Actualiza completamente los datos de un cliente existente.
Cuerpo de la Solicitud: Objeto JSON con los campos del cliente a actualizar.
Respuesta: Objeto JSON del cliente actualizado o 404 Not Found si no se encuentra.

```bash
curl -X PUT http://localhost:8080/clientes/{clientId} -H "Content-Type: application/json" -d '{"name": "Carlos Lopez", "age": 35}'
```

### Actualización Parcial de Cliente

- URL: /clientes/{clientId}
- Método: PATCH
- Descripción: Actualiza parcialmente los datos de un cliente existente. Solo se actualizan los campos especificados.
Cuerpo de la Solicitud: Objeto JSON con los campos a actualizar.
Respuesta: Objeto JSON del cliente actualizado o 404 Not Found si no se encuentra.

```bash
curl -X PATCH http://localhost:8080/clientes/{clientId} -H "Content-Type: application/json" -d '{"age": 40}'
```

### Eliminar Todos los Clientes

- URL: /clientes
- Método: DELETE
- Descripción: Elimina todos los clientes del sistema.
Respuesta: 204 No Content.

```bash
```
curl -X DELETE http://localhost:8080/clientes

### Eliminar Cliente por ID

- URL: /clientes/{clientId}
- Método: DELETE
- Descripción: Elimina un cliente específico usando su UUID.
Respuesta: 204 No Content si el cliente es eliminado o 404 Not Found si no se encuentra.

```bash
curl -X DELETE http://localhost:8080/clientes/{clientId}
```
### Notas Adicionales
- Manejo de Errores: La API retorna 404 Not Found para clientes inexistentes y 400 Bad Request para datos de entrada en formato incorrecto.
- Seguridad: Este proyecto incluye encriptación de contraseñas para los clientes antes de guardarlas en la base de datos.
- Configuración de RabbitMQ: Se utiliza RabbitMQ para publicar eventos sobre las operaciones de creación, actualización y eliminación de clientes.
Licencia
- Este proyecto está bajo la Licencia MIT.


# Servicio de Gestión de Cuentas

Este proyecto es una aplicación Spring Boot que proporciona una API REST para gestionar cuentas de clientes. Incluye operaciones para crear, obtener, actualizar y eliminar datos de cuentas.

## Prerrequisitos
Asegúrate de tener instalados los siguientes elementos:

- Java 17 o superior
- Maven 3.6 o superior
- Docker (opcional, para configuración en contenedores)

Configuración y Ejecución del Proyecto
1. Clonar el Repositorio

```bash
git clone https://github.com/finances.git
```

2. Compilar el Proyecto
Usa Docker para ejecutar el proyecto:

```bash
docker compose build
docker compose up
```
- La aplicación estará disponible en http://localhost:8081.

## Documentación de la API

A continuación se detallan los endpoints proporcionados por el AccountController para gestionar cuentas:

### URL Base
```bash
```
http://localhost:8081/cuentas

### Endpoints

### Obtener Todas las Cuentas

URL: /cuentas
Método: GET
Descripción: Recupera una lista de todas las cuentas.
Respuesta: Array JSON con las cuentas.
```bash
curl -X GET http://localhost:8081/cuentas
```

### Obtener Cuenta por ID

URL: /cuentas/{accountId}
Método: GET
Descripción: Recupera una cuenta específica por su UUID.
Respuesta: Objeto JSON de la cuenta o 404 Not Found si no se encuentra.

```bash
curl -X GET http://localhost:8081/cuentas/{accountId}
```

- Crear Cuentas

URL: /cuentas
Método: POST
Descripción: Crea múltiples cuentas a la vez.
Cuerpo de la Solicitud: Array JSON de objetos de cuenta (sin accountId ya que se genera automáticamente).
Respuesta: Array JSON de las cuentas creadas.

```bash
curl -X POST http://localhost:8081/cuentas -H "Content-Type: application/json" -d '[{"accountType": "Checking", "balance": 1000.0}]'
```

- Actualizar Cuenta

URL: /cuentas/{accountId}
Método: PUT
Descripción: Actualiza completamente una cuenta existente.
Cuerpo de la Solicitud: Objeto JSON con los campos de la cuenta actualizados.
Respuesta: Objeto JSON de la cuenta actualizada o 404 Not Found si no se encuentra.

```bash
curl -X PUT http://localhost:8081/cuentas/{accountId} -H "Content-Type: application/json" -d '{"accountType": "Savings", "balance": 1500.0}'
```

- Actualización Parcial de Cuenta

URL: /cuentas/{accountId}
Método: PATCH
Descripción: Actualiza parcialmente una cuenta existente. Solo se actualizan los campos especificados.
Cuerpo de la Solicitud: Objeto JSON con los campos a actualizar.
Respuesta: Objeto JSON de la cuenta actualizada o 404 Not Found si no se encuentra.

```bash
curl -X PATCH http://localhost:8081/cuentas/{accountId} -H "Content-Type: application/json" -d '{"balance": 2000.0}'
```

- Eliminar Todas las Cuentas

URL: /cuentas
Método: DELETE
Descripción: Elimina todas las cuentas del sistema.
Respuesta: 204 No Content.

```bash
curl -X DELETE http://localhost:8081/cuentas
```

- Eliminar Cuenta por ID

URL: /cuentas/{accountId}
Método: DELETE
Descripción: Elimina una cuenta específica por su UUID.
Respuesta: 204 No Content si la cuenta es eliminada o 404 Not Found si no se encuentra.

```bash
curl -X DELETE http://localhost:8081/cuentas/{accountId}
```

### Notas Adicionales

- Manejo de Errores: La API retorna 404 Not Found para cuentas inexistentes y 400 Bad Request para datos de entrada en formato inválido.
- Configuración de la Base de Datos: Este servicio está configurado para usar una base de datos embebida de forma predeterminada. Para un entorno de producción, actualiza application.properties para conectarte a una base de datos externa.

- Licencia
Este proyecto está bajo la Licencia MIT.