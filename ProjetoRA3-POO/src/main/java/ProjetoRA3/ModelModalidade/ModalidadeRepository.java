package ProjetoRA3.ModelModalidade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ModalidadeRepository implements ICrudRepository<Modalidade> {

    private Modalidade[] bancoDeDadosTemporario = new Modalidade[100];
    private int quantidadeRegistros = 0;
    private int geradorDeId = 1;

    private String ARQUIVO_DADOS = "modalidades.dat";

    public ModalidadeRepository() {
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

            bancoDeDadosTemporario = (Modalidade[]) ois.readObject();
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
    public RetornoOperacao inserir(Modalidade modalidade) {
        if (modalidade.getTipo() == null || modalidade.getTipo().trim().isEmpty()) {
            return new RetornoOperacao("nok", "O campo Tipo é obrigatório.", null);
        }
        if (modalidade.getCargaHoraria() <= 0) {
            return new RetornoOperacao("nok", "A carga horária deve ser maior que zero.", null);
        }
        if (modalidade.getCargaHoraria() > 1000) {
            return new RetornoOperacao("nok", "A carga horária não pode ser maior que 1000.", null);
        }
        if (modalidade.getAcademiaId() == null) {
            return new RetornoOperacao("nok", "Selecione uma academia.", null);
        }
        if (quantidadeRegistros >= bancoDeDadosTemporario.length) {
            return new RetornoOperacao("nok", "O banco de dados temporário está cheio!", null);
        }

        modalidade.setId(geradorDeId++);
        bancoDeDadosTemporario[quantidadeRegistros] = modalidade;
        quantidadeRegistros++;

        salvarDados();

        return new RetornoOperacao("ok", "Modalidade cadastrada e salva no disco!", modalidade);
    }

    @Override
    public RetornoOperacao alterar(Modalidade entidade) {
        if (entidade.getId() == null) {
            return new RetornoOperacao("nok", "ID não informado.", null);
        }
        if (entidade.getTipo() == null || entidade.getTipo().trim().isEmpty()) {
            return new RetornoOperacao("nok", "O campo Tipo é obrigatório.", null);
        }
        if (entidade.getCargaHoraria() <= 0) {
            return new RetornoOperacao("nok", "A carga horária deve ser maior que zero.", null);
        }
        if (entidade.getCargaHoraria() > 1000) {
            return new RetornoOperacao("nok", "A carga horária não pode ser maior que 1000.", null);
        }
        if (entidade.getAcademiaId() == null) {
            return new RetornoOperacao("nok", "Selecione uma academia.", null);
        }

        for (int i = 0; i < quantidadeRegistros; i++) {
            if (bancoDeDadosTemporario[i].getId().equals(entidade.getId())) {
                bancoDeDadosTemporario[i].setTipo(entidade.getTipo());
                bancoDeDadosTemporario[i].setDescricao(entidade.getDescricao());
                bancoDeDadosTemporario[i].setCargaHoraria(entidade.getCargaHoraria());
                bancoDeDadosTemporario[i].setAcademiaId(entidade.getAcademiaId());

                salvarDados();

                return new RetornoOperacao("ok", "Modalidade alterada com sucesso!", bancoDeDadosTemporario[i]);
            }
        }

        return new RetornoOperacao("nok", "Modalidade não encontrada.", null);
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
            return new RetornoOperacao("nok", "Modalidade com ID " + id + " não encontrada.", null);
        }

        for (int i = indexParaRemover; i < quantidadeRegistros - 1; i++) {
            bancoDeDadosTemporario[i] = bancoDeDadosTemporario[i + 1];
        }

        bancoDeDadosTemporario[quantidadeRegistros - 1] = null;
        quantidadeRegistros--;

        salvarDados();

        return new RetornoOperacao("ok", "Modalidade excluída com sucesso!", null);
    }

    @Override
    public RetornoOperacao buscarTodos() {
        ArrayList<Modalidade> listaLocal = new ArrayList<>();

        for (int i = 0; i < quantidadeRegistros; i++) {
            listaLocal.add(bancoDeDadosTemporario[i]);
        }

        if (listaLocal.isEmpty()) {
            return new RetornoOperacao("nok", "Nenhuma modalidade cadastrada ainda.", listaLocal);
        }

        return new RetornoOperacao("ok", "Consulta realizada com sucesso", listaLocal);
    }
}
