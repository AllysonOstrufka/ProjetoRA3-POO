package ProjetoRA3.ModelAdmin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class AdminRepository implements ICrudRepository<Admin> {

    private Admin[] bancoDeDadosTemporario = new Admin[100];
    private int quantidadeRegistros = 0;
    private int geradorDeId = 1;

    private String ARQUIVO_DADOS = "admins.dat";

    public AdminRepository() {
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

            bancoDeDadosTemporario = (Admin[]) ois.readObject();
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
    public RetornoOperacao inserir(Admin admin) {
        if (admin.getNome() == null || admin.getNome().trim().isEmpty()) {
            return new RetornoOperacao("nok", "Nome do admin é obrigatório", null);
        }
        if (admin.getEmail() == null || admin.getEmail().trim().isEmpty()) {
            return new RetornoOperacao("nok", "Email do admin é obrigatório", null);
        }
        if (admin.getSenha() == null || admin.getSenha().trim().isEmpty()) {
            return new RetornoOperacao("nok", "Senha do admin é obrigatória", null);
        }
        if (quantidadeRegistros >= bancoDeDadosTemporario.length) {
            return new RetornoOperacao("nok", "O banco de dados temporário está cheio!", null);
        }

        admin.setId(geradorDeId++);
        bancoDeDadosTemporario[quantidadeRegistros] = admin;
        quantidadeRegistros++;

        salvarDados();

        return new RetornoOperacao("ok", "Admin cadastrado e salvo no disco!", admin);
    }

    @Override
    public RetornoOperacao alterar(Admin entidade) {
        if (entidade.getId() == null) {
            return new RetornoOperacao("nok", "ID não informado.", null);
        }

        for (int i = 0; i < quantidadeRegistros; i++) {
            if (bancoDeDadosTemporario[i].getId().equals(entidade.getId())) {
                bancoDeDadosTemporario[i].setNome(entidade.getNome());
                bancoDeDadosTemporario[i].setEmail(entidade.getEmail());
                bancoDeDadosTemporario[i].setSenha(entidade.getSenha());

                salvarDados();

                return new RetornoOperacao("ok", "Admin alterado com sucesso!", bancoDeDadosTemporario[i]);
            }
        }

        return new RetornoOperacao("nok", "Admin não encontrado.", null);
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
            return new RetornoOperacao("nok", "Admin com ID " + id + " não encontrado.", null);
        }

        for (int i = indexParaRemover; i < quantidadeRegistros - 1; i++) {
            bancoDeDadosTemporario[i] = bancoDeDadosTemporario[i + 1];
        }

        bancoDeDadosTemporario[quantidadeRegistros - 1] = null;
        quantidadeRegistros--;

        salvarDados();

        return new RetornoOperacao("ok", "Admin excluído com sucesso!", null);
    }

    @Override
    public RetornoOperacao buscarTodos() {
        ArrayList<Admin> listaLocal = new ArrayList<>();

        for (int i = 0; i < quantidadeRegistros; i++) {
            if (bancoDeDadosTemporario[i] != null) {
                listaLocal.add(bancoDeDadosTemporario[i]);
            }
        }

        return new RetornoOperacao("ok", "Busca realizada com sucesso!", listaLocal);
    }
}
