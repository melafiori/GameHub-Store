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
Gestiona autenticación y generación de JWT.

### user-service
Administra perfiles y direcciones de usuarios.

### product-service
Gestiona el catálogo de productos gamer.

### inventory-service
Controla stock y disponibilidad de productos.

### promotion-service
Administra descuentos y promociones.

### order-service
Gestiona órdenes de compra y sus detalles.

### payment-service
Procesa y registra pagos asociados a órdenes.

### shipping-service
Gestiona despachos y seguimiento de envíos.

### warranty-service
Administra garantías y solicitudes postventa.

### review-service
Permite registrar reseñas y puntuaciones de productos.

### notification-service
Gestiona notificaciones internas para usuarios.

### gateway-service
Centraliza el acceso a los microservicios