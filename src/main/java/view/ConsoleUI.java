package view;

import model.ShortLink;
import services.ShortLinkService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ConsoleUI {
    private static final String userIdFile = "userId.txt";
    private final ShortLinkService linkService;
    private final UUID userId;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(ShortLinkService linkService, UUID userId) {
        this.linkService = linkService;
        this.userId = userId;
    }

    public void start() {
        linkService.createUser(userId);
        var isRunning = true;

        while (isRunning) {
            printMenu();
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "1" -> createLink();
                case "2" -> openLink();
                case "3" -> showNotifications();
                case "4" -> isRunning = false;
                default -> System.out.println("Неверная команда");
            }
        }

        System.out.println("Всего доброго!");
    }

    public static boolean tryLoadUser() {
        Path path = Paths.get(userIdFile);
        return Files.exists(path);
    }

    public static UUID loadUserId() throws IOException {
        Path path = Paths.get(userIdFile);
        return UUID.fromString(Files.readString(path).trim());
    }

    public static UUID createUser() {
        UUID newUserId = UUID.randomUUID();

        try {
            Files.writeString(Paths.get(userIdFile), newUserId.toString());
        } catch (IOException e) {
            System.err.println("Не удалось сохранить user ID: " + e.getMessage());
        }

        return newUserId;
    }

    private void printMenu() {
        System.out.println("\n1. Создать короткую ссылку");
        System.out.println("2. Перейти по короткой ссылке");
        System.out.println("3. Показать уведомления");
        System.out.println("4. Выход");
        System.out.print("> ");
    }

    private void createLink() {
        System.out.print("Введите оригинальный URL: ");
        String url = scanner.nextLine().trim();

        System.out.print("Лимит переходов (оставьте пустым, если без лимита): ");
        String maxClicksInput = scanner.nextLine().trim();

        Integer maxClicks = parseMaxClicks(maxClicksInput);
        if (maxClicks == null && !maxClicksInput.isEmpty()) return;

        String shortCode = linkService.createShortLink(userId, url, maxClicks);
        System.out.println("Короткая ссылка: click.ru/" + shortCode);
    }

    private Integer parseMaxClicks(String input) {
        if (input.isEmpty()) return null;
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат числа");
            return null;
        }
    }

    private void checkLinkValidity(String shortCode, ShortLink link) {
        if (!link.isActive()) {
            System.out.println("Ссылка деактивирована");
            return;
        }

        if (isLinkExpired(shortCode, link)) return;
        if (isClicksLimitReached(shortCode, link)) return;

        linkService.incrementClicks(shortCode);
        System.out.println("Переход успешен! Текущее количество переходов: " + (link.getClicksCount() + 1));
    }

    private boolean isLinkExpired(String shortCode, ShortLink link) {
        if (link.getExpiresAt().isBefore(Instant.now())) {
            linkService.deactivateLink(shortCode);
            linkService.addNotification(userId, "Ссылка " + shortCode + " истекла");
            System.out.println("Ссылка устарела");
            return true;
        }
        return false;
    }

    private boolean isClicksLimitReached(String shortCode, ShortLink link) {
        if (link.getMaxClicks() != null && link.getClicksCount() >= link.getMaxClicks()) {
            linkService.deactivateLink(shortCode);
            linkService.addNotification(userId, "Лимит переходов по ссылке " + shortCode + " исчерпан");
            System.out.println("Лимит переходов исчерпан");
            return true;
        }
        return false;
    }

    private void openLink() {
        System.out.print("Введите короткий код: ");
        String shortCode = scanner.nextLine().trim();

        ShortLink link = linkService.getShortLink(shortCode);

        if (link == null) {
            System.out.println("Ссылка не найдена");
            return;
        }

        checkLinkValidity(shortCode, link);
    }

    private void showNotifications() {
        List<String> notifications = linkService.getNotifications(userId);

        if (notifications.isEmpty()) {
            System.out.println("Уведомлений нет");
            return;
        }

        System.out.println("Уведомления:");
        notifications.forEach(System.out::println);
    }
}
