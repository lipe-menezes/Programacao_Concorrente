import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteBiblioteca {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String userInput;

            System.out.println("Digite um comando (listar, cadastrar, alugar, devolver): ");
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("Resposta do servidor: " + in.readLine());
                System.out.println("Digite um comando (listar, cadastrar, alugar, devolver): ");
            }
        }
    }
}



