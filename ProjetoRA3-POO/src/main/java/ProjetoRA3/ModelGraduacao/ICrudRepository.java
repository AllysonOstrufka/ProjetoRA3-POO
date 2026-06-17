package ProjetoRA3.ModelGraduacao;

interface ICrudRepository<T extends Graduacao> {
    RetornoOperacao inserir(T entidade);
    RetornoOperacao alterar(T entidade);
    RetornoOperacao excluir(Integer id);
    RetornoOperacao buscarTodos();
}