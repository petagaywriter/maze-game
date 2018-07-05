/* Lab: Final Lab
 * Name: Charlene Turner 
 * Date: 12/05/2017
 * Description: Programm lanching a maze game that utilizes intersection to 
 * access scoring, win and fail states. 
 */
package pkgfinal;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class maze extends Application
{

    private static final Stage stage = new Stage();
    private static final Group playerGroup = new Group();
    private static Rectangle playerModel = null;
    private static int level = 0;
    private static Path maze = null;
    private static final ArrayList<Group> enemies = new ArrayList<>();
    private static final ArrayList<Group> collectibles = new ArrayList<>();
    private static int score = 0;
    private static Timeline timer = null;
    private static final int playerMove = 10;
    private static final int enemyMove = 50;
    private static final Duration enemySpeed = Duration.seconds(1);
    private static MediaPlayer levelPlayer = null;
    private static MediaPlayer failPlayer = null;
    private static MediaPlayer winPlayer = null;

    @Override
    public void start(Stage mainStage)
    {

        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(50));
        gridpane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(gridpane);

        levelPlayer = GenerateMedia("Lover_s_Stripes.mp3", true);
        failPlayer = GenerateMedia("Male_Crying.mp3", true);
        winPlayer = GenerateMedia("Flighty_Theme.mp3", true);

        Pane pane = new Pane();
        pane.setMinSize(500, 500);
        gridpane.getChildren().add(pane);

        Image icon = new Image("josh.png");
        ImageView player = new ImageView(icon);
        playerModel = new Rectangle(21, 25, Color.TRANSPARENT);

        playerGroup.getChildren().addAll(player, playerModel);

        // setup keyboard functionality
        scene.setOnKeyPressed(e
                ->
        {
            if (pane.getChildren().contains(playerGroup) && maze != null)
            {
                // if the player is added to the pane and the maze is loaded
                double oldX = playerGroup.getLayoutX();
                double oldY = playerGroup.getLayoutY();
                double newX;
                double newY;

                switch (e.getCode())
                {
                    case W:
                    case UP:
                        newX = playerGroup.getLayoutX();
                        newY = playerGroup.getLayoutY() - playerMove;
                        break;
                    case RIGHT:
                    case D:
                        newX = playerGroup.getLayoutX() + playerMove;
                        newY = playerGroup.getLayoutY();
                        break;
                    case DOWN:
                    case S:
                        newX = playerGroup.getLayoutX();
                        newY = playerGroup.getLayoutY() + playerMove;
                        break;
                    case LEFT:
                    case A:
                        newX = playerGroup.getLayoutX() - playerMove;
                        newY = playerGroup.getLayoutY();
                        break;
                    default:
                        return; // do nothing for other key strokes
                }

                if (newX > 0 && newY > 0 && newX < 500 && newY < 500)
                {
                    // if new coordinates are within maze
                    playerGroup.setLayoutX(newX);
                    playerGroup.setLayoutY(newY);

                    if (!Shape.intersect(playerModel, maze).getBoundsInLocal().isEmpty())
                    {
                        // player is running into maze set old coordinates back
                        // https://stackoverflow.com/a/27880408
                        playerGroup.setLayoutX(oldX);
                        playerGroup.setLayoutY(oldY);
                    }

                    if (CheckIfPlayerIsTouchingEnemy(pane))
                    {
                        return; // cancel method call if touching enemy
                    }

                    Group collectibleObtained = null;

                    for (Group collectible : collectibles)
                    {
                        for (Node child : collectible.getChildren())
                        {
                            if (child instanceof Shape && !Shape.intersect(playerModel, (Shape) child).getBoundsInLocal().isEmpty())
                            {
                                // if player has reached a collectible
                                collectibleObtained = collectible;
                                break; // break out of the loop
                            }
                        }
                    }

                    if (collectibleObtained != null)
                    {
                        pane.getChildren().remove(collectibleObtained);
                        collectibles.remove(collectibleObtained);
                        score += 500; // add to score
                    }

                    if (newX > 450 && newY > 470)
                    {
                        // player is at the end of the maze
                        score += 2000; // beat level score

                        if (timer != null)
                        {
                            timer.stop(); // stop enemy movements
                        }

                        if (level == 1)
                        {
                            Level2(pane);
                        } else
                        {
                            WinScreen(pane);
                        }
                    }
                }
            }
        });

        StartMenu(pane); // add start menu as the first screen
        stage.setTitle("Maze");
        stage.setScene(scene);
        stage.show();
    }

    //Method creates maze and player icon for level 1 using Path class.
    public static void Level1(Pane pane)
    {
        level = 1;
        score = 0;

        Path path = new Path(new MoveTo());
        path.setStrokeWidth(10);
        path.setStroke(Color.web("rgb(97, 49, 15)"));

        path.getElements().add(new LineTo(0, 150));
        path.getElements().add(new LineTo(50, 150));
        path.getElements().add(new MoveTo(0, 150));
        path.getElements().add(new LineTo(0, 400));
        path.getElements().add(new LineTo(50, 400));
        path.getElements().add(new MoveTo(0, 400));
        path.getElements().add(new LineTo(0, 500));
        path.getElements().add(new LineTo(250, 500));
        path.getElements().add(new LineTo(250, 400));
        path.getElements().add(new LineTo(300, 400));
        path.getElements().add(new LineTo(300, 350));
        path.getElements().add(new LineTo(350, 350));
        path.getElements().add(new MoveTo(300, 350));
        path.getElements().add(new MoveTo(300, 300));
        path.getElements().add(new LineTo(350, 300));
        path.getElements().add(new MoveTo(300, 300));
        path.getElements().add(new LineTo(300, 250));
        path.getElements().add(new LineTo(50, 250));
        path.getElements().add(new LineTo(50, 200));
        path.getElements().add(new MoveTo(50, 250));
        path.getElements().add(new LineTo(50, 350));
        path.getElements().add(new LineTo(250, 350));
        path.getElements().add(new LineTo(250, 300));
        path.getElements().add(new LineTo(100, 300));
        path.getElements().add(new MoveTo(200, 350));
        path.getElements().add(new LineTo(200, 400));
        path.getElements().add(new MoveTo(100, 350));
        path.getElements().add(new LineTo(100, 450));
        path.getElements().add(new LineTo(50, 450));
        path.getElements().add(new MoveTo(100, 450));
        path.getElements().add(new LineTo(200, 450));
        path.getElements().add(new MoveTo(150, 450));
        path.getElements().add(new LineTo(150, 400));
        path.getElements().add(new MoveTo(250, 250));
        path.getElements().add(new LineTo(250, 200));
        path.getElements().add(new LineTo(450, 200));
        path.getElements().add(new MoveTo(350, 200));
        path.getElements().add(new LineTo(350, 250));
        path.getElements().add(new LineTo(400, 250));
        path.getElements().add(new LineTo(400, 350));
        path.getElements().add(new LineTo(450, 350));
        path.getElements().add(new LineTo(450, 450));
        path.getElements().add(new MoveTo(450, 400));
        path.getElements().add(new LineTo(350, 400));
        path.getElements().add(new LineTo(350, 450));
        path.getElements().add(new LineTo(300, 450));
        path.getElements().add(new MoveTo(205, 500));
        path.getElements().add(new LineTo(450, 500));
        path.getElements().add(new MoveTo(400, 500));
        path.getElements().add(new LineTo(400, 450));
        path.getElements().add(new MoveTo(50, 0));
        path.getElements().add(new LineTo(500, 0));
        path.getElements().add(new MoveTo(200, 0));
        path.getElements().add(new LineTo(200, 100));
        path.getElements().add(new MoveTo(200, 50));
        path.getElements().add(new LineTo(100, 50));
        path.getElements().add(new MoveTo(50, 50));
        path.getElements().add(new LineTo(50, 100));
        path.getElements().add(new LineTo(150, 100));
        path.getElements().add(new LineTo(150, 150));
        path.getElements().add(new MoveTo(100, 100));
        path.getElements().add(new LineTo(100, 200));
        path.getElements().add(new LineTo(200, 200));
        path.getElements().add(new LineTo(200, 150));
        path.getElements().add(new LineTo(250, 150));
        path.getElements().add(new LineTo(250, 50));
        path.getElements().add(new MoveTo(300, 200));
        path.getElements().add(new LineTo(300, 50));
        path.getElements().add(new MoveTo(350, 0));
        path.getElements().add(new LineTo(350, 150));
        path.getElements().add(new LineTo(450, 150));
        path.getElements().add(new MoveTo(500, 0));
        path.getElements().add(new LineTo(500, 500));
        path.getElements().add(new MoveTo(500, 100));
        path.getElements().add(new LineTo(400, 100));
        path.getElements().add(new LineTo(400, 50));
        path.getElements().add(new LineTo(450, 50));
        path.getElements().add(new MoveTo(500, 300));
        path.getElements().add(new LineTo(450, 300));
        path.getElements().add(new LineTo(450, 250));

        // enemies
        enemies.clear();
        Group e1 = GenerateEnemy();
        e1.setLayoutX(15);
        e1.setLayoutY(165);
        enemies.add(e1);
        Group e2 = GenerateEnemy();
        e2.setLayoutX(265);
        e2.setLayoutY(465);
        enemies.add(e2);
        final int[] interval =
        {
            1
        };

        // https://stackoverflow.com/a/9966213
        timer = new Timeline(new KeyFrame(enemySpeed, e ->
        {

            switch (interval[0])
            {
                case 1:
                    e1.setLayoutX(e1.getLayoutX() + enemyMove); // move right
                    e2.setLayoutX(e2.getLayoutX() + enemyMove); // move right
                    interval[0]++;
                    break;

                case 2:
                    e1.setLayoutY(e1.getLayoutY() + enemyMove); // move down
                    e2.setLayoutX(e2.getLayoutX() + enemyMove); // move right
                    interval[0]++;
                    break;

                case 3:
                    e1.setLayoutX(e1.getLayoutX() + enemyMove); // move right
                    e2.setLayoutY(e2.getLayoutY() - enemyMove); // move up
                    interval[0]++;
                    break;

                case 4:
                    e1.setLayoutX(e1.getLayoutX() + enemyMove); // move right
                    e2.setLayoutX(e2.getLayoutX() + enemyMove); // move right
                    interval[0]++;
                    break;

                case 5:
                    e1.setLayoutX(e1.getLayoutX() + enemyMove); // move right
                    e2.setLayoutY(e2.getLayoutY() + enemyMove); // move down
                    interval[0]++;
                    break;

                case 6:
                    e1.setLayoutX(e1.getLayoutX() - enemyMove); // move left
                    e2.setLayoutY(e2.getLayoutY() - enemyMove); // move up
                    interval[0]++;
                    break;

                case 7:
                    e1.setLayoutX(e1.getLayoutX() - enemyMove); // move left
                    e2.setLayoutX(e2.getLayoutX() - enemyMove); // move left
                    interval[0]++;
                    break;

                case 8:
                    e1.setLayoutX(e1.getLayoutX() - enemyMove); // move left
                    e2.setLayoutY(e2.getLayoutY() + enemyMove); // move down
                    interval[0]++;
                    break;

                case 9:
                    e1.setLayoutY(e1.getLayoutY() - enemyMove); // move up
                    e2.setLayoutX(e2.getLayoutX() - enemyMove); // move left
                    interval[0]++;
                    break;

                case 10:
                    e1.setLayoutX(e1.getLayoutX() - enemyMove); // move left
                    e2.setLayoutX(e2.getLayoutX() - enemyMove); // move left
                    interval[0] = 1;
                    break;
            }

            CheckIfPlayerIsTouchingEnemy(pane);
        }));

        // collectibles
        collectibles.clear();
        Group c1 = GenerateCollectible();
        c1.setLayoutX(425);
        c1.setLayoutY(65);
        collectibles.add(c1);
        Group c2 = GenerateCollectible();
        c2.setLayoutX(225);
        c2.setLayoutY(315);
        collectibles.add(c2);
        Group c3 = GenerateCollectible();
        c3.setLayoutX(275);
        c3.setLayoutY(215);
        collectibles.add(c3);
        Group c4 = GenerateCollectible();
        c4.setLayoutX(375);
        c4.setLayoutY(215);
        collectibles.add(c4);
        Group c5 = GenerateCollectible();
        c5.setLayoutX(125);
        c5.setLayoutY(415);
        collectibles.add(c5);

        maze = path;
        pane.getChildren().clear();
        AddSchoolBackground(pane);
        pane.getChildren().addAll(path, c1, c2, c3, c4, c5, e1, e2);
        // add enemies to array list
        AddPlayer(pane);

        timer.setCycleCount(Timeline.INDEFINITE);//allows an indefinite loop of animation
        
        timer.play();
        failPlayer.stop();
        levelPlayer.play();
    }

    //Method creates maze and player icon for level 2 using Path class.
    public static void Level2(Pane pane)
    {
        level = 2;

        Path path = new Path(new MoveTo());
        path.setStrokeWidth(10);
        path.setStroke(Color.web("rgb(97, 49, 15)"));

        path.getElements().add(new LineTo(0, 100));
        path.getElements().add(new LineTo(50, 100));
        path.getElements().add(new LineTo(50, 50));
        path.getElements().add(new MoveTo(50, 100));
        path.getElements().add(new LineTo(100, 100));
        path.getElements().add(new LineTo(100, 150));
        path.getElements().add(new LineTo(150, 150));
        path.getElements().add(new MoveTo(0, 100));
        path.getElements().add(new LineTo(0, 250));
        path.getElements().add(new LineTo(50, 250));
        path.getElements().add(new LineTo(50, 300));
        path.getElements().add(new LineTo(100, 300));
        path.getElements().add(new MoveTo(0, 250));
        path.getElements().add(new LineTo(0, 450));
        path.getElements().add(new LineTo(50, 450));
        path.getElements().add(new MoveTo(0, 450));
        path.getElements().add(new LineTo(0, 500));
        path.getElements().add(new LineTo(350, 500));
        path.getElements().add(new LineTo(350, 350));
        path.getElements().add(new LineTo(400, 350));
        path.getElements().add(new LineTo(400, 450));
        path.getElements().add(new LineTo(450, 450));
        path.getElements().add(new MoveTo(350, 450));
        path.getElements().add(new LineTo(100, 450));
        path.getElements().add(new LineTo(100, 350));
        path.getElements().add(new LineTo(50, 350));
        path.getElements().add(new LineTo(50, 400));
        path.getElements().add(new MoveTo(100, 350));
        path.getElements().add(new LineTo(150, 350));
        path.getElements().add(new LineTo(150, 250));
        path.getElements().add(new LineTo(100, 250));
        path.getElements().add(new LineTo(100, 200));
        path.getElements().add(new LineTo(50, 200));
        path.getElements().add(new LineTo(50, 150));
        path.getElements().add(new MoveTo(350, 500));
        path.getElements().add(new LineTo(450, 500));
        path.getElements().add(new MoveTo(500, 500));
        path.getElements().add(new LineTo(500, 400));
        path.getElements().add(new LineTo(450, 400));
        path.getElements().add(new MoveTo(500, 400));
        path.getElements().add(new LineTo(500, 350));
        path.getElements().add(new LineTo(450, 350));
        path.getElements().add(new MoveTo(500, 350));
        path.getElements().add(new LineTo(500, 100));
        path.getElements().add(new LineTo(450, 100));
        path.getElements().add(new LineTo(450, 200));
        path.getElements().add(new LineTo(350, 200));
        path.getElements().add(new LineTo(350, 250));
        path.getElements().add(new LineTo(400, 250));
        path.getElements().add(new MoveTo(450, 200));
        path.getElements().add(new LineTo(450, 300));
        path.getElements().add(new LineTo(300, 300));
        path.getElements().add(new LineTo(300, 400));
        path.getElements().add(new LineTo(250, 400));
        path.getElements().add(new MoveTo(500, 100));
        path.getElements().add(new LineTo(500, 0));
        path.getElements().add(new MoveTo(450, 50));
        path.getElements().add(new LineTo(400, 50));
        path.getElements().add(new LineTo(400, 150));
        path.getElements().add(new LineTo(300, 150));
        path.getElements().add(new LineTo(300, 250));
        path.getElements().add(new MoveTo(300, 150));
        path.getElements().add(new LineTo(200, 150));
        path.getElements().add(new MoveTo(250, 150));
        path.getElements().add(new LineTo(250, 350));
        path.getElements().add(new LineTo(200, 350));
        path.getElements().add(new LineTo(200, 400));
        path.getElements().add(new LineTo(150, 400));
        path.getElements().add(new MoveTo(150, 300));
        path.getElements().add(new LineTo(200, 300));
        path.getElements().add(new LineTo(200, 200));
        path.getElements().add(new LineTo(150, 200));
        path.getElements().add(new MoveTo(300, 150));
        path.getElements().add(new LineTo(300, 50));
        path.getElements().add(new MoveTo(500, 100));
        path.getElements().add(new LineTo(500, 0));
        path.getElements().add(new LineTo(350, 0));
        path.getElements().add(new LineTo(350, 100));
        path.getElements().add(new MoveTo(350, 0));
        path.getElements().add(new LineTo(200, 0));
        path.getElements().add(new LineTo(200, 100));
        path.getElements().add(new LineTo(250, 100));
        path.getElements().add(new LineTo(250, 50));
        path.getElements().add(new MoveTo(200, 100));
        path.getElements().add(new LineTo(150, 100));
        path.getElements().add(new LineTo(150, 50));
        path.getElements().add(new LineTo(100, 50));
        path.getElements().add(new MoveTo(200, 0));
        path.getElements().add(new LineTo(50, 0));

        // enemies
        enemies.clear();
        Group e1 = GenerateEnemy();
        e1.setLayoutX(115);
        e1.setLayoutY(65);
        enemies.add(e1);
        Group e2 = GenerateEnemy();
        e2.setLayoutX(365);
        e2.setLayoutY(365);
        enemies.add(e2);
        Group e3 = GenerateEnemy();
        e3.setLayoutX(265);
        e3.setLayoutY(165);
        enemies.add(e3);
        final int[] interval =
        {
            1
        };

        // https://stackoverflow.com/a/9966213
        timer = new Timeline(new KeyFrame(enemySpeed, e ->
        {

            switch (interval[0])
            {
                case 1:
                    e1.setLayoutY(e1.getLayoutY() + enemyMove); // move down
                    e2.setLayoutY(e2.getLayoutY() + enemyMove); // move down
                    e3.setLayoutY(e3.getLayoutY() + enemyMove); // move down
                    interval[0]++;
                    break;

                case 2:
                    e1.setLayoutX(e1.getLayoutX() + enemyMove); // move right
                    e2.setLayoutY(e2.getLayoutY() + enemyMove); // move down
                    e3.setLayoutY(e3.getLayoutY() + enemyMove); // move down
                    interval[0]++;
                    break;

                case 3:
                    e1.setLayoutX(e1.getLayoutX() + enemyMove); // move right
                    e2.setLayoutX(e2.getLayoutX() + enemyMove); // move right
                    e3.setLayoutY(e3.getLayoutY() + enemyMove); // move down
                    interval[0]++;
                    break;

                case 4:
                    e1.setLayoutX(e1.getLayoutX() + enemyMove); // move right
                    e2.setLayoutX(e2.getLayoutX() + enemyMove); // move right
                    e3.setLayoutY(e3.getLayoutY() + enemyMove); // move down
                    interval[0]++;
                    break;

                case 5:
                    e1.setLayoutY(e1.getLayoutY() - enemyMove); // move up
                    e2.setLayoutY(e2.getLayoutY() - enemyMove); // move up
                    e3.setLayoutX(e3.getLayoutX() - enemyMove); // move left
                    interval[0]++;
                    break;

                case 6:
                    e1.setLayoutY(e1.getLayoutY() + enemyMove); // move down
                    e2.setLayoutY(e2.getLayoutY() + enemyMove); // move down
                    e3.setLayoutX(e3.getLayoutX() + enemyMove); // move right
                    interval[0]++;
                    break;

                case 7:
                    e1.setLayoutX(e1.getLayoutX() - enemyMove); // move left
                    e2.setLayoutX(e2.getLayoutX() - enemyMove); // move left
                    e3.setLayoutY(e3.getLayoutY() - enemyMove); // move up
                    interval[0]++;
                    break;

                case 8:
                    e1.setLayoutX(e1.getLayoutX() - enemyMove); // move left
                    e2.setLayoutX(e2.getLayoutX() - enemyMove); // move left
                    e3.setLayoutY(e3.getLayoutY() - enemyMove); // move up
                    interval[0]++;
                    break;

                case 9:
                    e1.setLayoutX(e1.getLayoutX() - enemyMove); // move left
                    e2.setLayoutY(e2.getLayoutY() - enemyMove); // move up
                    e3.setLayoutY(e3.getLayoutY() - enemyMove); // move up
                    interval[0]++;
                    break;

                case 10:
                    e1.setLayoutY(e1.getLayoutY() - enemyMove); // move up
                    e2.setLayoutY(e2.getLayoutY() - enemyMove); // move up
                    e3.setLayoutY(e3.getLayoutY() - enemyMove); // move up
                    interval[0] = 1;
                    break;
            }

            CheckIfPlayerIsTouchingEnemy(pane);
        }));

        // collectibles
        collectibles.clear();
        Group c1 = GenerateCollectible();
        c1.setLayoutX(225);
        c1.setLayoutY(65);
        collectibles.add(c1);
        Group c2 = GenerateCollectible();
        c2.setLayoutX(425);
        c2.setLayoutY(65);
        collectibles.add(c2);
        Group c3 = GenerateCollectible();
        c3.setLayoutX(175);
        c3.setLayoutY(265);
        collectibles.add(c3);
        Group c4 = GenerateCollectible();
        c4.setLayoutX(375);
        c4.setLayoutY(215);
        collectibles.add(c4);
        Group c5 = GenerateCollectible();
        c5.setLayoutX(475);
        c5.setLayoutY(365);
        collectibles.add(c5);

        maze = path;
        pane.getChildren().clear();
        AddSchoolBackground(pane);
        pane.getChildren().addAll(path, c1, c2, c3, c4, c5, e1, e2, e3);
        AddPlayer(pane);
        
        failPlayer.stop();
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    public static void StartMenu(Pane pane)
    {
        level = 0;
        GridPane gridPane = new GridPane();
        Button start = new Button();
        Button quit = new Button();
        Button howTo = new Button();
        Label title = new Label();
        Label description = new Label();

        Image calvin = new Image("calvin.jpg");
        ImageView calvinView = new ImageView(calvin);

        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setMinSize(500, 500);
        gridPane.setStyle("-fx-background-color: #2e8b57;");

        GridPane.setHalignment(start, HPos.CENTER);
        GridPane.setHalignment(quit, HPos.CENTER);
        GridPane.setHalignment(howTo, HPos.CENTER);
        GridPane.setHalignment(title, HPos.CENTER);
        GridPane.setHalignment(calvinView, HPos.CENTER);
        GridPane.setHalignment(description, HPos.CENTER);
        description.setTextAlignment(TextAlignment.CENTER);

        start.setOnMousePressed(e -> Level1(pane));
        quit.setOnMousePressed(e -> Close());
        howTo.setOnMousePressed(e -> HowTo(pane));

        title.setText("Oh Boy !");
        title.setFont(Font.font("Verdana", 30));

        description.setText("Get Hobbes out of your school! Don't get caught and grab as much home work as you can.");
        description.setFont(Font.font("Verdana", 14));
        start.setText("START");
        quit.setText("QUIT");
        howTo.setText("HOW TO");

        gridPane.add(title, 0, 1);
        gridPane.add(calvinView, 0, 2);
        gridPane.add(description, 0, 3);
        gridPane.add(howTo, 0, 4);
        gridPane.add(quit, 0, 5);
        gridPane.add(start, 0, 6);
        gridPane.getRowConstraints().setAll(new RowConstraints(25));//sets gap between rows 
       
        failPlayer.stop();
        winPlayer.stop();
        maze = null;
        pane.getChildren().clear();
        pane.getChildren().add(gridPane);
    }

    public static void HowTo(Pane pane)
    {
        level = 0;
        GridPane gridPane = new GridPane();
        Button mainMenu = new Button();
        Label howTo = new Label();

        Image cAndH = new Image("Calvin and Hobbes.jpg");
        ImageView cAndHView = new ImageView(cAndH);

        // https://docs.oracle.com/javase/7/docs/api/java/lang/System.html#lineSeparator%28%29
        howTo.setText("Use W, A, S, D or the arrow keys to move Calvin through school and get Hobbes out safely.  "
                + System.lineSeparator()//allows proper spacing on different systems 
                + "Don't let your teachers see you or you will have to start again."
                + System.lineSeparator()
                + "Try to grab as much of your home work as you can"
                + System.lineSeparator()
                + "Get points by finishing a level and gathering home work.");
        howTo.setTextAlignment(TextAlignment.CENTER);

        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setMinSize(500, 500);
        gridPane.setStyle("-fx-background-color: #2e8b57;");

        GridPane.setHalignment(mainMenu, HPos.CENTER);
        mainMenu.setText("MAIN MENU");
        mainMenu.setOnMousePressed(e -> StartMenu(pane));

        GridPane.setHalignment(cAndHView, HPos.CENTER);

        gridPane.add(cAndHView, 0, 0);
        gridPane.add(howTo, 0, 2, 1, 2);
        gridPane.add(mainMenu, 0, 4);
       

        maze = null;
        pane.getChildren().clear();
        pane.getChildren().addAll(gridPane);
    }

    public static void FailScreen(Pane pane)
    {
        GridPane gridPane = new GridPane();
        Button tryAgain = new Button();
        Button quit = new Button();
        Button mainMenu = new Button();
        Label OhNo = new Label();

        Image rain = new Image("rain.png");
        ImageView rainView = new ImageView(rain);

        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setMinSize(500, 500);
        gridPane.setStyle("-fx-background-color: #2e8b57;");

        GridPane.setHalignment(tryAgain, HPos.CENTER);
        GridPane.setHalignment(quit, HPos.CENTER);
        GridPane.setHalignment(mainMenu, HPos.CENTER);
        GridPane.setHalignment(rainView, HPos.CENTER);

        OhNo.setText("Oh No. You were caught!");
        OhNo.setFont(Font.font("Veranda", (FontWeight.BOLD), 40));
        tryAgain.setText("TRY AGAIN");
        quit.setText("QUIT");
        mainMenu.setText("MAIN MENU");

        mainMenu.setOnMousePressed(e -> StartMenu(pane));
        quit.setOnMousePressed(e -> Close());
        //check the level failed to retry only the failed level and not entire game
        tryAgain.setOnMousePressed(e ->
        {
            if (level == 1)
            {
                Level1(pane);
            } else if (level == 2)
            {
                Level2(pane);
            }
        });

        gridPane.add(OhNo, 0, 1);
        gridPane.add(rainView, 0, 2);
        gridPane.add(tryAgain, 0, 3);
        gridPane.add(quit, 0, 4);
        gridPane.add(mainMenu, 0, 5);
        gridPane.getRowConstraints().setAll(new RowConstraints(25));
        
        levelPlayer.stop();
        failPlayer.play();

        maze = null;
        pane.getChildren().clear();
        pane.getChildren().add(gridPane);
    }

    public static void WinScreen(Pane pane)
    {
        level = 0;
        WriteScoreToFile();

        // grid pane
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(30);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setMinSize(500, 500);
        gridPane.setStyle("-fx-background-color: #2e8b57;");

        // label
        Label youWon = new Label("YOU WON!");
        youWon.setFont(Font.font("Veranda", (FontWeight.BOLD), 40));
        GridPane.setHalignment(youWon, HPos.CENTER);
        GridPane.setValignment(youWon, VPos.TOP);
        gridPane.add(youWon, 0, 0);

        // image
        Image dance = new Image("dance.png");
        ImageView danceView = new ImageView(dance);
        GridPane.setHalignment(danceView, HPos.CENTER);
        GridPane.setValignment(danceView, VPos.CENTER);
        gridPane.add(danceView, 0, 1);

        // player score
        Label playerScore = new Label("Your Score: " + score + "!");
        playerScore.setFont(Font.font("Veranda", 16));
        GridPane.setHalignment(playerScore, HPos.CENTER);
        GridPane.setValignment(playerScore, VPos.CENTER);
        gridPane.add(playerScore, 0, 2);

        // replay button
        Button replay = new Button();
        replay.setText("REPLAY");
        replay.setOnMousePressed(e -> Level1(pane));
        GridPane.setHalignment(replay, HPos.CENTER);
        GridPane.setValignment(replay, VPos.CENTER);
        gridPane.add(replay, 0, 3);

        // main menu button
        Button mainMenu = new Button();
        mainMenu.setText("MAIN MENU");
        mainMenu.setOnMousePressed(e -> StartMenu(pane));
        GridPane.setHalignment(mainMenu, HPos.CENTER);
        GridPane.setValignment(mainMenu, VPos.CENTER);
        gridPane.add(mainMenu, 0, 4);

        // quit button
        Button quit = new Button();
        quit.setText("QUIT");
        quit.setOnMousePressed(e -> Close());
        GridPane.setHalignment(quit, HPos.CENTER);
        GridPane.setValignment(quit, VPos.CENTER);
        gridPane.add(quit, 0, 5);

        // top score label
        Label topScores = new Label("TOP SCORES");
        topScores.setFont(Font.font("Veranda", 16));
        GridPane.setHalignment(topScores, HPos.CENTER);
        GridPane.setValignment(topScores, VPos.BOTTOM);
        gridPane.add(topScores, 2, 0);

        // scores
        ArrayList<Integer> scores = ReadScoresFromFile();
        Label scoresLabel = new Label();
        String scoresText = "";

        //adds each score to string line line seperate
        for (int i = 0; i < scores.size(); i++)
        {
            scoresText += (i + 1) + ". " + scores.get(i) + System.lineSeparator();
        }

        scoresLabel.setText(scoresText);
        scoresLabel.setFont(Font.font("Veranda", 14));
        GridPane.setHalignment(scoresLabel, HPos.CENTER);
        GridPane.setValignment(scoresLabel, VPos.TOP);
        gridPane.add(scoresLabel, 1, 1, 1, 5);
        
        levelPlayer.stop();
        winPlayer.play();

        maze = null;
        pane.getChildren().clear();
        pane.getChildren().add(gridPane);
    }

    public static void Close()
    {
        stage.close();
    }

    public static void AddPlayer(Pane pane)
    {
        pane.getChildren().add(playerGroup);
        playerGroup.setLayoutX(15);
        playerGroup.setLayoutY(15);
    }

    private static Group GenerateEnemy()
    {
        Group group = new Group();
        Rectangle rectangle = new Rectangle(21, 25, Color.TRANSPARENT);
        
        Image icon = new Image("teacher.png");
        ImageView player = new ImageView(icon);
        
        group.getChildren().addAll(rectangle, player);
        return group;
    }

    private static Group GenerateCollectible()
    {
        Group group = new Group();
        Rectangle rectangle = new Rectangle(14, 25, Color.TRANSPARENT);
        
        Image icon = new Image("notebook.png");
        ImageView collectible = new ImageView(icon);
        
        group.getChildren().addAll(rectangle,collectible);
        return group;
    }

    private static void WriteScoreToFile()
    {
        BufferedWriter bw = null;

        try
        {
            // https://alvinalexander.com/java/edu/qanda/pjqa00009.shtml
            bw = new BufferedWriter(new FileWriter("highscores.txt", true));
            bw.write(score + "");
            bw.newLine();
            bw.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (bw != null)
            {
                try
                {
                    bw.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private static ArrayList<Integer> ReadScoresFromFile()
    {
        Scanner reader = null;
        ArrayList<Integer> scoreList = new ArrayList<>();

        try
        {
            File scores = new File("highscores.txt");
            reader = new Scanner(scores);

            while (reader.hasNextInt())
            {
                try
                {
                    scoreList.add(reader.nextInt());
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } finally
        {
            if (reader != null)
            {
                reader.close();
            }
        }

        Collections.sort(scoreList, Collections.reverseOrder());
        ArrayList<Integer> returnList = new ArrayList<>();
        returnList.addAll(scoreList.subList(0, 10));
        return returnList;
    }

    private static void AddSchoolBackground(Pane pane)
    {
        Image Cow = new Image("school.jpg");
        ImageView background = new ImageView(Cow);
        background.setOpacity(0.4);
        background.setFitHeight(500);
        background.setFitWidth(500);
        pane.getChildren().add(background);
    }

    private static boolean CheckIfPlayerIsTouchingEnemy(Pane pane)
    {
        for (Group enemy : enemies)
        {
            // iterate over all of the enemies in the maze
            for (Node child : enemy.getChildren())
            {
                if (child instanceof Shape && !Shape.intersect(playerModel, (Shape) child).getBoundsInLocal().isEmpty())
                {
                    // if the player is tocuhing an enemy
                    FailScreen(pane);

                    if (timer != null)
                    {
                        timer.stop(); // stop enemy movements
                    }

                    return true; // quit method since player failed
                }
            }
        }

        return false; // false if this point reached
    }

    public static MediaPlayer GenerateMedia(String filename, boolean loop)
    {
        Media media = new Media(new File("src\\" + filename).toURI().toString());
        MediaPlayer player = new MediaPlayer(media);

        if (loop)
        {
            player.setOnEndOfMedia(() -> player.seek(Duration.ZERO));
        }

        return player;
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
