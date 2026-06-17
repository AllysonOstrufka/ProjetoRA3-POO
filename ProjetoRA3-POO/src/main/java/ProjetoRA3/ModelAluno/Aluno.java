package ProjetoRA3.ModelAluno;


public class Aluno extends Pessoa {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String cpf;
    private String corDeFaixa;
    private int statusAtivo = 1;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getCorDeFaixa() { return corDeFaixa; }

    public void setCorDeFaixa(String corDeFaixa) { this.corDeFaixa = corDeFaixa; }

    public int getStatusAtivo() { return statusAtivo; }
    public void setStatusAtivo(int statusAtivo) { this.statusAtivo = statusAtivo; }
}
