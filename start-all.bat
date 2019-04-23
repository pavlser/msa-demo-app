@echo off

echo.
echo ======================================================
echo Build all maven artifacts
echo ======================================================

call mvn clean install

echo Start applications
echo ======================================================
echo.

echo 1 - Start Config server
start "CONFIG SERVER" "%comspec%" /c "java -jar %cd%/config-server/target/config-server-1.0.0-SNAPSHOT.jar & timeout -1 /nobreak"
echo     Wait 12 sec for starting config server...
sleep 12

echo 2 - Start Eureka discovery server
start "EUREKA SERVER" "%comspec%" /c "java -jar %cd%/discovery-service/target/discovery-service-1.0.0-SNAPSHOT.jar & timeout -1 /nobreak"
sleep 20

echo 3 - Start Auth server
start "AUTH SERVER" "%comspec%" /c "java -jar %cd%/auth-server/target/auth-server-1.0.0-SNAPSHOT.jar & timeout -1 /nobreak"
sleep 20

echo 4 - Start Account service
start "ACCOUNT SERVICE" "%comspec%" /c "java -jar %cd%/account-service/target/account-service-1.0.0-SNAPSHOT.jar & timeout -1 /nobreak"
sleep 20

echo 5 - Start Bank service
start "BANK SERVICE" "%comspec%" /c "java -jar %cd%/bank-information-service/target/bank-info-service-1.0.0-SNAPSHOT.jar & timeout -1 /nobreak"
sleep 20

echo 6 - Start Gateway server
start "GATEWAY SERVER" "%comspec%" /c "java -jar %cd%/gateway-server/target/gateway-server-1.0.0-SNAPSHOT.jar & timeout -1 /nobreak"
sleep 20

echo 7 - Start Admin monitoring server
start "ADMIN SERVER" "%comspec%" /c "java -jar %cd%/admin-server/target/admin-server-1.0.0-SNAPSHOT.jar & timeout -1 /nobreak"
sleep 40

echo 8 - Run integration tests
start "TESTING" "%comspec%" /c "cd testing-application && mvn clean package && pause & timeout -1 /nobreak"

echo.

pause
