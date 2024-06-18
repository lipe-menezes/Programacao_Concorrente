import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServidorBiblioteca {
    private static final int PORT = 12345;
    private Biblioteca biblioteca;
    private ObjectMapper mapper;

    public ServidorBiblioteca() throws IOException {
        biblioteca = new Biblioteca();
        mapper = new ObjectMapper();
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado na porta " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    String comando = in.readLine();
                    processarComando(comando, out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processarComando(String comando, PrintWriter out) throws IOException {
        String[] partes = comando.split(";", 2);
        String acao = partes[0];

        switch (acao) {
            case "listar":
                List<Livro> livros = biblioteca.listarLivros();
                out.println(((Object) mapper).writeValueAsString(livros));
                break;

            case "cadastrar":
                Livro livro = mapper.readValue(partes[1], Livro.class);
                biblioteca.cadastrarLivro(livro);
                out.println("Livro cadastrado com sucesso");
                break;

            case "alugar":
                biblioteca.alugarLivro(partes[1]);
                out.println("Livro alugado com sucesso");
                break;

            case "devolver":
                biblioteca.devolverLivro(partes[1]);
                out.println("Livro devolvido com sucesso");
                break;

            default:
                out.println("Comando não reconhecido");
                break;
        }
    }

    public static void main(String[] args) throws IOException {
        new ServidorBiblioteca().iniciar();
    }
}


