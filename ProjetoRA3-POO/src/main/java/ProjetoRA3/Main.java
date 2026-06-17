package ProjetoRA3;

import ProjetoRA3.UI.AcademiaPanel;
import ProjetoRA3.UI.AdminPanel;
import ProjetoRA3.UI.AlunoPanel;
import ProjetoRA3.UI.PlaceholderPanel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);


        Tab tabAcademias = new Tab("Academias", new AcademiaPanel().createPanel());
        tabAcademias.setStyle("-fx-text-base-color: #333;");


        Tab tabAlunos = new Tab("Alunos", new AlunoPanel().createPanel());
        tabAlunos.setStyle("-fx-text-base-color: #333;");


        Tab tabAdmins = new Tab("Admins", new AdminPanel().createPanel());
        tabAdmins.setStyle("-fx-text-base-color: #333;");

        
        Tab tabInstrutores = new Tab("Instrutores", new PlaceholderPanel().createPanel("Instrutores"));
        tabInstrutores.setStyle("-fx-text-base-color: #333;");

        //abas do TabPane
        tabPane.getTabs().addAll(tabAcademias, tabAlunos, tabAdmins, tabInstrutores);

        BorderPane root = new BorderPane();
        root.setCenter(tabPane);
        root.setPadding(new Insets(10));

        Scene cena = new Scene(root, 700, 700);
        stage.setTitle("Sistema de Gestão de Academias - KONECT");
        stage.setScene(cena);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}