package ProjetoRA3.ModelAcademia;

import java.io.*;
import java.util.ArrayList;

public class AcademiaRepository implements ICrudRepository<AcademiaLuta> {

    private AcademiaLuta[] bancoDeDadosTemporario = new AcademiaLuta[100];
    private int quantidadeRegistros = 0;
    private int geradorDeId = 1;

    private String ARQUIVO_DADOS = "academias.dat";

    public AcademiaRepository() {
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

            bancoDeDadosTemporario = (AcademiaLuta[]) ois.readObject();
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
    public RetornoOperacao inserir(AcademiaLuta academia) {
        if (academia.getNome() == null || academia.getNome().trim().isEmpty()) {
            return new RetornoOperacao("nok", "Nome da academia é obrigatório", null);
        }
        if (quantidadeRegistros >= bancoDeDadosTemporario.length) {
            return new RetornoOperacao("nok", "O banco de dados temporário está cheio!", null);
        }

        academia.setId(geradorDeId++);
        bancoDeDadosTemporario[quantidadeRegistros] = academia;
        quantidadeRegistros++;

        salvarDados();

        return new RetornoOperacao("ok", "Academia cadastrada e salva no disco!", academia);
    }

    @Override
    public RetornoOperacao alterar(AcademiaLuta entidade) {
        if (entidade.getId() == null) {
            return new RetornoOperacao("nok", "ID não informado.", null);
        }

        for (int i = 0; i < quantidadeRegistros; i++) {
            if (bancoDeDadosTemporario[i].getId().equals(entidade.getId())) {
                bancoDeDadosTemporario[i].setNome(entidade.getNome());
                bancoDeDadosTemporario[i].setCnpj(entidade.getCnpj());
                bancoDeDadosTemporario[i].setEndereco(entidade.getEndereco());

                salvarDados();

                return new RetornoOperacao("ok", "Academia alterada com sucesso!", bancoDeDadosTemporario[i]);
            }
        }

        return new RetornoOperacao("nok", "Academia não encontrada.", null);
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
            return new RetornoOperacao("nok", "Academia com ID " + id + " não encontrada.", null);
        }

        for (int i = indexParaRemover; i < quantidadeRegistros - 1; i++) {
            bancoDeDadosTemporario[i] = bancoDeDadosTemporario[i + 1];
        }

        bancoDeDadosTemporario[quantidadeRegistros - 1] = null;
        quantidadeRegistros--;

        salvarDados();

        return new RetornoOperacao("ok", "Academia excluída com sucesso!", null);
    }

    @Override
    public RetornoOperacao buscarTodos() {
        ArrayList<AcademiaLuta> listaLocal = new ArrayList<>();

        for (int i = 0; i < quantidadeRegistros; i++) {
            listaLocal.add(bancoDeDadosTemporario[i]);
        }

        if (listaLocal.isEmpty()) {
            return new RetornoOperacao("nok", "Nenhuma academia cadastrada ainda.", listaLocal);
        }

        return new RetornoOperacao("ok", "Consulta realizada com sucesso", listaLocal);
    }
}