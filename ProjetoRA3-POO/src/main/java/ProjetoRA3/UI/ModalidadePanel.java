package ProjetoRA3.UI;

import java.util.ArrayList;

import ProjetoRA3.ModelAcademia.AcademiaLuta;
import ProjetoRA3.ModelAcademia.AcademiaRepository;
import ProjetoRA3.ModelModalidade.Modalidade;
import ProjetoRA3.ModelModalidade.ModalidadeRepository;
import ProjetoRA3.ModelModalidade.RetornoOperacao;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class ModalidadePanel {

    private final ModalidadeRepository repository = new ModalidadeRepository();
    private final AcademiaRepository academiaRepository = new AcademiaRepository();

    public VBox createPanel() {
        Label lblId = new Label("ID (Informar somente para editar/excluir):");
        TextField txtId = new TextField();

        Label lblTipo = new Label("Tipo:");
        TextField txtTipo = new TextField();

        Label lblDescricao = new Label("Descrição:");
        TextField txtDescricao = new TextField();

        Label lblCargaHoraria = new Label("Carga horária:");
        TextField txtCargaHoraria = new TextField();

        Label lblAcademia = new Label("Academia:");
        ComboBox<AcademiaLuta> comboAcademia = new ComboBox<>();
        comboAcademia.setConverter(new StringConverter<AcademiaLuta>() {
            @Override
            public String toString(AcademiaLuta academia) {
                return academia == null ? "" : academia.getNome();
            }

            @Override
            public AcademiaLuta fromString(String string) {
                return null;
            }
        });
        carregarAcademias(comboAcademia);
        comboAcademia.setOnShowing(e -> carregarAcademias(comboAcademia));

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
                Modalidade nova = new Modalidade();
                nova.setTipo(txtTipo.getText());
                nova.setDescricao(txtDescricao.getText());
                nova.setCargaHoraria(Integer.parseInt(txtCargaHoraria.getText()));
                AcademiaLuta academiaSelecionada = comboAcademia.getValue();
                nova.setAcademiaId(academiaSelecionada == null ? null : academiaSelecionada.getId());

                new Thread(() -> {
                    RetornoOperacao retorno = repository.inserir(nova);

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText(retorno.getMensagem());
                        alert.show();

                        if(retorno.getStatus().equals("ok")) {
                            txtTipo.clear(); txtDescricao.clear(); txtCargaHoraria.clear(); txtId.clear();
                            comboAcademia.setValue(null);
                            btnBuscar.fire();
                        }
                    });
                }).start();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("O campo Carga horária deve ser numérico.");
                alert.show();
            }
        });

        btnEditar.setOnAction(e -> {
            try {
                Modalidade editada = new Modalidade();
                editada.setId(Integer.parseInt(txtId.getText()));
                editada.setTipo(txtTipo.getText());
                editada.setDescricao(txtDescricao.getText());
                editada.setCargaHoraria(Integer.parseInt(txtCargaHoraria.getText()));
                AcademiaLuta academiaSelecionada = comboAcademia.getValue();
                editada.setAcademiaId(academiaSelecionada == null ? null : academiaSelecionada.getId());

                new Thread(() -> {
                    RetornoOperacao retorno = repository.alterar(editada);

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText(retorno.getMensagem());
                        alert.show();

                        if(retorno.getStatus().equals("ok")) {
                            txtTipo.clear(); txtDescricao.clear(); txtCargaHoraria.clear(); txtId.clear();
                            comboAcademia.setValue(null);
                            btnBuscar.fire();
                        }
                    });
                }).start();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("ID e Carga horária devem ser numéricos.");
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
                        ArrayList<Modalidade> listaLocal = (ArrayList<Modalidade>) retorno.getData();

                        for (Modalidade m : listaLocal) {
                            areaResultados.appendText("ID: " + m.getId() +
                                    " | Tipo: " + m.getTipo() +
                                    " | Descrição: " + m.getDescricao() +
                                    " | Carga horária: " + m.getCargaHoraria() +
                                    " | Academia ID: " + m.getAcademiaId() + "\n");
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
                lblTipo, txtTipo,
                lblDescricao, txtDescricao,
                lblCargaHoraria, txtCargaHoraria,
                lblAcademia, comboAcademia,
                boxBotoesAcao,
                new Separator(),
                btnBuscar, areaResultados
        );

        return layout;
    }

    private void carregarAcademias(ComboBox<AcademiaLuta> comboAcademia) {
        ProjetoRA3.ModelAcademia.RetornoOperacao retorno = academiaRepository.buscarTodos();
        if (retorno.getStatus().equals("ok")) {
            ArrayList<AcademiaLuta> listaLocal = (ArrayList<AcademiaLuta>) retorno.getData();
            comboAcademia.getItems().setAll(listaLocal);
        }
    }
}
