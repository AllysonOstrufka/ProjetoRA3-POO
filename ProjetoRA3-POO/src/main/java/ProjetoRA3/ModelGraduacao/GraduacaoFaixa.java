package ProjetoRA3.ModelGraduacao;

public class GraduacaoFaixa extends Graduacao {
    private static final long serialVersionUID = 1L;

    private String corFaixa;
    private int hierarquia;
    private int tempoMinimoMeses;

    public String getCorFaixa() { return corFaixa; }
    public void setCorFaixa(String corFaixa) { this.corFaixa = corFaixa; }

    public int getHierarquia() { return hierarquia; }
    public void setHierarquia(int hierarquia) { this.hierarquia = hierarquia; }

    public int getTempoMinimoMeses() { return tempoMinimoMeses; }
    public void setTempoMinimoMeses(int tempoMinimoMeses) { this.tempoMinimoMeses = tempoMinimoMeses; }
}