package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private final UUID userId;
    private final List<ShortLink> links = new ArrayList<>();
    private final List<String> notifications = new ArrayList<>();

    public User(UUID userId) {
        this.userId = userId;
    }

    //region Setters
    //endregion
    //region Getters
    public List<String> getNotifications() {
        return notifications;
    }

    public List<ShortLink> getLinks() {
        return links;
    }
    //endregion
}
