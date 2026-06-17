package ProjetoRA3.UI;

import java.util.ArrayList;

import ProjetoRA3.ModelAdmin.RetornoOperacao;
import ProjetoRA3.ModelRegras.Regras;
import ProjetoRA3.ModelRegras.RegrasRepository;
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

public class RegrasPanel {

    private final RegrasRepository repository = new RegrasRepository();

    public VBox createPanel() {
        Label lblId = new Label("ID (Informar somente para editar/excluir):");
        TextField txtId = new TextField();

        Label lblDescricao = new Label("Descrição:");
        TextField txtDescricao = new TextField();

        Label lblTempoMinimo = new Label("Tempo Mínimo (em meses):");
        TextField txtTempoMinimo = new TextField();

        Button btnSalvar = new Button("Salvar Novo");
        Button btnEditar = new Button("Editar Existente");
        Button btnExcluir = new Button("Excluir");
        Button btnBuscar = new Button("Carregar Lista");

        TextArea areaResultados = new TextArea();
        areaResultados.setEditable(false);

        HBox boxBotoesAcao = new HBox(10);
        boxBotoesAcao.getChildren().addAll(btnSalvar, btnEditar, btnExcluir);

        btnSalvar.setOnAction(e -> {
            Regras novaRegra = new Regras();
            novaRegra.setDescricao(txtDescricao.getText());

            try {
                Integer tempoMinimo = Integer.parseInt(txtTempoMinimo.getText());
                novaRegra.setTempoMinimo(tempoMinimo);

                new Thread(() -> {
                    RetornoOperacao retorno = repository.inserir(novaRegra);

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText(retorno.getMensagem());
                        alert.show();

                        if(retorno.getStatus().equals("ok")) {
                            txtDescricao.clear(); txtTempoMinimo.clear(); txtId.clear();
                            btnBuscar.fire();
                        }
                    });
                }).start();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Tempo mínimo deve ser um número inteiro.");
                alert.show();
            }
        });

        btnEditar.setOnAction(e -> {
            try {
                Regras editada = new Regras();
                editada.setId(Integer.parseInt(txtId.getText()));
                editada.setDescricao(txtDescricao.getText());

                try {
                    Integer tempoMinimo = Integer.parseInt(txtTempoMinimo.getText());
                    editada.setTempoMinimo(tempoMinimo);

                    new Thread(() -> {
                        RetornoOperacao retorno = repository.alterar(editada);

                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText(retorno.getMensagem());
                            alert.show();

                            if(retorno.getStatus().equals("ok")) {
                                txtDescricao.clear(); txtTempoMinimo.clear(); txtId.clear();
                                btnBuscar.fire();
                            }
                        });
                    }).start();
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Tempo mínimo deve ser um número inteiro.");
                    alert.show();
                }
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
                        ArrayList<Regras> listaLocal = (ArrayList<Regras>) retorno.getData();

                        for (Regras regra : listaLocal) {
                            areaResultados.appendText("ID: " + regra.getId() +
                                    " | Descrição: " + regra.getDescricao() +
                                    " | Tempo Mínimo: " + regra.getTempoMinimo() + " meses\n");
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
                lblDescricao, txtDescricao,
                lblTempoMinimo, txtTempoMinimo,
                boxBotoesAcao,
                new Separator(),
                btnBuscar, areaResultados
        );

        return layout;
    }
}
