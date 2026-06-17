package ProjetoRA3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    private final AcademiaRepository repository = new AcademiaRepository();

    @Override
    public void start(Stage stage) {
        Label lblId = new Label("ID (Informar somente para editar/excluir):");
        TextField txtId = new TextField();

        Label lblNome = new Label("Nome:");
        TextField txtNome = new TextField();

        Label lblCnpj = new Label("CNPJ:");
        TextField txtCnpj = new TextField();

        Label lblEndereco = new Label("Endereço:");
        TextField txtEndereco = new TextField();

        Button btnSalvar = new Button("Salvar Nova");
        Button btnEditar = new Button("Editar Existente");
        Button btnExcluir = new Button("Excluir");
        Button btnBuscar = new Button("Carregar Lista");

        TextArea areaResultados = new TextArea();
        areaResultados.setEditable(false);

        HBox boxBotoesAcao = new HBox(10);
        boxBotoesAcao.getChildren().addAll(btnSalvar, btnEditar, btnExcluir);

        btnSalvar.setOnAction(e -> {
            AcademiaLuta nova = new AcademiaLuta();
            nova.setNome(txtNome.getText());
            nova.setCnpj(txtCnpj.getText());
            nova.setEndereco(txtEndereco.getText());

            new Thread(() -> {
                RetornoOperacao retorno = repository.inserir(nova);

                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText(retorno.getMensagem());
                    alert.show();

                    if(retorno.getStatus().equals("ok")) {
                        txtNome.clear(); txtCnpj.clear(); txtEndereco.clear(); txtId.clear();
                        btnBuscar.fire();
                    }
                });
            }).start();
        });

        btnEditar.setOnAction(e -> {
            try {
                AcademiaLuta editada = new AcademiaLuta();
                editada.setId(Integer.parseInt(txtId.getText()));
                editada.setNome(txtNome.getText());
                editada.setCnpj(txtCnpj.getText());
                editada.setEndereco(txtEndereco.getText());

                new Thread(() -> {
                    RetornoOperacao retorno = repository.alterar(editada);

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText(retorno.getMensagem());
                        alert.show();

                        if(retorno.getStatus().equals("ok")) {
                            txtNome.clear(); txtCnpj.clear(); txtEndereco.clear(); txtId.clear();
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
                        ArrayList<AcademiaLuta> listaLocal = (ArrayList<AcademiaLuta>) retorno.getData();

                        for (AcademiaLuta ac : listaLocal) {
                            areaResultados.appendText("ID: " + ac.getId() +
                                    " | Nome: " + ac.getNome() +
                                    " | CNPJ: " + ac.getCnpj() + "\n");
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
                lblCnpj, txtCnpj,
                lblEndereco, txtEndereco,
                boxBotoesAcao,
                new Separator(),
                btnBuscar, areaResultados
        );

        Scene cena = new Scene(layout, 500, 600);
        stage.setTitle("Gestão de Academias");
        stage.setScene(cena);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}