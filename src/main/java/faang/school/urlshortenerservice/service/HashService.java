package faang.school.urlshortenerservice.service;

public interface HashService {
    public void performCronTaskTransactional(int createdBeforeMonths);
}
