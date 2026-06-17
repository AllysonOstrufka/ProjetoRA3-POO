package ProjetoRA3.ModelGraduacao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class GraduacaoRepository implements ICrudRepository<GraduacaoFaixa> {

    private GraduacaoFaixa[] bancoDeDadosTemporario = new GraduacaoFaixa[100];
    private int quantidadeRegistros = 0;
    private int geradorDeId = 1;

    private String ARQUIVO_DADOS = "graduacoes.dat";

    public GraduacaoRepository() {
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

            bancoDeDadosTemporario = (GraduacaoFaixa[]) ois.readObject();
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
    public RetornoOperacao inserir(GraduacaoFaixa graduacao) {
        if (graduacao.getCorFaixa() == null || graduacao.getCorFaixa().trim().isEmpty()) {
            return new RetornoOperacao("nok", "O campo Cor da faixa é obrigatório.", null);
        }
        if (graduacao.getHierarquia() < 0 || graduacao.getTempoMinimoMeses() < 0) {
            return new RetornoOperacao("nok", "Hierarquia e Tempo mínimo não podem ser negativos.", null);
        }
        if (graduacao.getHierarquia() > 25) {
            return new RetornoOperacao("nok", "A hierarquia não pode ser maior que 25.", null);
        }
        if (graduacao.getTempoMinimoMeses() > 300) {
            return new RetornoOperacao("nok", "O tempo mínimo não pode ser maior que 300 meses.", null);
        }
        if (quantidadeRegistros >= bancoDeDadosTemporario.length) {
            return new RetornoOperacao("nok", "O banco de dados temporário está cheio!", null);
        }

        graduacao.setId(geradorDeId++);
        bancoDeDadosTemporario[quantidadeRegistros] = graduacao;
        quantidadeRegistros++;

        salvarDados();

        return new RetornoOperacao("ok", "Graduação cadastrada e salva no disco!", graduacao);
    }

    @Override
    public RetornoOperacao alterar(GraduacaoFaixa entidade) {
        if (entidade.getId() == null) {
            return new RetornoOperacao("nok", "ID não informado.", null);
        }
        if (entidade.getCorFaixa() == null || entidade.getCorFaixa().trim().isEmpty()) {
            return new RetornoOperacao("nok", "O campo Cor da faixa é obrigatório.", null);
        }
        if (entidade.getHierarquia() < 0 || entidade.getTempoMinimoMeses() < 0) {
            return new RetornoOperacao("nok", "Hierarquia e Tempo mínimo não podem ser negativos.", null);
        }
        if (entidade.getHierarquia() > 25) {
            return new RetornoOperacao("nok", "A hierarquia não pode ser maior que 25.", null);
        }
        if (entidade.getTempoMinimoMeses() > 300) {
            return new RetornoOperacao("nok", "O tempo mínimo não pode ser maior que 300 meses.", null);
        }

        for (int i = 0; i < quantidadeRegistros; i++) {
            if (bancoDeDadosTemporario[i].getId().equals(entidade.getId())) {
                bancoDeDadosTemporario[i].setCorFaixa(entidade.getCorFaixa());
                bancoDeDadosTemporario[i].setHierarquia(entidade.getHierarquia());
                bancoDeDadosTemporario[i].setTempoMinimoMeses(entidade.getTempoMinimoMeses());

                salvarDados();

                return new RetornoOperacao("ok", "Graduação alterada com sucesso!", bancoDeDadosTemporario[i]);
            }
        }

        return new RetornoOperacao("nok", "Graduação não encontrada.", null);
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
            return new RetornoOperacao("nok", "Graduação com ID " + id + " não encontrada.", null);
        }

        for (int i = indexParaRemover; i < quantidadeRegistros - 1; i++) {
            bancoDeDadosTemporario[i] = bancoDeDadosTemporario[i + 1];
        }

        bancoDeDadosTemporario[quantidadeRegistros - 1] = null;
        quantidadeRegistros--;

        salvarDados();

        return new RetornoOperacao("ok", "Graduação excluída com sucesso!", null);
    }

    @Override
    public RetornoOperacao buscarTodos() {
        ArrayList<GraduacaoFaixa> listaLocal = new ArrayList<>();

        for (int i = 0; i < quantidadeRegistros; i++) {
            listaLocal.add(bancoDeDadosTemporario[i]);
        }

        if (listaLocal.isEmpty()) {
            return new RetornoOperacao("nok", "Nenhuma graduação cadastrada ainda.", listaLocal);
        }

        return new RetornoOperacao("ok", "Consulta realizada com sucesso", listaLocal);
    }
}
