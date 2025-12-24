package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;

/**
 * Сервис для управления ссылками.
 * Предоставляет методы для создания короткой ссылки и возвращения оригинальной ссылки.
 */
public interface UrlService {

    /**
     * Пользователь создаёт короткую ссылку
     * <p>
     * Условия:
     * <ul>
     *     <li>Пользователь отправляет оригинальную ссылку в сервис</li>
     *     <li>Ссылка не должна быть пустая и должна быть валидная —
     *     в противном случае выбрасывается {@code MethodArgumentNotValidException}.</li>
     * </ul>
     *
     * @param urlDto объект {@link UrlDto}, содержащий информацию о полученной ссылке
     * @return объект {@link UrlDto}, содержащий информацию о полученной и возвращенной ссылках
     */
    UrlDto getShortUrl(UrlDto urlDto);

    /**
     * Пользователь возвращает оригинальную ссылку
     * <p>
     * Условия:
     * <ul>
     *     <li>Отсутствуют</li>
     * </ul>
     *
     * @param hash — короткая ссылка
     * @return оригинальная ссылка
     */
    String getOriginalUrl(String hash);
}
