package model;

import abstarcts.BaseHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import services.ShortLinkService;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class HandlerCreator extends BaseHandler implements HttpHandler {

    private final ShortLinkService linkService;

    public HandlerCreator(ShortLinkService linkService) {
        this.linkService = linkService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
        String originalURL = params.get("url");

        if (originalURL == null) {
            sendResponse(exchange, 400, "URL is required");
            return;
        }

        Integer maxClicks = null;

        try {
            if (params.containsKey("maxClicks")) {
                maxClicks = Integer.parseInt(params.get("maxClicks"));
            }
        } catch (NumberFormatException exception) {
            sendResponse(exchange, 400, "Invalid clicks count");
            return;
        }

        UUID userId;
        String userIdCookie = getCookie(exchange, "userId");

        if (userIdCookie != null) {
            try {
                userId = UUID.fromString(userIdCookie);

                if (!linkService.getUsers().containsKey(userId)) {
                    userId = UUID.randomUUID();
                    linkService.createUser(userId);
                }
            } catch (IllegalArgumentException exception) {
                userId = UUID.randomUUID();
                linkService.createUser(userId);
            }
        } else {
            userId = UUID.randomUUID();
            linkService.createUser(userId);
        }

        String shortCode = linkService.createShortLink(userId, originalURL, maxClicks);
        String response = String.format("{\"shortUrl\": \"http://localhost:8080/%s\", \"userId\": \"%s\"}", shortCode, userId);

        exchange.getResponseHeaders().add("Set-Cookie", "userId=" + userId);
        sendResponse(exchange, 200, response);
    }
}
