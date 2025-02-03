package services;

import model.ShortLink;
import model.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class ShortLinkService {
    private final Map<String, ShortLink> shortLinks = new ConcurrentHashMap<>();
    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    //if Using_HttpServer
    //private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /*public ShortLinkService() {
        scheduler.scheduleAtFixedRate(this::cleanupExpiredLinks, 0, 1, TimeUnit.MINUTES);
    }*/

    //region Setters
    //endregion
    //region Getters
    public ShortLink getShortLink(String shortCode) {
        return shortLinks.get(shortCode);
    }

    public List<String> getNotifications(UUID userId) {
        User user = users.get(userId);
        return user != null ? user.getNotifications() : Collections.emptyList();
    }

    public Map<UUID, User> getUsers() {
        return this.users;
    }
    //endregion

    public String createShortLink(UUID userId, String originalUrl, Integer maxClicks) {
        User user = users.get(userId);

        if (user == null)
            throw new IllegalArgumentException("User not found");

        String shortCode;

        do {
            shortCode = generateShortCode();
        } while (shortLinks.containsKey(shortCode));

        Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);
        ShortLink link = new ShortLink(shortCode, originalUrl, userId, Instant.now(), expiresAt, maxClicks, 0, true);

        shortLinks.put(shortCode, link);
        user.getLinks().add(link);
        return shortCode;
    }

    public void createUser(UUID userId) {
        users.putIfAbsent(userId, new User(userId));
    }

    public void incrementClicks(String shortCode) {
        ShortLink link = shortLinks.get(shortCode);

        if (link != null)
            link.setClicksCount(link.getClicksCount() + 1);
    }

    public void deactivateLink(String shortCode) {
        ShortLink link = shortLinks.get(shortCode);
        if (link != null) link.setActive(false);
    }

    public void addNotification(UUID userId, String message) {
        User user = users.get(userId);
        if (user != null) user.getNotifications().add(message);
    }

    private String generateShortCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = ThreadLocalRandom.current().nextInt(chars.length());
            stringBuilder.append(chars.charAt(index));
        }

        return stringBuilder.toString();
    }

    private void cleanupExpiredLinks() {
        Instant now = Instant.now();
        shortLinks.entrySet().removeIf(entry ->
                entry.getValue().getExpiresAt().isBefore(now)
        );
    }
}
