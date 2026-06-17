package ProjetoRA3.ModelAcademia;

public class AcademiaLuta extends SalaComercial {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String cnpj;
    private String endereco;
    private int statusAtivo = 1;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public int getStatusAtivo() { return statusAtivo; }
    public void setStatusAtivo(int statusAtivo) { this.statusAtivo = statusAtivo; }
}