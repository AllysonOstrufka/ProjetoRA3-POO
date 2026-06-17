package ProjetoRA3.UI;

import java.util.ArrayList;

import ProjetoRA3.ModelAdmin.Admin;
import ProjetoRA3.ModelAdmin.AdminRepository;
import ProjetoRA3.ModelAdmin.RetornoOperacao;
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

public class AdminPanel {

    private final AdminRepository repository = new AdminRepository();

    public VBox createPanel() {
        Label lblId = new Label("ID (Informar somente para editar/excluir):");
        TextField txtId = new TextField();

        Label lblNome = new Label("Nome:");
        TextField txtNome = new TextField();

        Label lblEmail = new Label("Email:");
        TextField txtEmail = new TextField();

        Label lblSenha = new Label("Senha:");
        TextField txtSenha = new TextField();

        Button btnSalvar = new Button("Salvar Novo");
        Button btnEditar = new Button("Editar Existente");
        Button btnExcluir = new Button("Excluir");
        Button btnBuscar = new Button("Carregar Lista");

        TextArea areaResultados = new TextArea();
        areaResultados.setEditable(false);

        HBox boxBotoesAcao = new HBox(10);
        boxBotoesAcao.getChildren().addAll(btnSalvar, btnEditar, btnExcluir);

        btnSalvar.setOnAction(e -> {
            Admin novoAdmin = new Admin();
            novoAdmin.setNome(txtNome.getText());
            novoAdmin.setEmail(txtEmail.getText());
            novoAdmin.setSenha(txtSenha.getText());

            new Thread(() -> {
                RetornoOperacao retorno = repository.inserir(novoAdmin);

                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText(retorno.getMensagem());
                    alert.show();

                    if(retorno.getStatus().equals("ok")) {
                        txtNome.clear(); txtEmail.clear(); txtSenha.clear(); txtId.clear();
                        btnBuscar.fire();
                    }
                });
            }).start();
        });

        btnEditar.setOnAction(e -> {
            try {
                Admin editado = new Admin();
                editado.setId(Integer.parseInt(txtId.getText()));
                editado.setNome(txtNome.getText());
                editado.setEmail(txtEmail.getText());
                editado.setSenha(txtSenha.getText());

                new Thread(() -> {
                    RetornoOperacao retorno = repository.alterar(editado);

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText(retorno.getMensagem());
                        alert.show();

                        if(retorno.getStatus().equals("ok")) {
                            txtNome.clear(); txtEmail.clear(); txtSenha.clear(); txtId.clear();
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
                        ArrayList<Admin> listaLocal = (ArrayList<Admin>) retorno.getData();

                        for (Admin admin : listaLocal) {
                            areaResultados.appendText("ID: " + admin.getId() +
                                    " | Nome: " + admin.getNome() +
                                    " | Email: " + admin.getEmail() + "\n");
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
                lblEmail, txtEmail,
                lblSenha, txtSenha,
                boxBotoesAcao,
                new Separator(),
                btnBuscar, areaResultados
        );

        return layout;
    }
}
