# GameHub-Store
Integrantes: Cassie Muñoz, David Pérez y  Carolina Rivera.
## Descripción del Proyecto

GameHub Store es una plataforma basada en arquitectura de microservicios orientada a la gestión de una tienda online de videojuegos y productos gamer.
El sistema permite administrar usuarios, autenticación, órdenes de compra, pagos, despachos, reseñas y notificaciones, separando cada funcionalidad en microservicios independientes para mejorar la escalabilidad, mantenimiento y organización del sistema.

Cada microservicio posee su propia responsabilidad, base de datos y API REST, permitiendo la comunicación entre servicios mediante Feign Client.
## Arquitectura

El sistema utiliza arquitectura de microservicios, donde cada servicio funciona de manera independiente y expone endpoints REST para su consumo.

El proyecto fue desarrollado utilizando:

- Java 21
- Spring Boot
- Spring Data JPA
- MySQL
- Maven
- Lombok
- OpenFeign

## Microservicios Implementados

### auth-service
Se encarga de todo el proceso de autenticación. Cuando un usuario inicia sesión, este servicio
valida sus credenciales y genera un token JWT (JSON Web Token), que es básicamente un "pase digital"
firmado que el usuario usa en las siguientes peticiones para demostrar que está autenticado, sin necesidad
de volver a ingresar su contraseña en cada solicitud

### user-service
Gestiona la información relacionada con los usuarios dentro del sistema. Esto incluye sus perfiles (nombre, correo, datos personales, etc.)Es el servicio al que se consulta cuando se necesita leer o actualizar datos de un usuario específico.

### product-service
Gestiona el catálogo completo de productos gamer. Es el servicio que contiene toda la información de cada producto: nombre, descripción, precio, categoría, imágenes, etc. es al que se consulta cuando un usuario navega o busca productos.

### inventory-service
Controla el stock real disponible de cada producto. Sabe cuántas unidades hay en existencia y si un producto está disponible o agotado

### promotion-service

Administra descuentos, cupones y promociones activas. Cuando un usuario aplica un código de descuento o hay una oferta vigente, este servicio es el responsable de calcular y validar cuánto se debe rebajar del precio original.

### order-service
Es el núcleo de las compras. Gestiona la creación y el seguimiento de las órdenes de compra, incluyendo qué productos se compraron, en qué cantidad y a qué precio. Coordina con varios servicios (inventory, payment, shipping) para completar el flujo de una compra.

### payment-service
Se encarga de procesar y registrar los pagos asociados a cada orden. Recibe la solicitud de cobro, la procesa y deja registro del resultado, ya sea aprobado o rechazado.

### shipping-service
Gestiona todo lo relacionado con el despacho físico del pedido. Crea el envío una vez confirmado el pago, asigna un número de seguimiento y permite rastrear en qué estado está la entrega (preparando, en camino, entregado.
### warranty-service
Administra las garantías de los productos comprados y las solicitudes postventa. Si un usuario tiene un problema con un producto después de comprarlo, este servicio gestiona el proceso de garantía o reclamo.


### review-service
Permite a los usuarios registrar reseñas y puntuar los productos que han comprado. Almacena los comentarios y calificaciones

### notification-service
Gestiona el envío de notificaciones internas hacia los usuarios. Por ejemplo, avisar cuando un pago fue confirmado, cuando el pedido fue despachado o cuando una garantía fue aprobada.

### gateway-service
Es la puerta de entrada única a todo el sistema. En lugar de que el cliente (frontend o aplicación) tenga que conocer la dirección de cada microservicio individualmente, todas las peticiones pasan primero por el gateway. Él se encarga de recibir la solicitud, determinar a qué microservicio va dirigida y redirigirla correctamente.