package ProjetoRA3.UI;

import java.util.ArrayList;

import ProjetoRA3.ModelInstrutor.InstrutorRepository;
import ProjetoRA3.ModelInstrutor.Instrutor;
import ProjetoRA3.ModelInstrutor.RetornoOperacao;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InstrutorPanel {

    private final InstrutorRepository repository = new InstrutorRepository();

    public VBox createPanel() {
        Label lblId = new Label("ID (Informar somente para editar/excluir):");
        TextField txtId = new TextField();

        Label lblNome = new Label("Nome:");
        TextField txtNome = new TextField();

        Label lblCpf = new Label("CPF:");
        TextField txtCpf = new TextField();

        Label lblTelefone = new Label("Telefone:");
        TextField txtTelefone = new TextField();

        Button btnSalvar = new Button("Salvar Novo");
        Button btnEditar = new Button("Editar Existente");
        Button btnExcluir = new Button("Excluir");
        Button btnBuscar = new Button("Carregar Lista");

        TextArea areaResultados = new TextArea();
        areaResultados.setEditable(false);

        HBox boxBotoesAcao = new HBox(10);
        boxBotoesAcao.getChildren().addAll(btnSalvar, btnEditar, btnExcluir);

        btnSalvar.setOnAction(e -> {
            Instrutor nova = new Instrutor();
            nova.setNome(txtNome.getText());
            nova.setCpf(txtCpf.getText());
            nova.setTelefone(txtTelefone.getText());

            new Thread(() -> {
                RetornoOperacao retorno = repository.inserir(nova);

                Platform.runLater(() -> {
                    Alert alert = new Alert(retorno.getStatus().equals("ok") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                    alert.setContentText(retorno.getMensagem());
                    alert.show();

                    if(retorno.getStatus().equals("ok")) {
                        txtNome.clear(); txtCpf.clear(); txtTelefone.clear(); txtId.clear();
                        btnBuscar.fire();
                    }
                });
            }).start();
        });

        btnEditar.setOnAction(e -> {
            try {
                Instrutor editada = new Instrutor();
                editada.setId(Integer.parseInt(txtId.getText()));
                editada.setNome(txtNome.getText());
                editada.setCpf(txtCpf.getText());
                editada.setTelefone(txtTelefone.getText());

                new Thread(() -> {
                    RetornoOperacao retorno = repository.alterar(editada);

                    Platform.runLater(() -> {
                        Alert alert = new Alert(retorno.getStatus().equals("ok") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                        alert.setContentText(retorno.getMensagem());
                        alert.show();

                        if(retorno.getStatus().equals("ok")) {
                            txtNome.clear(); txtCpf.clear(); txtTelefone.clear(); txtId.clear();
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
                        Alert alert = new Alert(retorno.getStatus().equals("ok") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
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
                        ArrayList<Instrutor> listaLocal = (ArrayList<Instrutor>) retorno.getData();

                        for (Instrutor inst : listaLocal) {
                            areaResultados.appendText("ID: " + inst.getId() +
                                    " | Nome: " + inst.getNome() +
                                    " | CPF: " + inst.getCpf() +
                                    " | Telefone: " + inst.getTelefone() + "\n");
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
                lblCpf, txtCpf,
                lblTelefone, txtTelefone,
                boxBotoesAcao,
                new Separator(),
                btnBuscar, areaResultados
        );

        return layout;
    }
}