package A09_112201039; // 將其替換為你的實際 package 名稱

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class controller {

    @FXML
    private Label myLabel; // 綁定 FXML 中對應的 Label 控件

    // 一個初始化方法
    @FXML
    public void initialize() {
        myLabel.setText("Hello, JavaFX!"); // 初始化標籤文字
    }
}
