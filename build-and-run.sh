#!/bin/bash

# Установка кодировки для корректного отображения вывода
export LANG=C.UTF-8

echo "Start Build and Run"

# Сборка frontend
echo "Build frontend..."
cd frontend
npm install
npm run build
if [ $? -ne 0 ]; then
    echo "Error when build frontend"
    exit 1
fi
cd ..

# Сборка backend
echo "Build backend..."
./gradlew clean build -x test
if [ $? -ne 0 ]; then
    echo "Error when build backend"
    exit 1
fi

# Запуск Docker Compose
echo "Start Docker Compose..."
docker compose down
docker compose up --build -d

echo "Build and run completed successfully!" 