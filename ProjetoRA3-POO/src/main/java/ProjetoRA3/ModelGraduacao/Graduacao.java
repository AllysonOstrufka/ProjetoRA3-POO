package ProjetoRA3.ModelGraduacao;

import java.io.Serializable;

abstract class Graduacao implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Integer id;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
}
