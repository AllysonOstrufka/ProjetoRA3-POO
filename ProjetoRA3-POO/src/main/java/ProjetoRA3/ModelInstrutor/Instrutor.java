package ProjetoRA3.ModelInstrutor;

public class Instrutor extends Pessoa {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String cpf;
    private String telefone;
    private int statusAtivo = 1;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public int getStatusAtivo() { return statusAtivo; }
    public void setStatusAtivo(int statusAtivo) { this.statusAtivo = statusAtivo; }
}