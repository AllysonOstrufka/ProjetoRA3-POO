package ProjetoRA3.ModelAviso;

import java.io.*;
import java.util.ArrayList;

public class AvisoRepository implements ICrudRepository<Aviso> {

    private Aviso[] bancoDeDadosTemporario = new Aviso[100];
    private int quantidadeRegistros = 0;
    private int geradorDeId = 1;

    private String ARQUIVO_DADOS = "avisos.dat";

    public AvisoRepository() {
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
            throw new KonectException("Falha de I/O ao gravar o arquivo avisos.dat", e);
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

            bancoDeDadosTemporario = (Aviso[]) ois.readObject();
            quantidadeRegistros = ois.readInt();
            geradorDeId = ois.readInt();

        } catch (Exception e) {
            System.out.println("Arquivo de dados antigo/incompatível encontrado. Deletando para criar um novo...");
            try {
                if (ois != null) ois.close();
                if (fis != null) fis.close();
            } catch (IOException ex) { }
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
    public RetornoOperacao inserir(Aviso aviso) {
        if (aviso.getTitulo() == null || aviso.getTitulo().trim().isEmpty()) {
            return new RetornoOperacao("nok", "Título do aviso é obrigatório", null);
        }
        if (aviso.getMensagem() == null || aviso.getMensagem().trim().isEmpty()) {
            return new RetornoOperacao("nok", "A mensagem não pode estar vazia", null);
        }
        if (quantidadeRegistros >= bancoDeDadosTemporario.length) {
            return new RetornoOperacao("nok", "O banco de dados temporário está cheio!", null);
        }

        aviso.setId(geradorDeId++);
        bancoDeDadosTemporario[quantidadeRegistros] = aviso;
        quantidadeRegistros++;

        try {
            salvarDados();
            return new RetornoOperacao("ok", "Aviso publicado e salvo no disco!", aviso);
        } catch (KonectException e) {
            System.out.println(e.getMessage());
            quantidadeRegistros--; // Reverte a adição se falhou ao salvar
            return new RetornoOperacao("nok", "Erro ao salvar no arquivo físico.", null);
        }
    }

    @Override
    public RetornoOperacao alterar(Aviso entidade) {
        if (entidade.getId() == null) {
            return new RetornoOperacao("nok", "ID não informado.", null);
        }

        for (int i = 0; i < quantidadeRegistros; i++) {
            if (bancoDeDadosTemporario[i].getId().equals(entidade.getId())) {
                bancoDeDadosTemporario[i].setTitulo(entidade.getTitulo());
                bancoDeDadosTemporario[i].setDataPublicacao(entidade.getDataPublicacao());
                bancoDeDadosTemporario[i].setMensagem(entidade.getMensagem());

                try {
                    salvarDados();
                    return new RetornoOperacao("ok", "Aviso alterado com sucesso!", bancoDeDadosTemporario[i]);
                } catch (KonectException e) {
                    System.out.println(e.getMessage());
                    return new RetornoOperacao("nok", "Erro ao atualizar o arquivo físico.", null);
                }
            }
        }
        return new RetornoOperacao("nok", "Aviso não encontrado.", null);
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
            return new RetornoOperacao("nok", "Aviso com ID " + id + " não encontrado.", null);
        }

        for (int i = indexParaRemover; i < quantidadeRegistros - 1; i++) {
            bancoDeDadosTemporario[i] = bancoDeDadosTemporario[i + 1];
        }

        bancoDeDadosTemporario[quantidadeRegistros - 1] = null;
        quantidadeRegistros--;

        try {
            salvarDados();
            return new RetornoOperacao("ok", "Aviso excluído com sucesso!", null);
        } catch (KonectException e) {
            System.out.println(e.getMessage());
            return new RetornoOperacao("nok", "Erro ao excluir do arquivo físico.", null);
        }
    }

    @Override
    public RetornoOperacao buscarTodos() {
        ArrayList<Aviso> listaLocal = new ArrayList<>();

        for (int i = 0; i < quantidadeRegistros; i++) {
            listaLocal.add(bancoDeDadosTemporario[i]);
        }

        if (listaLocal.isEmpty()) {
            return new RetornoOperacao("nok", "Nenhum aviso publicado ainda.", listaLocal);
        }

        return new RetornoOperacao("ok", "Consulta realizada com sucesso", listaLocal);
    }
}