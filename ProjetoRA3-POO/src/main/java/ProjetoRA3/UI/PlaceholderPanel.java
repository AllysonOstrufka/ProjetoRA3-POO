package ProjetoRA3.UI;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PlaceholderPanel {

    public VBox createPanel(String titulo) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5;");

        Label lblTitulo = new Label("Aba: " + titulo);
        lblTitulo.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Label lblMensagem = new Label("Este módulo será adicionado em breve!");
        lblMensagem.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");

        layout.getChildren().addAll(lblTitulo, lblMensagem);

        return layout;
    }
}
