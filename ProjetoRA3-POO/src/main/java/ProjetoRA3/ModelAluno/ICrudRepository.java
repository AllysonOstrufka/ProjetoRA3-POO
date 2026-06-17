package ProjetoRA3.ModelAluno;

interface ICrudRepository<T extends Pessoa> {
    RetornoOperacao inserir(T entidade);
    RetornoOperacao alterar(T entidade);
    RetornoOperacao excluir(Integer id);
    RetornoOperacao buscarTodos();
}
