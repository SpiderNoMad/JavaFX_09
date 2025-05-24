package A09_112201039; // 替換為實際使用的包名

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml")); // 載入 FXML 文件
        primaryStage.setTitle("JavaFX Sample");
        primaryStage.setScene(new Scene(root, 400, 300)); // 設定視窗大小
        primaryStage.show(); // 顯示視窗
    }

    public static void main(String[] args) {
        launch(args); // 啟動應用程式
    }
}
