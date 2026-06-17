package ProjetoRA3.ModelInstrutor;

interface ICrudRepository<T extends Pessoa> {
    RetornoOperacao inserir(T entidade);
    RetornoOperacao alterar(T entidade);
    RetornoOperacao excluir(Integer id);
    RetornoOperacao buscarTodos();
}