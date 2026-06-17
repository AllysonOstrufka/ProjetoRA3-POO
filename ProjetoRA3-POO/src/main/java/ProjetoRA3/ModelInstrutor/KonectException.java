package ProjetoRA3.ModelInstrutor;

public class KonectException extends Exception {
    public KonectException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return "\n+------------------------------------+\n" +
                "Erro: \n" +
                super.getMessage() +
                "\n+------------------------------------+";
    }
}