import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '10s', target: 1000 }, // Постепенный рост нагрузки до 1000 RPS
        { duration: '30s', target: 1000 }, // Держим нагрузку
        { duration: '10s', target: 0 },    // Плавное завершение нагрузки
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% запросов должны быть быстрее 500 мс
    },
};

export default function () {
    const res = http.get('http://localhost:8080/actuator/health'); // Изменено с /health на /actuator/health
    check(res, {
        'status is 200': (r) => r.status === 200,
    });
    sleep(1); // Имитация реального поведения пользователей
}