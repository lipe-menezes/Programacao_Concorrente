
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Biblioteca {
    private List<Livro> livros;
    private final String filePath = "src/main/resources/livro.json";
    private final ObjectMapper mapper = new ObjectMapper();

    public Biblioteca() throws IOException {
        livros = carregarLivros();
    }

    @SuppressWarnings("unchecked")
    private List<Livro> carregarLivros() throws IOException {
        return (List<Livro>) mapper.readValue(new File(filePath), new TypeReference<List<Livro>>() {});
    }

    private void salvarLivros() throws IOException {
        mapper.writeValue(new File(filePath), livros);
    }

    public List<Livro> listarLivros() {
        return livros;
    }

    public void cadastrarLivro(Livro livro) throws IOException {
        livros.add(livro);
        salvarLivros();
    }

    public void alugarLivro(String titulo) throws IOException {
        for (Livro livro : livros) {
            if (livro.getTitulo().equalsIgnoreCase(titulo) && livro.getExemplares() > 0) {
                livro.setExemplares(livro.getExemplares() - 1);
                salvarLivros();
                return;
            }
        }
    }

    public void devolverLivro(String titulo) throws IOException {
        for (Livro livro : livros) {
            if (livro.getTitulo().equalsIgnoreCase(titulo)) {
                livro.setExemplares(livro.getExemplares() + 1);
                salvarLivros();
                return;
            }
        }
    }
}
