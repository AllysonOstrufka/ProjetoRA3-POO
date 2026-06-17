package ProjetoRA3.ModelModalidade;

import java.io.Serializable;

public class Modalidade implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String tipo;
    private String descricao;
    private int cargaHoraria;
    private Integer academiaId;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getCargaHoraria() { return cargaHoraria; }
    public void setCargaHoraria(int cargaHoraria) { this.cargaHoraria = cargaHoraria; }

    public Integer getAcademiaId() { return academiaId; }
    public void setAcademiaId(Integer academiaId) { this.academiaId = academiaId; }
}
