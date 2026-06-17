package ProjetoRA3;
import java.io.Serializable;

interface ICrudRepository<T extends Academia> {
    RetornoOperacao inserir(T entidade);
    RetornoOperacao alterar(T entidade);
    RetornoOperacao excluir(Integer id);
    RetornoOperacao buscarTodos();
}