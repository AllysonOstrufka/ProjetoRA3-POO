package ProjetoRA3.ModelRegras;

import java.io.Serializable;

public class Regras implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String descricao;
    private Integer tempoMinimo;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getTempoMinimo() { return tempoMinimo; }
    public void setTempoMinimo(Integer tempoMinimo) { this.tempoMinimo = tempoMinimo; }
}
