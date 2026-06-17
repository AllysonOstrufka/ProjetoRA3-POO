package ProjetoRA3.ModelAviso;

public class Aviso extends EntidadeBase {
    private static final long serialVersionUID = 1L;

    private String titulo;
    private String dataPublicacao;
    private String mensagem;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDataPublicacao() { return dataPublicacao; }
    public void setDataPublicacao(String dataPublicacao) { this.dataPublicacao = dataPublicacao; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}