# GameHub-Store
Integrantes: Cassie Muñoz, David Pérez y  Carolina Rivera.
## Descripción del Proyecto

GameHub Store es una plataforma basada en arquitectura de microservicios orientada a la gestión de una tienda online de videojuegos y productos gamer.
El sistema permite administrar usuarios, autenticación, órdenes de compra, pagos, despachos, reseñas y notificaciones, separando cada funcionalidad en microservicios independientes para mejorar la escalabilidad, mantenimiento y organización del sistema.

Cada microservicio posee su propia responsabilidad, base de datos y API REST, permitiendo la comunicación entre servicios mediante Feign Client.
## Arquitectura

El sistema utiliza arquitectura de microservicios, donde cada servicio funciona de manera independiente y expone endpoints REST para su consumo.

## Tecnologías utilizadas:

- Java 21
- Apache Maven 4.0.7
- Spring Boot
- Spring Data JPA
- Spring Cloud OpenFeign
- Spring Cloud Gateway
- Eureka Server
- H2 DATABASE
- Lombok
- Swagger / OpenAPI
- Postman
- GitHub
- JUnit
- Mockito

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

### eureka-service
Servidor de descubrimiento de servicios encargado de registrar y monitorear todos los microservicios de la plataforma. Permite que los servicios se encuentren y comuniquen entre sí de forma dinámica, facilitando la escalabilidad y la gestión centralizada de la arquitectura.

## Rutas Principales del GateAway
- Auth Service     -----   /api/v1/auth/**
- User Service      -----  /api/v1/users/**
- Category Service	----  /api/v1/categories/**
- Product Service	----  /api/v1/products/**	
- Inventory Service	----  /api/v1/inventory/**
- Order Service	    ----  /api/v1/orders/**
- Payment Service	----  /api/v1/payments/**
- Shipping Service  ----  /api/v1/shippings/**
- Review Service  ----	  /api/v1/reviews/**
- Promotion Service ---- /api/v1/promotions/**
- Warranty Service ----	/api/v1/warranties/**

## Enlaces Swagger
- http://localhost:8001/swagger-ui/index.html (Auth Service)
- http://localhost:8002/swagger-ui/index.html (Category Service)
- http://localhost:8003/swagger-ui/index.html (Inventory Service)
- http://localhost:8004/swagger-ui/index.html (Notification Service)
- http://localhost:8005/swagger-ui/index.html (Order Service)
- http://localhost:8006/swagger-ui/index.html (Payment Service)
- http://localhost:8007/swagger-ui/index.html (Product Service)
- http://localhost:8008/swagger-ui/index.html (Promotion Service)
- http://localhost:8009/swagger-ui/index.html (Review Service)
- http://localhost:8010/swagger-ui/index.html (User Service)
- http://localhost:8011/swagger-ui/index.html (Warranty Service)
- http://localhost:8015/swagger-ui/index.html (Shipping Service)

## Instrucciones Ejecución local
### Requisitos
- JDK 21
- Apache Maven 4.0.7
- IntelliJ IDEA
- Git
- Navegador web para acceder a Swagger UI y Eureka Server

### Pasos
1. Clonar repositorio:
https://github.com/melafiori/GameHub-Store.git

2. Abrir proyecto en IntelliJ IDEA
3. Ejecutar primero:
 - Eureka Server
 - API Gateway
4. Posteriormente, ejecutar los microservicios:
 - Auth Service 
 - User Service
 - Category Service
 - Product Service
 - Inventory Service
 - Order Service
 - Payment Service
   Shipping Service
 - Review Service
 - Promotion Service
 - Warranty Service
5. Verificar el registro de servicios en: http://localhost:8013
6. Consumir los endpoints mediante Postman o Swagger.