package ProjetoRA3.ModelInstrutor;

import java.io.*;
import java.util.ArrayList;

public class InstrutorRepository implements ICrudRepository<Instrutor> {

    private Instrutor[] bancoDeDadosTemporario = new Instrutor[100];
    private int quantidadeRegistros = 0;
    private int geradorDeId = 1;

    private String ARQUIVO_DADOS = "instrutores.dat";

    public InstrutorRepository() {
        carregarDados();
    }

    private void salvarDados() throws KonectException {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = new FileOutputStream(ARQUIVO_DADOS);
            oos = new ObjectOutputStream(fos);

            oos.writeObject(bancoDeDadosTemporario);
            oos.writeInt(quantidadeRegistros);
            oos.writeInt(geradorDeId);

        } catch (IOException e) {
            throw new KonectException("Falha de I/O ao gravar o arquivo instrutores.dat", e);
        } finally {
            try {
                if (oos != null) oos.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                System.out.println("Erro ao fechar fluxos: " + e.getMessage());
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

            bancoDeDadosTemporario = (Instrutor[]) ois.readObject();
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
    public RetornoOperacao inserir(Instrutor instrutor) {
        if (instrutor.getNome() == null || instrutor.getNome().trim().isEmpty()) {
            return new RetornoOperacao("nok", "Nome do instrutor é obrigatório", null);
        }
        if (quantidadeRegistros >= bancoDeDadosTemporario.length) {
            return new RetornoOperacao("nok", "O banco de dados temporário está cheio!", null);
        }

        instrutor.setId(geradorDeId++);
        bancoDeDadosTemporario[quantidadeRegistros] = instrutor;
        quantidadeRegistros++;

        try {
            salvarDados();
            return new RetornoOperacao("ok", "Instrutor cadastrado e salvo no disco!", instrutor);
        } catch (KonectException e) {
            System.out.println(e.getMessage());
            quantidadeRegistros--; // Reverte a adição se falhou ao salvar
            return new RetornoOperacao("nok", "Erro ao salvar no arquivo físico.", null);
        }
    }

    @Override
    public RetornoOperacao alterar(Instrutor entidade) {
        if (entidade.getId() == null) {
            return new RetornoOperacao("nok", "ID não informado.", null);
        }

        for (int i = 0; i < quantidadeRegistros; i++) {
            if (bancoDeDadosTemporario[i].getId().equals(entidade.getId())) {
                bancoDeDadosTemporario[i].setNome(entidade.getNome());
                bancoDeDadosTemporario[i].setCpf(entidade.getCpf());
                bancoDeDadosTemporario[i].setTelefone(entidade.getTelefone());

                try {
                    salvarDados();
                    return new RetornoOperacao("ok", "Instrutor alterado com sucesso!", bancoDeDadosTemporario[i]);
                } catch (KonectException e) {
                    System.out.println(e.getMessage());
                    return new RetornoOperacao("nok", "Erro ao atualizar o arquivo físico.", null);
                }
            }
        }
        return new RetornoOperacao("nok", "Instrutor não encontrado.", null);
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
            return new RetornoOperacao("nok", "Instrutor com ID " + id + " não encontrado.", null);
        }

        for (int i = indexParaRemover; i < quantidadeRegistros - 1; i++) {
            bancoDeDadosTemporario[i] = bancoDeDadosTemporario[i + 1];
        }

        bancoDeDadosTemporario[quantidadeRegistros - 1] = null;
        quantidadeRegistros--;

        try {
            salvarDados();
            return new RetornoOperacao("ok", "Instrutor excluído com sucesso!", null);
        } catch (KonectException e) {
            System.out.println(e.getMessage());
            return new RetornoOperacao("nok", "Erro ao excluir do arquivo físico.", null);
        }
    }

    @Override
    public RetornoOperacao buscarTodos() {
        ArrayList<Instrutor> listaLocal = new ArrayList<>();

        for (int i = 0; i < quantidadeRegistros; i++) {
            listaLocal.add(bancoDeDadosTemporario[i]);
        }

        if (listaLocal.isEmpty()) {
            return new RetornoOperacao("nok", "Nenhum instrutor cadastrado ainda.", listaLocal);
        }

        return new RetornoOperacao("ok", "Consulta realizada com sucesso", listaLocal);
    }
}