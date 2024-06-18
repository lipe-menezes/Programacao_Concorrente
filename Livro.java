

public class Livro {
    private String titulo;
    private String autor;
    private String genero;
    private int exemplares;

    public Livro(
        @JsonProperty("titulo") String titulo,
        @JsonProperty("autor") String autor,
        @JsonProperty("genero") String genero,
        @JsonProperty("exemplares") int exemplares
    ) {
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.exemplares = exemplares;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getExemplares() {
        return exemplares;
    }

    public void setExemplares(int exemplares) {
        this.exemplares = exemplares;
    }
}


