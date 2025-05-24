package A09_112201039;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MazeGameController {

    // 開始畫面控件
    @FXML private Button startButton;
    @FXML private Button nextTimeButton;
    @FXML private Label titleLabel;

    // 遊戲畫面控件
    @FXML private GridPane mazeGrid;
    @FXML private Button northButton;
    @FXML private Button southButton;
    @FXML private Button eastButton;
    @FXML private Button westButton;

    // 完成畫面控件
    @FXML private Button playAgainButton;
    @FXML private Button quitButton;
    @FXML private Label congratsLabel;

    // 遊戲狀態
    private int[][] maze;
    private int playerRow, playerCol;
    private int goalRow, goalCol;
    private int mazeSize = 8;
    private Rectangle[][] tiles;

    // 迷宮類型：0=路徑(白), 1=牆壁(灰), 2=玩家(黑), 3=目標(黃)
    private static final int PATH = 0;
    private static final int WALL = 1;
    private static final int PLAYER = 2;
    private static final int GOAL = 3;

    @FXML
    public void initialize() {
        if (titleLabel != null) {
            // 開始畫面初始化
            titleLabel.setText("古代樹迷宮冒險");
        }

        if (congratsLabel != null) {
            // 完成畫面初始化
            congratsLabel.setText("恭喜抵達終點!");
        }

        if (mazeGrid != null) {
            // 遊戲畫面初始化
            initializeMaze();
            createMazeDisplay();
            updateButtonStates();
        }
    }

    private void initializeMaze() {
        maze = new int[mazeSize][mazeSize];
        tiles = new Rectangle[mazeSize][mazeSize];

        // 創建一個簡單的迷宮布局
        // 初始化為牆壁
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                maze[i][j] = WALL;
            }
        }

        // 創建路徑
        int[][] paths = {
                {1,1}, {1,2}, {1,3}, {1,4}, {1,5}, {1,6},
                {2,1}, {2,6},
                {3,1}, {3,2}, {3,3}, {3,4}, {3,6},
                {4,4}, {4,6},
                {5,1}, {5,2}, {5,4}, {5,5}, {5,6},
                {6,1}, {6,4}
        };

        for (int[] path : paths) {
            maze[path[0]][path[1]] = PATH;
        }

        // 設置玩家起始位置
        playerRow = 1;
        playerCol = 1;
        maze[playerRow][playerCol] = PLAYER;

        // 設置目標位置
        goalRow = 6;
        goalCol = 4;
        maze[goalRow][goalCol] = GOAL;
    }

    private void createMazeDisplay() {
        mazeGrid.getChildren().clear();

        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                StackPane cell = new StackPane();
                Rectangle tile = new Rectangle(40, 40);
                tiles[i][j] = tile;

                updateTileColor(i, j);

                cell.getChildren().add(tile);
                mazeGrid.add(cell, j, i);
            }
        }
    }

    private void updateTileColor(int row, int col) {
        Rectangle tile = tiles[row][col];

        switch (maze[row][col]) {
            case PATH:
                tile.setFill(Color.WHITE);
                tile.setStroke(Color.LIGHTGRAY);
                break;
            case WALL:
                tile.setFill(Color.GRAY);
                tile.setStroke(Color.DARKGRAY);
                break;
            case PLAYER:
                tile.setFill(Color.BLACK);
                tile.setStroke(Color.DARKGRAY);
                break;
            case GOAL:
                tile.setFill(Color.YELLOW);
                tile.setStroke(Color.ORANGE);
                break;
        }
    }

    @FXML
    private void handleStart() {
        switchToScene("game.fxml");
    }

    @FXML
    private void handleNextTime() {
        System.exit(0);
    }

    @FXML
    private void moveNorth() {
        movePlayer(-1, 0);
    }

    @FXML
    private void moveSouth() {
        movePlayer(1, 0);
    }

    @FXML
    private void moveEast() {
        movePlayer(0, 1);
    }

    @FXML
    private void moveWest() {
        movePlayer(0, -1);
    }

    private void movePlayer(int deltaRow, int deltaCol) {
        int newRow = playerRow + deltaRow;
        int newCol = playerCol + deltaCol;

        // 檢查邊界和牆壁
        if (newRow < 0 || newRow >= mazeSize || newCol < 0 || newCol >= mazeSize) {
            return;
        }

        if (maze[newRow][newCol] == WALL) {
            return;
        }

        // 清除舊位置
        maze[playerRow][playerCol] = PATH;
        updateTileColor(playerRow, playerCol);

        // 移動到新位置
        playerRow = newRow;
        playerCol = newCol;

        // 檢查是否到達目標
        if (playerRow == goalRow && playerCol == goalCol) {
            switchToScene("complete.fxml");
            return;
        }

        // 更新玩家位置
        maze[playerRow][playerCol] = PLAYER;
        updateTileColor(playerRow, playerCol);

        // 更新按鈕狀態
        updateButtonStates();
    }

    private void updateButtonStates() {
        northButton.setDisable(!canMove(-1, 0));
        southButton.setDisable(!canMove(1, 0));
        eastButton.setDisable(!canMove(0, 1));
        westButton.setDisable(!canMove(0, -1));
    }

    private boolean canMove(int deltaRow, int deltaCol) {
        int newRow = playerRow + deltaRow;
        int newCol = playerCol + deltaCol;

        if (newRow < 0 || newRow >= mazeSize || newCol < 0 || newCol >= mazeSize) {
            return false;
        }

        return maze[newRow][newCol] != WALL;
    }

    @FXML
    private void handlePlayAgain() {
        switchToScene("start.fxml");
    }

    @FXML
    private void handleQuit() {
        System.exit(0);
    }

    private void switchToScene(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) (startButton != null ? startButton.getScene().getWindow() :
                    northButton != null ? northButton.getScene().getWindow() :
                            playAgainButton.getScene().getWindow());
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}