package A09_112201039;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.util.Duration;
import java.util.*;

public class MazeGameController {

    // 開始畫面控件
    @FXML private Button startButton;
    @FXML private Button nextTimeButton;
    @FXML private Label titleLabel;

    // 遊戲畫面控件
    @FXML private GridPane mazeGrid;
    @FXML private Pane view3D;
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
    private int mazeSize = 9; // 奇數尺寸便於生成迷宮
    private Rectangle[][] tiles;
    private Random random = new Random();

    // 迷宮類型：0=路徑(白), 1=牆壁(灰), 2=玩家(黑), 3=目標(黃)
    private static final int PATH = 0;
    private static final int WALL = 1;
    private static final int PLAYER = 2;
    private static final int GOAL = 3;

    // 方向常數
    private static final int[] dx = {-2, 2, 0, 0}; // 上, 下, 左, 右
    private static final int[] dy = {0, 0, -2, 2};

    @FXML
    public void initialize() {
        if (titleLabel != null) {
            titleLabel.setText("古代樹迷宮冒險");
        }

        if (congratsLabel != null) {
            congratsLabel.setText("恭喜抵達終點!");
        }

        if (mazeGrid != null) {
            generateRandomMaze();
            createMazeDisplay();
            update3DView();
            updateButtonStates();
        }
    }

    private void generateRandomMaze() {
        maze = new int[mazeSize][mazeSize];
        tiles = new Rectangle[mazeSize][mazeSize];

        // 初始化為牆壁
        for (int i = 0; i < mazeSize; i++) {
            Arrays.fill(maze[i], WALL);
        }

        // 使用遞歸回溯算法生成迷宮
        Stack<int[]> stack = new Stack<>();
        playerRow = 1;
        playerCol = 1;
        maze[playerRow][playerCol] = PATH;
        stack.push(new int[]{playerRow, playerCol});

        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int x = current[0], y = current[1];

            List<Integer> neighbors = getUnvisitedNeighbors(x, y);

            if (!neighbors.isEmpty()) {
                int direction = neighbors.get(random.nextInt(neighbors.size()));
                int nx = x + dx[direction];
                int ny = y + dy[direction];

                // 連接當前格子和鄰居
                maze[x + dx[direction]/2][y + dy[direction]/2] = PATH;
                maze[nx][ny] = PATH;
                stack.push(new int[]{nx, ny});
            } else {
                stack.pop();
            }
        }

        // 確保起點和終點
        maze[playerRow][playerCol] = PLAYER;

        // 選擇一個遠離起點的位置作為終點
        goalRow = mazeSize - 2;
        goalCol = mazeSize - 2;
        maze[goalRow][goalCol] = GOAL;

        // 確保終點可達
        if (maze[goalRow-1][goalCol] == WALL && maze[goalRow][goalCol-1] == WALL) {
            maze[goalRow-1][goalCol] = PATH;
        }
    }

    private List<Integer> getUnvisitedNeighbors(int x, int y) {
        List<Integer> neighbors = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];

            if (nx > 0 && nx < mazeSize-1 && ny > 0 && ny < mazeSize-1 && maze[nx][ny] == WALL) {
                neighbors.add(i);
            }
        }

        return neighbors;
    }

    private void createMazeDisplay() {
        mazeGrid.getChildren().clear();

        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                StackPane cell = new StackPane();
                Rectangle tile = new Rectangle(30, 30);
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

    private void update3DView() {
        if (view3D == null) return;

        // 淡出舊視圖
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), view3D);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.3);

        fadeOut.setOnFinished(e -> {
            view3D.getChildren().clear();

            // 3D視角參數
            double centerX = 150; // view3D寬度的一半
            double centerY = 125; // view3D高度的一半

            // 繪製黑白3D迷宮視角
            drawBWWalls(centerX, centerY);
            drawBWFloor(centerX, centerY);
            drawBWGoal(centerX, centerY);

            // 淡入新視圖
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), view3D);
            fadeIn.setFromValue(0.3);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }

    private void drawBWWalls(double centerX, double centerY) {
        // 前方牆壁 - 純黑色
        if (isWall(playerRow - 1, playerCol)) {
            Polygon frontWall = new Polygon();
            frontWall.getPoints().addAll(new Double[]{
                    centerX - 50.0, centerY - 35.0,
                    centerX + 50.0, centerY - 35.0,
                    centerX + 50.0, centerY + 35.0,
                    centerX - 50.0, centerY + 35.0
            });
            frontWall.setFill(Color.BLACK);
            frontWall.setStroke(Color.WHITE);
            frontWall.setStrokeWidth(2);

            // 添加入場動畫
            ScaleTransition scale = new ScaleTransition(Duration.millis(300), frontWall);
            scale.setFromX(0.8);
            scale.setFromY(0.8);
            scale.setToX(1.0);
            scale.setToY(1.0);

            view3D.getChildren().add(frontWall);
            scale.play();
        }

        // 左側牆壁 - 深灰色
        if (isWall(playerRow, playerCol - 1)) {
            Polygon leftWall = new Polygon();
            leftWall.getPoints().addAll(new Double[]{
                    centerX - 100.0, centerY - 50.0,
                    centerX - 50.0, centerY - 35.0,
                    centerX - 50.0, centerY + 35.0,
                    centerX - 100.0, centerY + 50.0
            });
            leftWall.setFill(Color.DARKGRAY);
            leftWall.setStroke(Color.WHITE);
            leftWall.setStrokeWidth(1.5);

            // 滑入動畫
            TranslateTransition slide = new TranslateTransition(Duration.millis(250), leftWall);
            slide.setFromX(-30);
            slide.setToX(0);

            view3D.getChildren().add(leftWall);
            slide.play();
        }

        // 右側牆壁 - 深灰色
        if (isWall(playerRow, playerCol + 1)) {
            Polygon rightWall = new Polygon();
            rightWall.getPoints().addAll(new Double[]{
                    centerX + 50.0, centerY - 35.0,
                    centerX + 100.0, centerY - 50.0,
                    centerX + 100.0, centerY + 50.0,
                    centerX + 50.0, centerY + 35.0
            });
            rightWall.setFill(Color.DARKGRAY);
            rightWall.setStroke(Color.WHITE);
            rightWall.setStrokeWidth(1.5);

            // 滑入動畫
            TranslateTransition slide = new TranslateTransition(Duration.millis(250), rightWall);
            slide.setFromX(30);
            slide.setToX(0);

            view3D.getChildren().add(rightWall);
            slide.play();
        }

        // 遠處牆壁 - 淺灰色
        if (!isWall(playerRow - 1, playerCol) && isWall(playerRow - 2, playerCol)) {
            Polygon distantWall = new Polygon();
            distantWall.getPoints().addAll(new Double[]{
                    centerX - 25.0, centerY - 18.0,
                    centerX + 25.0, centerY - 18.0,
                    centerX + 25.0, centerY + 18.0,
                    centerX - 25.0, centerY + 18.0
            });
            distantWall.setFill(Color.LIGHTGRAY);
            distantWall.setStroke(Color.GRAY);
            distantWall.setStrokeWidth(1);

            // 縮放進入動畫
            ScaleTransition scale = new ScaleTransition(Duration.millis(400), distantWall);
            scale.setFromX(0.5);
            scale.setFromY(0.5);
            scale.setToX(1.0);
            scale.setToY(1.0);

            view3D.getChildren().add(distantWall);
            scale.play();
        }
    }

    private void drawBWFloor(double centerX, double centerY) {
        // 地板 - 白色帶黑色線條
        Polygon floor = new Polygon();
        floor.getPoints().addAll(new Double[]{
                centerX - 100.0, centerY + 50.0,
                centerX + 100.0, centerY + 50.0,
                centerX + 50.0, centerY + 35.0,
                centerX - 50.0, centerY + 35.0
        });
        floor.setFill(Color.WHITE);
        floor.setStroke(Color.BLACK);
        floor.setStrokeWidth(2);

        // 地板網格線
        for (int i = 1; i < 4; i++) {
            Polygon gridLine = new Polygon();
            double y = centerY + 35 + (15.0/4) * i;
            double width = 100 - (50.0/4) * i;
            gridLine.getPoints().addAll(new Double[]{
                    centerX - width, y,
                    centerX + width, y,
                    centerX + width, y + 1,
                    centerX - width, y + 1
            });
            gridLine.setFill(Color.LIGHTGRAY);
            view3D.getChildren().add(gridLine);
        }

        // 天花板 - 淺灰色
        Polygon ceiling = new Polygon();
        ceiling.getPoints().addAll(new Double[]{
                centerX - 50.0, centerY - 35.0,
                centerX + 50.0, centerY - 35.0,
                centerX + 100.0, centerY - 50.0,
                centerX - 100.0, centerY - 50.0
        });
        ceiling.setFill(Color.LIGHTGRAY);
        ceiling.setStroke(Color.BLACK);
        ceiling.setStrokeWidth(1.5);

        view3D.getChildren().addAll(floor, ceiling);

        // 地板淡入動畫
        FadeTransition floorFade = new FadeTransition(Duration.millis(300), floor);
        floorFade.setFromValue(0.5);
        floorFade.setToValue(1.0);

        FadeTransition ceilingFade = new FadeTransition(Duration.millis(300), ceiling);
        ceilingFade.setFromValue(0.5);
        ceilingFade.setToValue(1.0);

        ParallelTransition parallel = new ParallelTransition(floorFade, ceilingFade);
        parallel.play();
    }

    private void drawBWGoal(double centerX, double centerY) {
        // 如果目標在視線範圍內，繪製閃爍的白色方塊
        if (Math.abs(playerRow - goalRow) <= 2 && Math.abs(playerCol - goalCol) <= 2) {
            Rectangle glow = new Rectangle(centerX - 8, centerY - 8, 16, 16);
            glow.setFill(Color.WHITE);
            glow.setStroke(Color.BLACK);
            glow.setStrokeWidth(2);

            // 閃爍動畫
            FadeTransition blink = new FadeTransition(Duration.millis(800), glow);
            blink.setFromValue(1.0);
            blink.setToValue(0.3);
            blink.setCycleCount(FadeTransition.INDEFINITE);
            blink.setAutoReverse(true);

            view3D.getChildren().add(glow);
            blink.play();
        }
    }

    private boolean isWall(int row, int col) {
        if (row < 0 || row >= mazeSize || col < 0 || col >= mazeSize) {
            return true;
        }
        return maze[row][col] == WALL;
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

        // 更新3D視角和按鈕狀態
        update3DView();
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