package ProjetoRA3.UI;

import java.util.ArrayList;

import ProjetoRA3.ModelAviso.AvisoRepository;
import ProjetoRA3.ModelAviso.Aviso;
import ProjetoRA3.ModelAviso.RetornoOperacao;
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

public class AvisoPanel {

    private final AvisoRepository repository = new AvisoRepository();

    public VBox createPanel() {
        Label lblId = new Label("ID (Informar somente para editar/excluir):");
        TextField txtId = new TextField();

        Label lblTitulo = new Label("Título do Aviso:");
        TextField txtTitulo = new TextField();

        Label lblData = new Label("Data (DD/MM/AAAA):");
        TextField txtData = new TextField();

        Label lblMensagem = new Label("Mensagem:");
        TextArea txtMensagem = new TextArea();
        txtMensagem.setPrefRowCount(3);

        Button btnSalvar = new Button("Salvar Novo");
        Button btnEditar = new Button("Editar Existente");
        Button btnExcluir = new Button("Excluir");
        Button btnBuscar = new Button("Carregar Lista");

        TextArea areaResultados = new TextArea();
        areaResultados.setEditable(false);

        HBox boxBotoesAcao = new HBox(10);
        boxBotoesAcao.getChildren().addAll(btnSalvar, btnEditar, btnExcluir);

        btnSalvar.setOnAction(e -> {
            Aviso novo = new Aviso();
            novo.setTitulo(txtTitulo.getText());
            novo.setDataPublicacao(txtData.getText());
            novo.setMensagem(txtMensagem.getText());

            new Thread(() -> {
                RetornoOperacao retorno = repository.inserir(novo);

                Platform.runLater(() -> {
                    Alert alert = new Alert(retorno.getStatus().equals("ok") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                    alert.setContentText(retorno.getMensagem());
                    alert.show();

                    if(retorno.getStatus().equals("ok")) {
                        txtTitulo.clear(); txtData.clear(); txtMensagem.clear(); txtId.clear();
                        btnBuscar.fire();
                    }
                });
            }).start();
        });

        btnEditar.setOnAction(e -> {
            try {
                Aviso editado = new Aviso();
                editado.setId(Integer.parseInt(txtId.getText()));
                editado.setTitulo(txtTitulo.getText());
                editado.setDataPublicacao(txtData.getText());
                editado.setMensagem(txtMensagem.getText());

                new Thread(() -> {
                    RetornoOperacao retorno = repository.alterar(editado);

                    Platform.runLater(() -> {
                        Alert alert = new Alert(retorno.getStatus().equals("ok") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                        alert.setContentText(retorno.getMensagem());
                        alert.show();

                        if(retorno.getStatus().equals("ok")) {
                            txtTitulo.clear(); txtData.clear(); txtMensagem.clear(); txtId.clear();
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
                        ArrayList<Aviso> listaLocal = (ArrayList<Aviso>) retorno.getData();

                        for (Aviso aviso : listaLocal) {
                            areaResultados.appendText("ID: " + aviso.getId() +
                                    " | Título: " + aviso.getTitulo() +
                                    " | Data: " + aviso.getDataPublicacao() +
                                    "\nMsg: " + aviso.getMensagem() + "\n" +
                                    "-----------------------------------\n");
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
                lblTitulo, txtTitulo,
                lblData, txtData,
                lblMensagem, txtMensagem,
                boxBotoesAcao,
                new Separator(),
                btnBuscar, areaResultados
        );

        return layout;
    }
}