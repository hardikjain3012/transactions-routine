@echo off
setlocal

echo Building Spring Boot project...
call mvn clean package -DskipTests
if %errorlevel% neq 0 goto mvn_failed

echo Building Docker image...
docker build -t transactions-routine-app .
if %errorlevel% neq 0 goto docker_build_failed

echo Checking for docker compose file...
if exist "docker-compose.yml" goto start_compose
if exist "compose.yaml" goto start_compose

echo No compose file found, will use direct docker run fallback.

echo Running container...

docker stop transactions-routine-app-container 2>nul
docker rm transactions-routine-app-container 2>nul

docker run -d -p 8080:8080 --name transactions-routine-app-container transactions-routine-app
if %errorlevel% neq 0 goto docker_run_failed

echo Application running on http://localhost:8080
echo Press any key to exit...
pause
goto end

:: Labels for compose and failure handling
:start_compose
echo Found compose file — starting services with docker compose (detached)...
docker compose up --build -d
if %errorlevel% neq 0 goto docker_compose_failed
echo Docker Compose started services.
echo Application services launched via docker compose. Press any key to exit...
pause
goto end

:mvn_failed
echo Maven build failed.
pause
goto end

:docker_build_failed
echo Docker build failed.
pause
goto end

:docker_run_failed
echo Docker run failed.
pause
goto end

:docker_compose_failed
echo Docker Compose failed to start services.
pause
goto end

:end
exit /b 0
