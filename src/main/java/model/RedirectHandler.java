package model;

import abstarcts.BaseHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import services.ShortLinkService;

import java.io.IOException;
import java.time.Instant;

public class RedirectHandler extends BaseHandler implements HttpHandler {

    private final ShortLinkService linkService;

    public RedirectHandler(ShortLinkService linkService) {
        this.linkService = linkService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String shortCode = exchange.getRequestURI().getPath().substring(1);
        ShortLink link = linkService.getShortLink(shortCode);

        if(link == null) {
            sendResponse(exchange, 404, "Not Found");
            return;
        }

        if(!link.isActive()) {
            sendResponse(exchange, 410, "Gone");
            return;
        }

        if(link.getMaxClicks() != null && link.getClicksCount() >= link.getMaxClicks()) {
            linkService.deactivateLink(shortCode);
            linkService.addNotification(link.getUserId(), "Лимит переходов исчерпан");
            sendResponse(exchange, 410, "Gone");
            return;
        }

        if(Instant.now().isAfter(link.getExpiresAt())) {
            linkService.deactivateLink(shortCode);
            linkService.addNotification(link.getUserId(), "Время жизни ссылки истекло: ");
            sendResponse(exchange, 410, "Gone");
            return;
        }

        linkService.incrementClicks(shortCode);
        exchange.getRequestHeaders().add("Location", link.getOriginalUrl());
        sendResponse(exchange, 302, "");
    }
}
