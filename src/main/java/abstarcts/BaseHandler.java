package abstarcts;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseHandler {
    protected void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);

        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(response.getBytes());
        }
    }

    protected Map<String,String> parseQuery(String query) {
        Map<String,String> params = new HashMap<>();

        if(query == null)
            return params;

        for(String param : query.split("&")) {
            String[] pair = param.split("=");

            if(pair.length > 1) {
                params.put(pair[0], pair[1]);
            }
        }

        return params;
    }

    protected String getCookie(HttpExchange exchange, String name) {
        List<String> cookies = exchange.getRequestHeaders().get("Cookie");

        if (cookies == null)
            return null;

        for (String cookie : cookies) {
            String[] parts = cookie.split("=");

            if (parts.length == 2 && parts[0].equals(name))
                return parts[1];
        }

        return null;
    }
}
