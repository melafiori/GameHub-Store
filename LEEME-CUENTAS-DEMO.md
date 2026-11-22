# Cuentas de presentación

Estas bases son nuevas y contienen únicamente tres cuentas de demostración, sin cuentas personales ni órdenes.

| Perfil | Correo | Contraseña |
|---|---|---|
| Cliente | cliente@gamehub.com | contraseña |
| Operador | operador@gamehub.com | contraseña |
| Administrador | administrador@gamehub.com | contraseña |

Las contraseñas están almacenadas con BCrypt. Las tres cuentas tienen la dirección de prueba Gamehub 333, Santiago.

El ZIP del backend incluye `data/auths.mv.db` y `data/users.mv.db`; al descomprimirlo en una carpeta nueva esas son las bases que usa el script de arranque. No lo descomprimas sobre una instalación con datos que quieras conservar, ya que podrías reemplazar sus cuentas. Las cuentas se mantienen cuando se detienen los servicios porque sus bases son archivos H2.

Sigue `ENTREGA_EP3.md`, ajustando las carpetas y JAVA_HOME al computador donde lo ejecutes. El ZIP incluye una copia del catálogo y stock; no incluye órdenes personales. Las reservas y movimientos históricos se retiraron de la copia de inventario para evitar conflictos con órdenes nuevas. No uses estas contraseñas para producción.

Se verificaron los tres inicios de sesión por Gateway y el acceso a los paneles desde una instalación extraída. Una compra de prueba se completó y apareció pagada tras recargar el historial. Los flujos de despacho, reseña y garantía no se repitieron de extremo a extremo en esta instalación.
