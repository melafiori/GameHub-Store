# GameHub Store EP3 — instalación para presentación

Este archivo explica cómo ejecutar los ZIP de entrega en Windows con PowerShell. Descomprime backend y frontend en **carpetas nuevas**, por ejemplo `C:\GameHubEP3\backend` y `C:\GameHubEP3\frontend`. No los extraigas encima de una instalación existente: el backend incluye bases H2 con catálogo e identidades de demostración.

Requisitos: Java 21 o superior, Node.js 22 o superior, npm, conexión a Internet para descargar dependencias y puertos 5173 y 19601–19652 disponibles. La compilación local se realizó con Java 25 con destino Java 21 y Node 24; una instalación completamente nueva con Java 21 aún no se ha verificado.

## Terminal 1: backend

Abre PowerShell, cambia la ruta a tu carpeta y ejecuta:

```powershell
Set-Location 'C:\GameHubEP3\backend'
$env:JAVA_HOME = 'C:\ruta\a\jdk-21'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$env:JWT_SECRET = 'ejemplo-jwt-012345678901234567890123456789'
$env:INVENTORY_SERVICE_SECRET = 'ejemplo-inventario-012345678901234567890'
$env:REGISTRATION_SERVICE_SECRET = 'ejemplo-registro-012345678901234567890123'
$env:PROMOTION_SERVICE_SECRET = 'ejemplo-promocion-01234567890123456789012'
$env:PAYMENT_SERVICE_SECRET = 'ejemplo-pago-01234567890123456789012345'
$env:SHIPPING_SERVICE_SECRET = 'ejemplo-despacho-01234567890123456789012'
$env:WARRANTY_PERIOD_DAYS = '180'
.\mvnw.cmd -v
.\mvnw.cmd -pl eureka,gateway,auth,user,category,inventory,product,promotion,payment,order,shipping,warranty,review '-DskipTests' package
if ($LASTEXITCODE -ne 0) { throw 'Falló la compilación' }
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\iniciar-local.ps1
```

El script inicia Eureka y luego los servicios. Solo compila de nuevo cuando cambie el código; al reiniciar, configura de nuevo las variables y ejecuta el último comando. Todos los secretos son ejemplos para esta demo, no claves de producción. El script comprueba su longitud y configura Eureka en `http://localhost:19640/eureka/` y CORS para `http://localhost:5173`.

## Terminal 2: frontend

```powershell
Set-Location 'C:\GameHubEP3\frontend'
npm.cmd ci
$env:VITE_API_URL = 'http://localhost:19646'
npm.cmd run dev -- --host 127.0.0.1 --port 5173
```

Abre http://localhost:5173. `npm.cmd ci` solo es necesario la primera vez o cuando cambie el lockfile. Consulta Eureka en http://localhost:19640. Swagger Product: http://localhost:19642/doc/swagger-ui.html; Order: http://localhost:19647/doc/swagger-ui.html.

## Cuentas y datos

Las bases `data/auths.mv.db` y `data/users.mv.db` contienen únicamente cuentas de demo con contraseñas BCrypt. Las bases `data/catalog-local/` contienen una copia del catálogo, categorías y stock del entorno original en el momento de preparar la entrega. Se retiraron las reservas y movimientos históricos de la copia de inventario para evitar conflictos con las órdenes nuevas. No contienen las órdenes ni las cuentas personales del entorno original.

La copia de promociones incluye el cupón de demostración `NEON10`; sus usos históricos se limpiaron en la copia y su contador parte en cero. La vigencia y las demás condiciones se conservan en la base de promoción.

| Rol | Correo | Contraseña de demostración |
|---|---|---|
| Cliente | `cliente@gamehub.com` | `contraseña` |
| Operador | `operador@gamehub.com` | `contraseña` |
| Administrador | `administrador@gamehub.com` | `contraseña` |

El registro público crea CLIENTE. El administrador entra a `/admin`; el operador entra por **Mi perfil → Gestionar despachos**. En estas bases nuevas no hay pedidos previos. Las compras de la demo modifican su propio stock, no el del proyecto original.

Las bases H2 en archivo persisten al detener los servicios. Las bases en memoria se borran al apagar el proceso. No sustituyas las bases de otra instalación sin respaldo. Las credenciales de demo y las claves de ejemplo son solo para uso local.

## Puertos y diagnóstico

Eureka 19640; Gateway 19646; Auth 19601; User 19610; Product 19642; Category 19643; Inventory 19644; Order 19647; Payment 19648; Shipping 19649; Promotion 19650; Warranty 19651; Review 19652. Notification está incluido como módulo pero no se inicia con el script habitual.

Si un puerto está ocupado, revisa qué proceso lo usa antes de iniciar. Si el catálogo falla, comprueba en Eureka que los servicios estén registrados y visita `http://localhost:19646/api/v1/products` y `/api/v1/categories`. Si el token vence, vuelve a iniciar sesión. En 403 revisa el rol; en 404 de Swagger revisa puerto y ruta. El problema conocido de descripciones de más de 255 caracteres sigue pendiente.

## Pruebas y límites

Frontend: `npm.cmd run build` y `npm.cmd test -- --single-run`. Backend: `mvnw.cmd test` con bases de prueba independientes; no ejecute las pruebas contra las bases de presentación. Desde una instalación extraída se verificaron las tres cuentas por Gateway, la entrada a los paneles y una compra que apareció pagada tras recargar. Para esa prueba se limpiaron reservas históricas de la copia de inventario; el ZIP final ya incluye esa limpieza. Despachos, reseñas y garantías no se repitieron de extremo a extremo en esta instalación.
