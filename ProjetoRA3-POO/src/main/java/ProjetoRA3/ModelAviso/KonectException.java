package ProjetoRA3.ModelAviso;

public class KonectException extends Exception {
    public KonectException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return "\n+------------------------------------+\n" +
                "Erro no Sistema KOnect (Avisos): \n" +
                super.getMessage() +
                "\n+------------------------------------+";
    }
}