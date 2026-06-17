package ProjetoRA3.ModelAviso;

interface ICrudRepository<T extends EntidadeBase> {
    RetornoOperacao inserir(T entidade);
    RetornoOperacao alterar(T entidade);
    RetornoOperacao excluir(Integer id);
    RetornoOperacao buscarTodos();
}