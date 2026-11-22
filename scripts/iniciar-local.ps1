param([switch]$UsarEurekaExistente)
$ErrorActionPreference='Stop'
$root=Split-Path -Parent $PSScriptRoot
Set-Location $root
foreach($name in 'JWT_SECRET','INVENTORY_SERVICE_SECRET','REGISTRATION_SERVICE_SECRET','PROMOTION_SERVICE_SECRET','PAYMENT_SERVICE_SECRET','SHIPPING_SERVICE_SECRET') {
  $value=[Environment]::GetEnvironmentVariable($name)
  if (-not $value -or [Text.Encoding]::UTF8.GetByteCount($value) -lt 32) { throw "$name debe contener al menos 32 bytes" }
}
if (-not $env:JAVA_HOME -or -not (Test-Path (Join-Path $env:JAVA_HOME 'bin/java.exe'))) { throw 'Configure JAVA_HOME con un JDK 21 o superior' }
$env:EUREKA_CLIENT_SERVICEURL_DEFAULTZONE='http://localhost:19640/eureka/'
$env:EUREKA_INSTANCE_PREFER_IP_ADDRESS='true'
$env:EUREKA_CLIENT_REGISTRY_FETCH_INTERVAL_SECONDS='3'
$env:FRONTEND_ORIGIN='http://localhost:5173'
if (-not $env:WARRANTY_PERIOD_DAYS) { $env:WARRANTY_PERIOD_DAYS='180' }
$services=@()
if (-not $UsarEurekaExistente) {$services+=,@('eureka',19640,'')}
$services+=@(@('category',19643,'jdbc:h2:file:./data/catalog-local/category'),@('inventory',19644,'jdbc:h2:file:./data/catalog-local/inventory'),@('product',19642,'jdbc:h2:file:./data/catalog-local/product'),@('user',19610,'jdbc:h2:file:./data/users'),@('auth',19601,'jdbc:h2:file:./data/auths'),@('promotion',19650,'jdbc:h2:file:./data/promotions'),@('payment',19648,'jdbc:h2:file:./data/payment'),@('order',19647,'jdbc:h2:file:./data/order'),@('shipping',19649,'jdbc:h2:file:./data/shipping'),@('warranty',19651,'jdbc:h2:file:./data/warranty'),@('review',19652,'jdbc:h2:file:./data/reviews'),@('gateway',19646,''))
New-Item -ItemType Directory -Force target/local-logs | Out-Null
foreach ($service in $services) {
 $name=$service[0]; $port=$service[1]; $jdbc=$service[2]
 $test=New-Object Net.Sockets.TcpClient
 try { $test.Connect('127.0.0.1',[int]$port); throw "Puerto $port ya ocupado; no se inicia $name" } catch [Net.Sockets.SocketException] {} finally { $test.Dispose() }
 $env:SERVER_PORT="$port"
 if ($jdbc) { $env:SPRING_DATASOURCE_URL=$jdbc } else { Remove-Item Env:SPRING_DATASOURCE_URL -ErrorAction SilentlyContinue }
 $jar=Join-Path $root "$name/target/$name-0.0.1-SNAPSHOT.jar"
 if (-not (Test-Path $jar)) {throw "Falta compilar $jar"}
 $p=Start-Process -FilePath (Join-Path $env:JAVA_HOME 'bin/java.exe') -ArgumentList @('-Xmx160m','-XX:MaxMetaspaceSize=160m','-XX:ActiveProcessorCount=2','-jar',"`"$jar`"") -WorkingDirectory $root -PassThru -WindowStyle Hidden -RedirectStandardOutput "target/local-logs/$name.log" -RedirectStandardError "target/local-logs/$name.err.log"
 Add-Content target/local-logs/pids.txt "$name $($p.Id)"
 $ready=$false
 for($i=0;$i -lt 90;$i++){if($p.HasExited){break};$client=New-Object Net.Sockets.TcpClient;try {$client.Connect('127.0.0.1',[int]$port);$ready=$true;break}catch{}finally{$client.Dispose()};Start-Sleep -Seconds 1}
 if (-not $ready) { throw "No arranco $name en $port; revise target/local-logs/$name.log" }
 Write-Output "$name : $port (PID $($p.Id))"
}
