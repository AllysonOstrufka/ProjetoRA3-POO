# 📚 Guia: Como Adicionar Novos Módulos

Este guia mostra passo a passo como adicionar novos módulos (como Alunos e Instrutores) seguindo o padrão já estabelecido no projeto.

## 🎯 Exemplo: Implementando o Módulo de Alunos

### Passo 1: Criar a Classe Model

Crie o arquivo `ModelAluno/Aluno.java`:

```java
package ProjetoRA3.ModelAluno;

import java.io.Serializable;

public class Aluno implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String nome;
    private String matricula;
    private String email;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

### Passo 2: Criar a Interface CRUD

Crie o arquivo `ModelAluno/ICrudRepository.java`:

```java
package ProjetoRA3.ModelAluno;

public interface ICrudRepository<T> {
    RetornoOperacao inserir(T entidade);
    RetornoOperacao alterar(T entidade);
    RetornoOperacao excluir(Integer id);
    RetornoOperacao buscarTodos();
}
```

### Passo 3: Criar a Classe de Retorno

Crie o arquivo `ModelAluno/RetornoOperacao.java`:

```java
package ProjetoRA3.ModelAluno;

public class RetornoOperacao {
    private String status;
    private String mensagem;
    private Object data;

    public RetornoOperacao(String status, String mensagem, Object data) {
        this.status = status;
        this.mensagem = mensagem;
        this.data = data;
    }

    public String getStatus() { return status; }
    public String getMensagem() { return mensagem; }
    public Object getData() { return data; }
}
```

### Passo 4: Criar o Repository

Crie o arquivo `ModelAluno/AlunoRepository.java`:

```java
package ProjetoRA3.ModelAluno;

import java.io.*;
import java.util.ArrayList;

public class AlunoRepository implements ICrudRepository<Aluno> {

    private Aluno[] bancoDeDadosTemporario = new Aluno[100];
    private int quantidadeRegistros = 0;
    private int geradorDeId = 1;

    private String ARQUIVO_DADOS = "alunos.dat";

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
            System.out.println("Arquivo de dados antigo/incompatível encontrado. Deletando...");
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
    public RetornoOperacao inserir(Aluno aluno) {
        if (aluno.getNome() == null || aluno.getNome().trim().isEmpty()) {
            return new RetornoOperacao("nok", "Nome do aluno é obrigatório", null);
        }
        if (aluno.getMatricula() == null || aluno.getMatricula().trim().isEmpty()) {
            return new RetornoOperacao("nok", "Matrícula do aluno é obrigatória", null);
        }
        if (aluno.getEmail() == null || aluno.getEmail().trim().isEmpty()) {
            return new RetornoOperacao("nok", "Email do aluno é obrigatório", null);
        }
        if (quantidadeRegistros >= bancoDeDadosTemporario.length) {
            return new RetornoOperacao("nok", "O banco de dados temporário está cheio!", null);
        }

        aluno.setId(geradorDeId++);
        bancoDeDadosTemporario[quantidadeRegistros] = aluno;
        quantidadeRegistros++;

        salvarDados();

        return new RetornoOperacao("ok", "Aluno cadastrado e salvo no disco!", aluno);
    }

    @Override
    public RetornoOperacao alterar(Aluno entidade) {
        if (entidade.getId() == null) {
            return new RetornoOperacao("nok", "ID não informado.", null);
        }

        for (int i = 0; i < quantidadeRegistros; i++) {
            if (bancoDeDadosTemporario[i].getId().equals(entidade.getId())) {
                bancoDeDadosTemporario[i].setNome(entidade.getNome());
                bancoDeDadosTemporario[i].setMatricula(entidade.getMatricula());
                bancoDeDadosTemporario[i].setEmail(entidade.getEmail());

                salvarDados();

                return new RetornoOperacao("ok", "Aluno alterado com sucesso!", bancoDeDadosTemporario[i]);
            }
        }

        return new RetornoOperacao("nok", "Aluno não encontrado.", null);
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
            return new RetornoOperacao("nok", "Aluno com ID " + id + " não encontrado.", null);
        }

        for (int i = indexParaRemover; i < quantidadeRegistros - 1; i++) {
            bancoDeDadosTemporario[i] = bancoDeDadosTemporario[i + 1];
        }

        bancoDeDadosTemporario[quantidadeRegistros - 1] = null;
        quantidadeRegistros--;

        salvarDados();

        return new RetornoOperacao("ok", "Aluno excluído com sucesso!", null);
    }

    @Override
    public RetornoOperacao buscarTodos() {
        ArrayList<Aluno> listaLocal = new ArrayList<>();

        for (int i = 0; i < quantidadeRegistros; i++) {
            if (bancoDeDadosTemporario[i] != null) {
                listaLocal.add(bancoDeDadosTemporario[i]);
            }
        }

        return new RetornoOperacao("ok", "Busca realizada com sucesso!", listaLocal);
    }
}
```

### Passo 5: Criar o Painel de Interface

Crie o arquivo `UI/AlunoPanel.java`:

```java
package ProjetoRA3.UI;

import ProjetoRA3.ModelAluno.Aluno;
import ProjetoRA3.ModelAluno.AlunoRepository;
import ProjetoRA3.ModelAluno.RetornoOperacao;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class AlunoPanel {

    private final AlunoRepository repository = new AlunoRepository();

    public VBox createPanel() {
        Label lblId = new Label("ID (Informar somente para editar/excluir):");
        TextField txtId = new TextField();

        Label lblNome = new Label("Nome:");
        TextField txtNome = new TextField();

        Label lblMatricula = new Label("Matrícula:");
        TextField txtMatricula = new TextField();

        Label lblEmail = new Label("Email:");
        TextField txtEmail = new TextField();

        Button btnSalvar = new Button("Salvar Novo");
        Button btnEditar = new Button("Editar Existente");
        Button btnExcluir = new Button("Excluir");
        Button btnBuscar = new Button("Carregar Lista");

        TextArea areaResultados = new TextArea();
        areaResultados.setEditable(false);

        HBox boxBotoesAcao = new HBox(10);
        boxBotoesAcao.getChildren().addAll(btnSalvar, btnEditar, btnExcluir);

        btnSalvar.setOnAction(e -> {
            Aluno novoAluno = new Aluno();
            novoAluno.setNome(txtNome.getText());
            novoAluno.setMatricula(txtMatricula.getText());
            novoAluno.setEmail(txtEmail.getText());

            new Thread(() -> {
                RetornoOperacao retorno = repository.inserir(novoAluno);

                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText(retorno.getMensagem());
                    alert.show();

                    if(retorno.getStatus().equals("ok")) {
                        txtNome.clear(); txtMatricula.clear(); txtEmail.clear(); txtId.clear();
                        btnBuscar.fire();
                    }
                });
            }).start();
        });

        btnEditar.setOnAction(e -> {
            try {
                Aluno editado = new Aluno();
                editado.setId(Integer.parseInt(txtId.getText()));
                editado.setNome(txtNome.getText());
                editado.setMatricula(txtMatricula.getText());
                editado.setEmail(txtEmail.getText());

                new Thread(() -> {
                    RetornoOperacao retorno = repository.alterar(editado);

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText(retorno.getMensagem());
                        alert.show();

                        if(retorno.getStatus().equals("ok")) {
                            txtNome.clear(); txtMatricula.clear(); txtEmail.clear(); txtId.clear();
                            btnBuscar.fire();
                        }
                    });
                }).start();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("ID inválido para edição.");
                alert.show();
            }
        });

        btnExcluir.setOnAction(e -> {
            try {
                Integer idParaExcluir = Integer.parseInt(txtId.getText());

                new Thread(() -> {
                    RetornoOperacao retorno = repository.excluir(idParaExcluir);

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText(retorno.getMensagem());
                        alert.show();

                        if (retorno.getStatus().equals("ok")) {
                            txtId.clear();
                            btnBuscar.fire();
                        }
                    });
                }).start();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("ID inválido para exclusão.");
                alert.show();
            }
        });

        btnBuscar.setOnAction(e -> {
            new Thread(() -> {
                RetornoOperacao retorno = repository.buscarTodos();

                Platform.runLater(() -> {
                    areaResultados.clear();

                    if (retorno.getStatus().equals("ok")) {
                        ArrayList<Aluno> listaLocal = (ArrayList<Aluno>) retorno.getData();

                        for (Aluno aluno : listaLocal) {
                            areaResultados.appendText("ID: " + aluno.getId() +
                                    " | Nome: " + aluno.getNome() +
                                    " | Matrícula: " + aluno.getMatricula() + "\n");
                        }
                    } else {
                        areaResultados.setText(retorno.getMensagem());
                    }
                });
            }).start();
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        layout.getChildren().addAll(
                lblId, txtId,
                lblNome, txtNome,
                lblMatricula, txtMatricula,
                lblEmail, txtEmail,
                boxBotoesAcao,
                new Separator(),
                btnBuscar, areaResultados
        );

        return layout;
    }
}
```

### Passo 6: Atualizar Main.java

Modifique o arquivo `Main.java` para adicionar a aba de Alunos:

```java
// Adicione no início do arquivo:
import ProjetoRA3.UI.AlunoPanel;

// No método start(), substitua a linha:
// Tab tabAlunos = new Tab("Alunos", new PlaceholderPanel().createPanel("Alunos"));
// Por:
Tab tabAlunos = new Tab("Alunos", new AlunoPanel().createPanel());
```

## 📋 Checklist para Implementação de Novo Módulo

- [ ] Criar classe Model em `ModelXXX/`
- [ ] Criar interface `ICrudRepository.java`
- [ ] Criar classe `RetornoOperacao.java`
- [ ] Criar classe `XXXRepository.java`
- [ ] Criar classe `XXXPanel.java` em UI
- [ ] Atualizar `Main.java` para incluir nova aba
- [ ] Testar inserção
- [ ] Testar consulta
- [ ] Testar atualização
- [ ] Testar exclusão

## 🎨 Estrutura da UI

Cada painel segue a mesma estrutura visual:

```
┌─ ID Field (opcional)
├─ Campo 1
├─ Campo 2
├─ Campo 3
├─ Botões de Ação (Salvar, Editar, Excluir)
├─ Separador
├─ Botão Carregar Lista
└─ Área de Resultados
```

## 🔄 Fluxo de Operações

### Inserir

1. Preencher campos
2. Clicar "Salvar Novo"
3. Repository gera ID automaticamente
4. Dados salvos em arquivo .dat

### Atualizar

1. Informar ID
2. Preencher novos dados
3. Clicar "Editar Existente"
4. Dados atualizados no arquivo

### Excluir

1. Informar ID
2. Clicar "Excluir"
3. Registro removido do arquivo

### Consultar

1. Clicar "Carregar Lista"
2. Todos os registros aparecem na TextArea

## 💡 Dicas

- Sempre valide campos obrigatórios no Repository
- Use threads para operações de persistência (não bloqueia UI)
- Siga o padrão de nomes: `XXXRepository`, `XXXPanel`, `XXXInterface`
- Mantenha consistência com os nomes dos arquivos .dat

## ❓ Dúvidas Frequentes

**P: Por que usar threads para salvar dados?**
R: Para não congelar a interface enquanto persiste dados.

**P: Como adicionar mais de 3 atributos?**
R: Basta adicionar getters, setters e campos TextField na UI.

**P: Os dados são salvos automaticamente?**
R: Sim, cada operação (inserir, alterar, excluir) salva imediatamente.
