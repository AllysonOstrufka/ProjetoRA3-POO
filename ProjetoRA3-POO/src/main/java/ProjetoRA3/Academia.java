package ProjetoRA3;

import java.io.Serializable;

abstract class Academia implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Integer id;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
}