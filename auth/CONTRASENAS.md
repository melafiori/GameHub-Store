# Tratamiento de credenciales antiguas

Las credenciales nuevas se almacenan con BCrypt. Las credenciales antiguas que no tienen un hash BCrypt se rechazan en el inicio de sesión; no se comparan con la contraseña recibida ni se convierten automáticamente. Es necesario implementar un flujo de recuperación verificado antes de permitir que sus propietarios establezcan una contraseña nueva. Hasta entonces esas cuentas no podrán iniciar sesión. No se deben publicar ni copiar los valores antiguos en texto plano.

Auth firma JWT con HS256 usando `JWT_SECRET`, de al menos 32 bytes, y una vigencia de una hora. El Gateway mantiene bloqueadas las rutas de Auth mientras se implementan la validación del token y la coordinación del registro. No exponer Auth directamente a clientes.
