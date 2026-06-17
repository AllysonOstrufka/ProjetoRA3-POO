package ProjetoRA3.UI;

import java.util.ArrayList;

import ProjetoRA3.ModelGraduacao.GraduacaoFaixa;
import ProjetoRA3.ModelGraduacao.GraduacaoRepository;
import ProjetoRA3.ModelGraduacao.RetornoOperacao;
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

public class GraduacaoPanel {

    private final GraduacaoRepository repository = new GraduacaoRepository();

    public VBox createPanel() {
        Label lblId = new Label("ID (Informar somente para editar/excluir):");
        TextField txtId = new TextField();

        Label lblCorFaixa = new Label("Cor da faixa:");
        TextField txtCorFaixa = new TextField();

        Label lblHierarquia = new Label("Hierarquia (Ordem da faixa):");
        TextField txtHierarquia = new TextField();

        Label lblTempoMinimo = new Label("Tempo mínimo (meses):");
        TextField txtTempoMinimo = new TextField();

        Button btnSalvar = new Button("Salvar Nova");
        Button btnEditar = new Button("Editar Existente");
        Button btnExcluir = new Button("Excluir");
        Button btnBuscar = new Button("Carregar Lista");

        TextArea areaResultados = new TextArea();
        areaResultados.setEditable(false);

        HBox boxBotoesAcao = new HBox(10);
        boxBotoesAcao.getChildren().addAll(btnSalvar, btnEditar, btnExcluir);

        btnSalvar.setOnAction(e -> {
            try {
                GraduacaoFaixa nova = new GraduacaoFaixa();
                nova.setCorFaixa(txtCorFaixa.getText());
                nova.setHierarquia(Integer.parseInt(txtHierarquia.getText()));
                nova.setTempoMinimoMeses(Integer.parseInt(txtTempoMinimo.getText()));

                new Thread(() -> {
                    RetornoOperacao retorno = repository.inserir(nova);

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText(retorno.getMensagem());
                        alert.show();

                        if(retorno.getStatus().equals("ok")) {
                            txtCorFaixa.clear(); txtHierarquia.clear(); txtTempoMinimo.clear(); txtId.clear();
                            btnBuscar.fire();
                        }
                    });
                }).start();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Hierarquia e Tempo mínimo devem ser números inteiros.");
                alert.show();
            }
        });

        btnEditar.setOnAction(e -> {
            try {
                GraduacaoFaixa editada = new GraduacaoFaixa();
                editada.setId(Integer.parseInt(txtId.getText()));
                editada.setCorFaixa(txtCorFaixa.getText());
                editada.setHierarquia(Integer.parseInt(txtHierarquia.getText()));
                editada.setTempoMinimoMeses(Integer.parseInt(txtTempoMinimo.getText()));

                new Thread(() -> {
                    RetornoOperacao retorno = repository.alterar(editada);

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText(retorno.getMensagem());
                        alert.show();

                        if(retorno.getStatus().equals("ok")) {
                            txtCorFaixa.clear(); txtHierarquia.clear(); txtTempoMinimo.clear(); txtId.clear();
                            btnBuscar.fire();
                        }
                    });
                }).start();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("ID, Hierarquia e Tempo mínimo devem ser números inteiros.");
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
                        ArrayList<GraduacaoFaixa> listaLocal = (ArrayList<GraduacaoFaixa>) retorno.getData();

                        for (GraduacaoFaixa g : listaLocal) {
                            areaResultados.appendText("ID: " + g.getId() +
                                    " | Cor da faixa: " + g.getCorFaixa() +
                                    " | Hierarquia: " + g.getHierarquia() +
                                    " | Tempo mínimo: " + g.getTempoMinimoMeses() + " meses\n");
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
                lblCorFaixa, txtCorFaixa,
                lblHierarquia, txtHierarquia,
                lblTempoMinimo, txtTempoMinimo,
                boxBotoesAcao,
                new Separator(),
                btnBuscar, areaResultados
        );

        return layout;
    }
}
