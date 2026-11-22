// Solo para una instancia local con H2 en memoria y consola habilitada.
const gateway = process.env.LOCAL_GATEWAY_URL || 'http://localhost:19314';
const auth = process.env.LOCAL_AUTH_URL || 'http://localhost:19301';
const user = process.env.LOCAL_USER_URL || 'http://localhost:19310';
const userJdbc = process.env.LOCAL_USER_JDBC || 'jdbc:h2:mem:usership';
const jdbc = process.env.LOCAL_AUTH_JDBC || 'jdbc:h2:mem:authship';
const password = process.env.LOCAL_OPERATOR_PASSWORD;
const email = 'operador.local@example.cl';
for (const url of [gateway, auth, user]) {
  const parsed = new URL(url);
  if (parsed.protocol !== 'http:' || !['localhost', '127.0.0.1', '[::1]'].includes(parsed.hostname)) throw Error('Solo se permite localhost');
}
if (![jdbc, userJdbc].every(value => /^jdbc:h2:mem:[a-zA-Z0-9_]+$/.test(value))) throw Error('La base debe ser H2 en memoria');
if (!password || password.length < 8) throw Error('Configure LOCAL_OPERATOR_PASSWORD con al menos 8 caracteres');
const body = { nombre: 'Operador local', rut: '19000000-1', email, telefono: '912345678', password,
  direccion: { calle: 'Prueba local', numero: 1, comuna: 'Santiago', ciudad: 'Santiago' } };
const registration = await fetch(gateway + '/api/v1/auth/register', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) });
if (registration.status === 409) {
  const login = await fetch(gateway + '/api/v1/auth/login', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email, password }) });
  if (!login.ok) throw Error('Ya existe la cuenta local con otras credenciales');
} else if (registration.status !== 201) throw Error('Registro local: HTTP ' + registration.status + ' ' + await registration.text());
const root = auth + '/h2-console/';
let html = await (await fetch(root)).text();
const id = html.match(/jsessionid=([a-f0-9]+)/)?.[1];
if (!id) throw Error('Consola H2 no disponible');
const form = { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' } };
await fetch(root + 'login.do?jsessionid=' + id, { ...form, body: new URLSearchParams({ driver: 'org.h2.Driver', url: jdbc, user: 'sa', password: '' }) });
const sql = "UPDATE AUTH SET ROL='OPERADOR' WHERE EMAIL='operador.local@example.cl' AND HASH_PSWD LIKE '$2%' AND ESTADO='ACTIVO'";
html = await (await fetch(root + 'query.do?jsessionid=' + id, { ...form, body: new URLSearchParams({ sql }) })).text();
html = await (await fetch(root + 'query.do?jsessionid=' + id, { ...form, body: new URLSearchParams({ sql: "SELECT COUNT(*) AS N FROM AUTH WHERE EMAIL='operador.local@example.cl' AND ROL='OPERADOR' AND HASH_PSWD LIKE '$2%' AND ESTADO='ACTIVO'" }) })).text();
if (!/<td[^>]*>1<\/td>/i.test(html)) throw Error('No se encontro una credencial operadora BCrypt; revise la base H2');
const userRoot = user + '/h2-console/';
html = await (await fetch(userRoot)).text();
const userId = html.match(/jsessionid=([a-f0-9]+)/)?.[1];
if (!userId) throw Error('Consola H2 de User no disponible');
await fetch(userRoot + 'login.do?jsessionid=' + userId, { ...form, body: new URLSearchParams({ driver: 'org.h2.Driver', url: userJdbc, user: 'sa', password: 'sa' }) });
await fetch(userRoot + 'query.do?jsessionid=' + userId, { ...form, body: new URLSearchParams({ sql: "UPDATE USERS SET USER_ROL='OPERADOR' WHERE EMAIL='operador.local@example.cl'" }) });
html = await (await fetch(userRoot + 'query.do?jsessionid=' + userId, { ...form, body: new URLSearchParams({ sql: "SELECT COUNT(*) FROM USERS WHERE EMAIL='operador.local@example.cl' AND USER_ROL='OPERADOR'" }) })).text();
if (!/<td[^>]*>1<\/td>/i.test(html)) throw Error('No se actualizo el perfil del operador');
console.log('Cuenta local operadora lista: ' + email);
