package ProjetoRA3.ModelRegras;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import ProjetoRA3.ModelAdmin.ICrudRepository;
import ProjetoRA3.ModelAdmin.RetornoOperacao;

public class RegrasRepository implements ICrudRepository<Regras> {

    private Regras[] bancoDeDadosTemporario = new Regras[100];
    private int quantidadeRegistros = 0;
    private int geradorDeId = 1;

    private String ARQUIVO_DADOS = "regras.dat";

    public RegrasRepository() {
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

            bancoDeDadosTemporario = (Regras[]) ois.readObject();
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
    public RetornoOperacao inserir(Regras regra) {
        if (regra.getDescricao() == null || regra.getDescricao().trim().isEmpty()) {
            return new RetornoOperacao("nok", "Descrição da regra é obrigatória", null);
        }
        if (regra.getTempoMinimo() == null || regra.getTempoMinimo() <= 0) {
            return new RetornoOperacao("nok", "Tempo mínimo é obrigatório e deve ser maior que zero", null);
        }
        if (quantidadeRegistros >= bancoDeDadosTemporario.length) {
            return new RetornoOperacao("nok", "O banco de dados temporário está cheio!", null);
        }

        regra.setId(geradorDeId++);
        bancoDeDadosTemporario[quantidadeRegistros++] = regra;
        salvarDados();

        return new RetornoOperacao("ok", "Regra inserida com sucesso!", null);
    }

    @Override
    public RetornoOperacao alterar(Regras regra) {
        if (regra.getDescricao() == null || regra.getDescricao().trim().isEmpty()) {
            return new RetornoOperacao("nok", "Descrição da regra é obrigatória", null);
        }
        if (regra.getTempoMinimo() == null || regra.getTempoMinimo() <= 0) {
            return new RetornoOperacao("nok", "Tempo mínimo é obrigatório e deve ser maior que zero", null);
        }

        for (int i = 0; i < quantidadeRegistros; i++) {
            if (bancoDeDadosTemporario[i].getId().equals(regra.getId())) {
                bancoDeDadosTemporario[i] = regra;
                salvarDados();
                return new RetornoOperacao("ok", "Regra atualizada com sucesso!", null);
            }
        }

        return new RetornoOperacao("nok", "Regra não encontrada", null);
    }

    @Override
    public RetornoOperacao excluir(Integer id) {
        for (int i = 0; i < quantidadeRegistros; i++) {
            if (bancoDeDadosTemporario[i].getId().equals(id)) {
                for (int j = i; j < quantidadeRegistros - 1; j++) {
                    bancoDeDadosTemporario[j] = bancoDeDadosTemporario[j + 1];
                }
                quantidadeRegistros--;
                salvarDados();
                return new RetornoOperacao("ok", "Regra excluída com sucesso!", null);
            }
        }

        return new RetornoOperacao("nok", "Regra não encontrada", null);
    }

    @Override
    public RetornoOperacao buscarTodos() {
        ArrayList<Regras> lista = new ArrayList<>();

        for (int i = 0; i < quantidadeRegistros; i++) {
            lista.add(bancoDeDadosTemporario[i]);
        }

        return new RetornoOperacao("ok", "Busca realizada com sucesso!", lista);
    }
}
