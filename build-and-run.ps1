# Установка кодировки для корректного отображения вывода
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "Start Build and Run" -ForegroundColor Green

# Сборка frontend
Write-Host "Build frontend..." -ForegroundColor Cyan
Set-Location -Path "frontend"
npm install
npm run build
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error when build frontend" -ForegroundColor Red
    exit 1
}
Set-Location -Path ".."

# Сборка backend
Write-Host "Build backend..." -ForegroundColor Cyan
.\gradlew.bat clean build -x test

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error when build backend" -ForegroundColor Red
    exit 1
}

# Запуск Docker Compose
Write-Host "Start Docker Compose..." -ForegroundColor Cyan
docker compose down
docker compose up --build -d

Write-Host "Build and run completed successfully!" -ForegroundColor Green 