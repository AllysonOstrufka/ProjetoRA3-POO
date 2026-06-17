package ProjetoRA3.ModelAcademia;

interface ICrudRepository<T extends Academia> {
    RetornoOperacao inserir(T entidade);
    RetornoOperacao alterar(T entidade);
    RetornoOperacao excluir(Integer id);
    RetornoOperacao buscarTodos();
}