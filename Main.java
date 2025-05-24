package A09_112201039;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("start.fxml")); // 從開始畫面開始
        primaryStage.setTitle("古代樹迷宮冒險");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false); // 固定視窗大小
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}