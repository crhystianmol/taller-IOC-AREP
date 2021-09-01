package edu.escuelaing.arep;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HttpServer {
    private Map<String, Handler<String>> handlers = new HashMap();

    /**
     * metodo constructor de la clase HttpServer
     */
    public HttpServer() {
        super();
    }

    /**
     * metodo de registro de handler
     * @param h el handler
     * @param prefix prefijo
     */
    public void registerHandler(Handler<String> h, String prefix) {
        handlers.put(prefix,h);

    }

    /**
     * Metodo StarServer que inicializa los sockets e inicia el servidor
     * @throws IOException excepcion de la clase ServerSocket
     */
    public void startServer() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(getPort());
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            boolean pathRead = false;
            String path = "";
            while ((inputLine = in.readLine()) != null) {

                if (!pathRead) {
                    path= inputLine.split( "")[1];
                    System.out.println("path read: " + path);
                    pathRead=true;

                }
                System.out.println("Recib: " + inputLine);
                if (!in.ready()) {break; }
            }

            String prefix = "/Apps";
            String sufix = "/hello";
            if (handlers.containsKey(prefix)) {
                out.println(getDefaultokOutput() + handlers.get(prefix).handle(sufix,null,null));
            }
            else {
                out.println(getDefaultokOutput());
            }

            out.println(getDefaultokOutput());
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    /**
     * pagina httml
     * @return html
     */
    private String getDefaultokOutput() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<title>Title of the document</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Mi propio mensaje</h1>\n" +
                "</body>\n" +
                "</html>\n";
    }

    /**
     * metodo getPort que retorna el puerto a usar
     * @return el puerto
     */
    private int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 35000;
    }
}
