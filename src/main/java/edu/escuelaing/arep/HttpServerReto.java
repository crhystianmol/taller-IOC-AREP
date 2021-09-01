package edu.escuelaing.arep;
import static spark.Spark.*;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;


public class HttpServerReto {

    String rutaa = "/src/main/resources/Img/";

    /**
     * Constructor de HttpServerReto
     */
    public HttpServerReto() {
        super();
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
            while ((inputLine = in.readLine()) != null) {

                if(inputLine.contains("GET")){
                    String[] ruta=inputLine.split("/");
                    String[] tipo=ruta[1].split(" ");
                    if (tipo[0].contains("html")) mostrartexto(rutaa+tipo[0],clientSocket.getOutputStream(),"html");
                    if (tipo[0].contains("js"))mostrartexto(rutaa+tipo[0],clientSocket.getOutputStream(),"json");
                    if (tipo[0].contains("jpg"))mostrarImagen(rutaa+tipo[0], clientSocket.getOutputStream());



                }
                if (!in.ready()) {break; }
            }
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    /**
     * Metodo que muestra el texto en la pagina
     * @param string la ruta donde encontraremos el archivo
     * @param outputStream metodo de salida
     * @param tipo tipo de archivo
     */
    private void mostrartexto(String string, OutputStream outputStream, String tipo) {
        try {
            System.out.println(System.getProperty("user.dir")+string);
            BufferedReader in = new BufferedReader(
                    new FileReader(System.getProperty("user.dir")+ string));
            String line;
            String out = "";

            while ((line = in.readLine()) != null) {
                out = out + line;
            }
            outputStream.write(("HTTP/1.1 201 OK\r\n"
                    + "Content-Type: text/"+tipo+ ";"
                    + "charset=\"UTF-8\" \r\n"
                    + "\r\n" + out).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Metodo MostrarImagen muestra la imagen en el servidor
     * @param string ruta de la imagen
     * @param outputStream metodo salida
     */
    private void mostrarImagen(String string, OutputStream outputStream) {
        try {
            BufferedImage imagen = ImageIO.read(new File(System.getProperty("user.dir") + string));
            ByteArrayOutputStream ArrBytes = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(outputStream);
            ImageIO.write(imagen, "JPG", ArrBytes);
            out.writeBytes("HTTP/1.1 200 OK \r\n"
                    + "Content-Type: image/png \r\n"
                    + "\r\n");
            out.write(ArrBytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 35000;
    }





}