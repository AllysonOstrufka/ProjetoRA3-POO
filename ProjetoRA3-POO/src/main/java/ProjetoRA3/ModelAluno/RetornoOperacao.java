package ProjetoRA3.ModelAluno;

public class RetornoOperacao {
    private String status;
    private String mensagem;
    private Object data;

    public RetornoOperacao(String status, String mensagem, Object data) {
        this.status = status;
        this.mensagem = mensagem;
        this.data = data;
    }

    public String getStatus() { return status; }
    public String getMensagem() { return mensagem; }
    public Object getData() { return data; }
}
