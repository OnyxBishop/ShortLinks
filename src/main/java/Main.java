import services.ShortLinkService;
import view.ConsoleUI;

import java.io.IOException;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws IOException {
        ShortLinkService linkService = new ShortLinkService();
        UUID userId;

        if(!ConsoleUI.tryLoadUser()){
            System.out.println("Юзер создан");
            userId = ConsoleUI.createUser();
        }
        else{
            System.out.println("Найден юзер");
            userId = ConsoleUI.loadUserId();
        }


        ConsoleUI ui = new ConsoleUI(linkService, userId);
        ui.start();
    }

    //Using HttpServer
    /*public static void main(String[] args) throws IOException {
        ShortLinkService linkService = new ShortLinkService();
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);

        httpServer.createContext("/create", new HandlerCreator(linkService));
        httpServer.createContext("/", new RedirectHandler(linkService));
        httpServer.setExecutor(null);
        httpServer.start();
    }*/
}
