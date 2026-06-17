package ProjetoRA3.ModelAluno;

import java.io.*;
import java.util.ArrayList;

public class AlunoRepository implements ICrudRepository<Aluno> {

    private Aluno[] bancoDeDadosTemporario = new Aluno[100];
    private int quantidadeRegistros = 0;
    private int geradorDeId = 1;

    private String ARQUIVO_DADOS = "Pessoas.dat";

    public AlunoRepository() {
        carregarDados();
    }

    private void salvarDados() {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = new FileOutputStream(ARQUIVO_DADOS);
            oos = new ObjectOutputStream(fos);

            oos.writeObject(bancoDeDadosTemporario);
            oos.writeInt(quantidadeRegistros);
            oos.writeInt(geradorDeId);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (oos != null) oos.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void carregarDados() {
        File arquivo = new File(ARQUIVO_DADOS);
        if (!arquivo.exists()) {
            return;
        }

        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {
            fis = new FileInputStream(arquivo);
            ois = new ObjectInputStream(fis);

            bancoDeDadosTemporario = (Aluno[]) ois.readObject();
            quantidadeRegistros = ois.readInt();
            geradorDeId = ois.readInt();

        } catch (Exception e) {
            System.out.println("Arquivo de dados antigo/incompatível encontrado. Deletando para criar um novo...");

            try {
                if (ois != null) ois.close();
                if (fis != null) fis.close();
            } catch (IOException ex) {

            }


            arquivo.delete();

        } finally {
            try {
                if (ois != null) ois.close();
                if (fis != null) fis.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public RetornoOperacao inserir(Aluno Pessoa) {
        if (Pessoa.getNome() == null || Pessoa.getNome().trim().isEmpty()) {
            return new RetornoOperacao("nok", "Nome da Pessoa é obrigatório", null);
        }
        if (quantidadeRegistros >= bancoDeDadosTemporario.length) {
            return new RetornoOperacao("nok", "O banco de dados temporário está cheio!", null);
        }

        Pessoa.setId(geradorDeId++);
        bancoDeDadosTemporario[quantidadeRegistros] = Pessoa;
        quantidadeRegistros++;

        salvarDados();

        return new RetornoOperacao("ok", "Pessoa cadastrada e salva no disco!", Pessoa);
    }

    @Override
    public RetornoOperacao alterar(Aluno entidade) {
        if (entidade.getId() == null) {
            return new RetornoOperacao("nok", "ID não informado.", null);
        }

        for (int i = 0; i < quantidadeRegistros; i++) {
            if (bancoDeDadosTemporario[i].getId().equals(entidade.getId())) {
                bancoDeDadosTemporario[i].setNome(entidade.getNome());
                bancoDeDadosTemporario[i].setCpf(entidade.getCpf());
                bancoDeDadosTemporario[i].setCorDeFaixa(entidade.getCorDeFaixa());

                salvarDados();

                return new RetornoOperacao("ok", "Pessoa alterada com sucesso!", bancoDeDadosTemporario[i]);
            }
        }

        return new RetornoOperacao("nok", "Pessoa não encontrada.", null);
    }

    @Override
    public RetornoOperacao excluir(Integer id) {
        int indexParaRemover = -1;

        for (int i = 0; i < quantidadeRegistros; i++) {
            if (bancoDeDadosTemporario[i].getId().equals(id)) {
                indexParaRemover = i;
                break;
            }
        }

        if (indexParaRemover == -1) {
            return new RetornoOperacao("nok", "Pessoa com ID " + id + " não encontrada.", null);
        }

        for (int i = indexParaRemover; i < quantidadeRegistros - 1; i++) {
            bancoDeDadosTemporario[i] = bancoDeDadosTemporario[i + 1];
        }

        bancoDeDadosTemporario[quantidadeRegistros - 1] = null;
        quantidadeRegistros--;

        salvarDados();

        return new RetornoOperacao("ok", "Pessoa excluída com sucesso!", null);
    }

    @Override
    public RetornoOperacao buscarTodos() {
        ArrayList<Aluno> listaLocal = new ArrayList<>();

        for (int i = 0; i < quantidadeRegistros; i++) {
            listaLocal.add(bancoDeDadosTemporario[i]);
        }

        if (listaLocal.isEmpty()) {
            return new RetornoOperacao("nok", "Nenhuma Pessoa cadastrada ainda.", listaLocal);
        }

        return new RetornoOperacao("ok", "Consulta realizada com sucesso", listaLocal);
    }
}