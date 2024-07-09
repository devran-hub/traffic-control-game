//150122051 Devrim Polat
//150122001 Erhan Özer
//150122030 Burak Demirer
import javafx.geometry.Insets;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

// we have set other helper classes except MetaData as inner classes because we need to access the sizes of meta pane to create a map related to sizes of meta. For example, when you set sizes of MetaData instance "1000x1000" it will create you a responsive map
public class main extends Application {
	Metadata meta;// Since we have inner classes and some methods which use meta, they must use
					// sizes of same meta so we set meta as global variable.
	Pane pane; // since we need to access pane and paths[] in other methods
	Path paths[];

	// Since TLPane and CarPane are used in the inner classes and some other
	// methods. We should define them global.

	Pane TLPane;
	Pane CarPane;
	int mode; // if we start game from level 1 to level 5 or we select level to complete
				// single level
	static Boolean spawns[];

	static Circle circles[]; // circles which will be added to pane to prevent spawning cars when other cars
								// intersect with those circles
	static Circle endCircles[];// cars will be disappeared when they intersect one of those endCircles element
	private Stage primaryStage; // We define it as a datafield to use it in other methods to setScene of the
								// primaryStage
	int level = 1;// Initial level

	@Override
	public void start(Stage primaryStage) throws FileNotFoundException {
		this.primaryStage = primaryStage;

		primaryStage.setScene(menuScreen()); // Game starts with menu screen
		primaryStage.show();

	}

	public Scene gameScene(String str) throws FileNotFoundException {
		trafficLight.lights.clear(); // we make sure that trafficLight.lights is empty at the beginning of the level
		Label Score = new Label("Score:");
		Label Crashes = new Label("Crashes:");

		// in every 0.1 seconds it checks if the level is failed or completed
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
			Score.setText("Score: " + meta.getCompletedCarCounter() + "/" + meta.getWinCond()); // record score and
																								// crashes on the screen
			Crashes.setText("Crashes: " + meta.getCrashCounter() + "/" + meta.getLossCond());
			if (meta.getCrashCounter() >= meta.getLossCond()) {// The reason why we set >= operator instead of ==
																// operator is avoiding the error of showing failed and
																// winning screen when two cars crashed or ended the
																// path
				for (int i = 0; i < Car.cars.size(); i++) {
				}

				// remove each car from the scene and pane to clear the environment
				for (Car car : Car.cars) {
					car.pathTransition.pause();
					CarPane.getChildren().remove(car);
				}
				trafficLight.lights.clear();// clear the trafficLight.lights to
				Car.cars.clear();// clears car array to start a new level
				StackPane failed = failedPane();
				pane.getChildren().add(failed);// show failed pane on the screen
				failed.setPadding(new Insets(150, 150, 50, 50));

				// utilize counters for new level
				meta.setCrashCounter(0);
				meta.setCompletedCarCounter(0);
			} else if (meta.getCompletedCarCounter() >= meta.getWinCond()) {
				for (Car car : Car.cars) {
					car.pathTransition.pause();
					CarPane.getChildren().remove(car);
				}
				level++;// increase level
				Car.cars.clear();
				trafficLight.lights.clear();

				if (mode == 1) {// it means if we select gaming mode as "Select Level"

					StackPane completeLevel = completeLevelPane();
					pane.getChildren().add(completeLevel);
					completeLevel.setPadding(new Insets(150, 150, 50, 50));

				} else if (level < 6) {// if mode = 0 and level variable is less than 6
					try {
						primaryStage.setScene(gameScene("level" + level + ".txt"));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} else {// at the end of the level 5 it makes level =6 if you pass level 5 then the code
						// goes to this clause because of that.

					StackPane winning = winningPane();
					pane.getChildren().add(winning);
					winning.setPadding(new Insets(150, 150, 50, 50));
					meta.setCrashCounter(0);
					meta.setCompletedCarCounter(0);
				}
				meta.setCompletedCarCounter(0);
				meta.setCrashCounter(0);

			}

		}));

		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();

		// Reading files and creating objects
		File file = new File("levels/"+str);
		Scanner input = new Scanner(file);
		String[] lines = input.nextLine().split(" ");

		meta = new Metadata(Double.parseDouble(lines[1]), Double.parseDouble(lines[2]), Integer.parseInt(lines[3]),
				Integer.parseInt(lines[4]), Integer.parseInt(lines[5]), Integer.parseInt(lines[6]),
				Integer.parseInt(lines[7]));
		meta.setCompletedCarCounter(0);
		pane = new Pane();

		// TLPane and CarPane help to spawn car objects below of trafficLights
		TLPane = new Pane();
		CarPane = new Pane();
		pane.getChildren().addAll(meta, CarPane, TLPane, Score, Crashes);// we add TLPane later than CarPane because we
																			// want Trafic Lights to lie on the top

		Crashes.setPadding(new Insets(20, 20, 0, 0));

		paths = new Path[meta.getNumOfPaths()];
		for (int i = 0; i < paths.length; i++) {
			paths[i] = new Path();

		}
		circles = new Circle[meta.getNumOfPaths()];
		endCircles = new Circle[meta.getNumOfPaths()];
		for (int i = 0; i < paths.length; i++) {
			endCircles[i] = new Circle(0, 0, 10);
		}
		spawns = new Boolean[meta.getNumOfPaths()];
		for (int i = 0; i < paths.length; i++) {
			spawns[i] = true;
		}

		Scene scene = new Scene(pane, Double.parseDouble(lines[1]), Double.parseDouble(lines[2]));
		meta.setCompletedCarCounter(0);
		while (input.hasNext()) {
			lines = input.nextLine().split(" ");
			if (lines[0].equals("TrafficLight")) {
				new trafficLight(Double.parseDouble(lines[1]), Double.parseDouble(lines[2]),
						Double.parseDouble(lines[3]), Double.parseDouble(lines[4]));
			} else if (lines[0].equals("RoadTile")) {
				new RoadTile(Integer.parseInt(lines[1]), Integer.parseInt(lines[2]), Integer.parseInt(lines[3]),
						Integer.parseInt(lines[4]));
			} else if (lines[0].equals("Building")) {
				new Building(Integer.parseInt(lines[1]), Integer.parseInt(lines[2]), Integer.parseInt(lines[3]),
						Integer.parseInt(lines[4]), Integer.parseInt(lines[5]));
			} else if (lines[0].equals("Path")) {
				int i = Integer.parseInt(lines[1]);
				if (lines[2].equals("MoveTo")) {

					// declare elements of circles array to lie at the beginning of the path to
					// control spawning of cars
					circles[i] = new Circle(Double.parseDouble(lines[3]), Double.parseDouble(lines[4]), 16);
					circles[i].opacityProperty().set(0);
					circles[i].setFill(Color.RED);
					pane.getChildren().add(circles[i]);
					paths[i].getElements().add(new MoveTo(Double.parseDouble(lines[3]), Double.parseDouble(lines[4])));
				} else if (lines[2].equals("LineTo")) {
					paths[i].getElements().add(new LineTo(Double.parseDouble(lines[3]), Double.parseDouble(lines[4])));
				}

			}

		}
		for (int i = 0; i < paths.length; i++) { // set position of endcircles elements which makes cars disappear when
													// other cars intersect it
			endCircles[i].setCenterX(((LineTo) paths[i].getElements().get(paths[i].getElements().size() - 1)).getX());
			endCircles[i].setCenterY(((LineTo) paths[i].getElements().get(paths[i].getElements().size() - 1)).getY());
		}

		for (trafficLight tf : trafficLight.lights) {
			for (int i = 0; i < paths.length; i++) {
				if (intersects(paths[i], tf)) {
					tf.pathsTL.add(paths[i]);// add paths that intersects with traffic light to pathsTL of trafficLight
												// pane
				}
			}

		}

		// end of reading files

		createTraffic(); // Implements traffic creation logic
		return scene;
	}

	// Failed game screen
	public StackPane failedPane() {
		StackPane failedRoot = new StackPane();
		HBox mainContent = new HBox(20);
		mainContent.setAlignment(Pos.CENTER);
		Label titleLabel = new Label("You failed :(");
		titleLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 50));
		titleLabel.setPadding(new Insets(40, 40, 40, 40));
		titleLabel.setTextFill(Color.ALICEBLUE);
		Button tryAgainButton = new Button("Try again");
		tryAgainButton.setOnAction(e -> {
			try {
				primaryStage.setScene(gameScene("level" + level + ".txt")); // Starts from the current level again
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		});
		tryAgainButton
				.setStyle("-fx-background-color:rgb(93, 171, 223);-fx-font-size: 14px; -fx-font-family: 'Helvetica';");

		tryAgainButton.setMinSize(120, 40);
		tryAgainButton.setPrefSize(120, 40);
		tryAgainButton.setMaxSize(120, 40);
		Button backToMenu = new Button("Back to menu");
		backToMenu
				.setStyle("-fx-background-color:rgb(93, 171, 223);-fx-font-size: 14px; -fx-font-family: 'Helvetica';");

		backToMenu.setMinSize(120, 40);
		backToMenu.setPrefSize(120, 40);
		backToMenu.setMaxSize(120, 40);
		backToMenu.setOnAction(e -> primaryStage.setScene(menuScreen())); // go back to menu
		mainContent.getChildren().addAll(titleLabel, tryAgainButton, backToMenu);
		Rectangle menuBarBg = new Rectangle(0, 0, 700, 500);
		menuBarBg.setFill(Color.rgb(20, 53, 90));
		menuBarBg.setOpacity(0.9);
		failedRoot.getChildren().addAll(menuBarBg, titleLabel, mainContent);
		titleLabel.setPadding(new Insets(0, 0, 150, 0));
		return failedRoot;

	}

	// opens select level scene which shows you levels to select and play
	public Scene selectLevel() {
		mode = 1; // mode is equal to select level feature which is allows you to play and
					// complete only single level
		StackPane root = new StackPane();
		StackPane sectionBody = new StackPane();
		VBox mainContent = new VBox(20);
		mainContent.setAlignment(Pos.CENTER);
		HBox firstLine = new HBox(20);
		HBox secondLine = new HBox(20);

		Button lvl1 = new Button("Level 1");
		lvl1.setOnAction(e -> {
			try {
				primaryStage.setScene(gameScene("level1.txt"));
				level = 1;
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		});
		lvl1.setStyle("-fx-background-color:rgb(93, 171, 223);-fx-font-size: 14px; -fx-font-family: 'Helvetica';");

		lvl1.setMinSize(120, 40);
		lvl1.setPrefSize(120, 40);
		lvl1.setMaxSize(120, 40);

		Button lvl2 = new Button("Level 2");
		lvl2.setOnAction(e -> {
			try {
				primaryStage.setScene(gameScene("level2.txt"));
				level = 2;
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		});
		lvl2.setStyle("-fx-background-color:rgb(93, 171, 223);-fx-font-size: 14px; -fx-font-family: 'Helvetica';");

		lvl2.setMinSize(120, 40);
		lvl2.setPrefSize(120, 40);
		lvl2.setMaxSize(120, 40);

		Button lvl3 = new Button("Level 3");
		lvl3.setOnAction(e -> {
			try {
				primaryStage.setScene(gameScene("level3.txt"));
				level = 3;
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		});
		lvl3.setStyle("-fx-background-color:rgb(93, 171, 223);-fx-font-size: 14px; -fx-font-family: 'Helvetica';");

		lvl3.setMinSize(120, 40);
		lvl3.setPrefSize(120, 40);
		lvl3.setMaxSize(120, 40);

		Button lvl4 = new Button("Level 4");
		lvl4.setOnAction(e -> {
			try {
				primaryStage.setScene(gameScene("level4.txt"));
				level = 4;
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		});
		lvl4.setStyle("-fx-background-color:rgb(93, 171, 223);-fx-font-size: 14px; -fx-font-family: 'Helvetica';");

		lvl4.setMinSize(120, 40);
		lvl4.setPrefSize(120, 40);
		lvl4.setMaxSize(120, 40);
		Button lvl5 = new Button("Level 5");
		lvl5.setOnAction(e -> {
			try {
				primaryStage.setScene(gameScene("level5.txt"));
				level = 5;
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		});
		lvl5.setStyle("-fx-background-color:rgb(93, 171, 223);-fx-font-size: 14px; -fx-font-family: 'Helvetica';");

		lvl5.setMinSize(120, 40);
		lvl5.setPrefSize(120, 40);
		lvl5.setMaxSize(120, 40);
		Rectangle menuBarBg = new Rectangle(0, 0, 700, 500);
		menuBarBg.setFill(Color.rgb(20, 53, 90));
		menuBarBg.setOpacity(0.9);

		ImageView image = new ImageView(new Image("menuBackGround.jpg"));
		image.setFitHeight(800);
		image.setFitWidth(800);

		firstLine.getChildren().addAll(lvl1, lvl2, lvl3);
		firstLine.setAlignment(Pos.CENTER);
		secondLine.setAlignment(Pos.CENTER);
		secondLine.getChildren().addAll(lvl4, lvl5);
		mainContent.getChildren().addAll(firstLine, secondLine);
		sectionBody.getChildren().addAll(menuBarBg, mainContent);
		root.getChildren().addAll(image, sectionBody);
		return new Scene(root, 800, 800);
	}

	// shows you a winning screen on the gameScene if you have passed all the 5
	// levels
	public StackPane winningPane() {

		StackPane winningRoot = new StackPane();
		HBox mainContent = new HBox(20);
		mainContent.setAlignment(Pos.CENTER);
		Label titleLabel = new Label("Congratulations! You won");
		titleLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 50));
		titleLabel.setPadding(new Insets(40, 40, 40, 40));
		titleLabel.setTextFill(Color.ALICEBLUE);
		Button backToMenu = new Button("Back to menu");
		backToMenu
				.setStyle("-fx-background-color:rgb(93, 171, 223);-fx-font-size: 14px; -fx-font-family: 'Helvetica';");

		backToMenu.setMinSize(120, 40);
		backToMenu.setPrefSize(120, 40);
		backToMenu.setMaxSize(120, 40);
		backToMenu.setOnAction(e -> primaryStage.setScene(menuScreen()));
		mainContent.getChildren().addAll(titleLabel, backToMenu);
		Rectangle menuBarBg = new Rectangle(0, 0, 700, 500);
		menuBarBg.setFill(Color.rgb(20, 53, 90));
		menuBarBg.setOpacity(0.9);
		winningRoot.getChildren().addAll(menuBarBg, titleLabel, mainContent);
		titleLabel.setPadding(new Insets(0, 0, 150, 0));
		return winningRoot;
	}

	// if you select "Select level" feature and complete that level you will see
	// that pane on the scene. It also adds "Back to menu" button which shows you
	// menu scene
	public StackPane completeLevelPane() {

		StackPane completeLevelRoot = new StackPane();
		HBox mainContent = new HBox(20);
		mainContent.setAlignment(Pos.CENTER);
		Label titleLabel = new Label("Bravo! you have successfully completed the level");
		titleLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 25));
		titleLabel.setPadding(new Insets(40, 40, 40, 40));
		titleLabel.setTextFill(Color.ALICEBLUE);
		Button backToMenu = new Button("Back to menu");
		backToMenu
				.setStyle("-fx-background-color:rgb(93, 171, 223);-fx-font-size: 14px; -fx-font-family: 'Helvetica';");

		backToMenu.setMinSize(120, 40);
		backToMenu.setPrefSize(120, 40);
		backToMenu.setMaxSize(120, 40);
		backToMenu.setOnAction(e -> primaryStage.setScene(menuScreen()));
		mainContent.getChildren().addAll(titleLabel, backToMenu);
		Rectangle menuBarBg = new Rectangle(0, 0, 700, 500);
		menuBarBg.setFill(Color.rgb(20, 53, 90));
		menuBarBg.setOpacity(0.9);
		completeLevelRoot.getChildren().addAll(menuBarBg, titleLabel, mainContent);
		titleLabel.setPadding(new Insets(0, 0, 150, 0));
		return completeLevelRoot;
	}

	public Scene menuScreen() {
		level = 1;// when you get to menu screen it initially sets all specification to click
					// "Start Game" and play that's why we set level to one in there because it
					// should start from level 1 when you click that button
		mode = 0;// inital mode is equal to zero which applies the logic of starting game and
					// completing levels through level 5
		StackPane root = new StackPane();
		Scene scene = new Scene(root, 800, 800);

		VBox mainContent = new VBox(20);
		mainContent.setAlignment(Pos.CENTER);
		Label titleLabel = new Label("Traffic Game");
		titleLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 50));
		titleLabel.setPadding(new Insets(40, 40, 40, 40));
		titleLabel.setTextFill(Color.ALICEBLUE);
		Button startButton = new Button("Start Game");

		startButton
				.setStyle("-fx-background-color:rgb(93, 171, 223);-fx-font-size: 14px; -fx-font-family: 'Helvetica';");

		startButton.setMinSize(120, 40);
		startButton.setPrefSize(120, 40);
		startButton.setMaxSize(120, 40);
		Button selectLevels = new Button("Select Levels");
		selectLevels
				.setStyle("-fx-background-color:rgb(93, 171, 223);-fx-font-size: 14px; -fx-font-family: 'Helvetica';");

		selectLevels.setMinSize(120, 40);
		selectLevels.setPrefSize(120, 40);
		selectLevels.setMaxSize(120, 40);
		startButton.setOnAction(e -> {
			try {
				primaryStage.setScene(gameScene("level" + level + ".txt")); // "level1.txt" actually
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		});
		selectLevels.setOnAction(e -> primaryStage.setScene(selectLevel())); // opens "select level" scene

		mainContent.getChildren().addAll(titleLabel, startButton, selectLevels);
		ImageView image = new ImageView(new Image("menuBackGround.jpg"));
		image.setFitHeight(800);
		image.setFitWidth(800);

		// Set the top menu bar and main content
		Rectangle menuBarBg = new Rectangle(0, 0, 700, 500);
		menuBarBg.setFill(Color.rgb(20, 53, 90));
		menuBarBg.setOpacity(0.9);
		root.getChildren().addAll(image, menuBarBg, mainContent);
		return scene;
	}

	class Building extends StackPane {
		private int type;
		private int rotation;
		private int color;
		private int gridX;
		private int gridY;

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getRotation() {
			return rotation;
		}

		public void setRotation(int rotation) {
			this.rotation = rotation;
		}

		public int getColor() {
			return color;
		}

		public void setColor(int color) {
			this.color = color;
		}

		public int getGridX() {
			return gridX;
		}

		public void setGridX(int gridX) {
			this.gridX = gridX;
		}

		public int getGridY() {
			return gridY;
		}

		public void setGridY(int gridY) {
			this.gridY = gridY;
		}

		Color[] colors = { Color.rgb(247, 206, 147), Color.rgb(188, 182, 230), Color.rgb(164, 226, 193),
				Color.rgb(125, 205, 250) }; // Şekillerin renkleri
		Color[] coDarker = { Color.rgb(234, 188, 148), Color.rgb(171, 165, 210), Color.rgb(149, 206, 175),
				Color.rgb(93, 171, 223) };// Şekillerin sınırlarının renkleri,

		Building(int t, int r, int col, int x, int y) {
			double size = meta.Height() / meta.getColumnCount(); // size of the edges of sinle grid cell.

			type = t;
			rotation = r;
			color = col;
			gridX = x;
			gridY = y;

			Color co = colors[col]; // color of the shape
			Color colDarker = coDarker[col];// color of the border of the shape

			if (type == 2) {// for the building that fits single grid cell
				Rectangle square1 = new Rectangle(size - 3, size - 3);// also the border will have a size so if we set
																		// width and height of square1 to "size"
																		// variable, it may cause an overflow so we set
																		// the sizes "size - stroke width"
				square1.setFill(co);
				square1.setStroke(colDarker);
				square1.setStrokeWidth(3);
				square1.arcHeightProperty().bind(square1.widthProperty().multiply(.1));
				square1.arcWidthProperty().bind(square1.widthProperty().multiply(.1)); // we smooth the corners

				getChildren().add(square1);// add shape to Building pane
				meta.add(this, gridX, gridY);// add Building pane intp meta Grid Pane

			} else {// this statement is for the shapes that fit more than single grid cell

				StackPane squarePane = new StackPane();// to set cirles or rectangles in the middle of each other
				squarePane.setMaxHeight(size * 2 / 1.4);// squarePane'in boyutu, üzerine eklenecek çemberler veya
														// karelerden daha büyük olmayacak şekilde ayarlandı.
				squarePane.setMaxWidth(size * 2 / 1.4);

				// we set sizes and colors of background of the Building pane.
				setStyle(
						"-fx-border-color: rgb(127,147,161);-fx-background-color: rgb(241,249,254);-fx-border-width: 3px;-fx-border-radius: "
								+ size * .1 + "px;" + "-fx-background-radius: " + size * .2 + "px;");

				if (type == 0) {
					Rectangle innerSquare = new Rectangle();
					Rectangle outherSquare = new Rectangle();
					outherSquare.setFill(co);
					outherSquare.setStroke(colDarker);
					outherSquare.setStrokeWidth(3);

					innerSquare.setFill(null);
					innerSquare.setStroke(colDarker);
					innerSquare.setStrokeWidth(3);

					outherSquare.setHeight(squarePane.getMaxHeight());
					outherSquare.setWidth(squarePane.getMaxWidth());

					outherSquare.arcHeightProperty().bind(squarePane.widthProperty().multiply(.1)); // rounded corners

					outherSquare.arcWidthProperty().bind(squarePane.widthProperty().multiply(.1));

					squarePane.setStyle("-fx-border-radius: 30 30 0 0"); // Make border of squarePane curved

					innerSquare.setHeight(squarePane.getMaxHeight() / 1.4);
					innerSquare.setWidth(squarePane.getMaxHeight() / 1.4);

					squarePane.getChildren().addAll(outherSquare, innerSquare);

				}

				// set circle building if type is 1. Same things we did on type 0
				if (type == 1) {
					Circle outherCircle = new Circle();
					Circle innerCircle = new Circle();
					outherCircle.setRadius(size * 1 / 1.4);
					innerCircle.setRadius(size * 0.8 / 1.4);
					innerCircle.setFill(null);
					innerCircle.setStroke(colDarker);
					outherCircle.setFill(co);
					outherCircle.setStroke(colDarker);
					outherCircle.setStrokeWidth(3);
					innerCircle.setStrokeWidth(3);

					squarePane.getChildren().addAll(outherCircle, innerCircle);

				}
				getChildren().add(squarePane);

				// Rotate building and add it to meta GridPane
				if (r == 90) {
					setAlignment(squarePane, Pos.CENTER_LEFT);
					meta.add(this, gridX, gridY, 3, 2);
				} else if (r == 180) {
					setAlignment(squarePane, Pos.BOTTOM_CENTER);
					meta.add(this, gridX, gridY, 2, 3);
				} else if (r == 270) {
					setAlignment(squarePane, Pos.CENTER_RIGHT);
					meta.add(this, gridX, gridY, 3, 2);
				} else {
					setAlignment(squarePane, Pos.TOP_CENTER);
					meta.add(this, gridX, gridY, 2, 3);
				}

			}

		}

	}

	class RoadTile extends StackPane {
		private int type;
		private int rotate;
		private int gridX;
		private int gridY;

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getR() {
			return rotate;
		}

		public void setR(int rotate) {
			this.rotate = rotate;
		}

		public int getGridX() {
			return gridX;
		}

		public void setGridX(int gridX) {
			this.gridX = gridX;
		}

		public int getGridY() {
			return gridY;
		}

		public void setGridY(int gridY) {
			this.gridY = gridY;
		}

		RoadTile(int t, int r, int x, int y) {
			double size = meta.Height() / meta.getColumnCount();// get size of grid cell
			type = t;
			rotate = r;
			gridX = x;
			gridY = y;
			if (type == 1) {
				Arc arc1 = new Arc(0, 0, size * 0.1, size * 0.1, 0, 90);// When you create the map the white curve that
																		// you see is actually a border of an arc object
																		// we set color of arc is null and stroke is
																		// white

				arc1.setStroke(Color.WHITE);
				arc1.setFill(null);
				setAlignment(arc1, Pos.BOTTOM_LEFT);

				arc1.setStrokeWidth(size * 0.8);

				getChildren().addAll(arc1);
				setRotate(-rotate);// get minus rotate to rotate it as demanded by the project.
			}

			else {
				if (type == 0) {
					Rectangle rect = new Rectangle(size, size * 0.8); // Initially rectangle is aligned in the middle of
																		// the grid cell so the road has distance of
																		// 0.1*size between 2 opposite corners
					rect.setFill(Color.WHITE);
					getChildren().add(rect);

				} else if (type == 2) {// To get this type we aligned to rectangle object. One is vertical and the
										// other is horizantal
					Rectangle rect = new Rectangle(size, size * 0.8);
					Rectangle rect1 = new Rectangle(size * 0.8, size);
					rect.setFill(Color.WHITE);
					rect1.setFill(Color.WHITE);

					getChildren().addAll(rect, rect1);
				} else if (type == 3) {// to make three sided road i initalize two rectangle again: one is similiar to
										// the one in type 0 and the other one is a square. Square one is set bottom
										// center to give the shape of three sides
					Rectangle rect = new Rectangle(size, size * 0.8);
					Rectangle rect1 = new Rectangle(size * 0.8, size * 0.8);
					setAlignment(rect1, Pos.BOTTOM_CENTER);

					rect.setFill(Color.WHITE);
					rect1.setFill(Color.WHITE);
					getChildren().addAll(rect, rect1);

				}
				setRotate(rotate);
			}

			meta.add(this, x, y);

		}
	}

	class Car extends Rectangle {
		public static ArrayList<Car> cars = new ArrayList<Car>();
		public PathTransition pathTransition;
		public int width;
		public int height;
		public int x;
		public int y;
		public int condition = 1;
		public int stoppedLight = -1;
		public ArrayList<trafficLight> CarTL = (ArrayList<trafficLight>) trafficLight.lights.clone();
		public boolean completed;// this states if a car has completed its path or not

		Car(int width, int height, int x, int y) {
			super(x, y, width, height);
			this.width = width;
			this.height = height;
			this.x = x;
			this.y = y;
			cars.add(this);

			// The contructor calls theese three methods to control the situations for each
			// car simultaneously
			carCrash();
			carStop();
			trafficStop();

		}

		public void carCrash() {
			// controls if a car is crashed or not for every 0.1 seconds
			Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> Crash()));
			timeline.setCycleCount(Animation.INDEFINITE);
			timeline.play();
		}

		public void Crash() {
			for (int i = 0; i < cars.size(); i++) {
				Car x = cars.get(i);
				if (cars.get(i) != null && cars.contains(this) && this != cars.get(i)
						&& this.getBoundsInParent().intersects(cars.get(i).getBoundsInParent())) {// if car is not null
																									// and the instance
																									// is included into
																									// cars array(we
																									// check this
																									// because when one
																									// instance gets a
																									// car from the loop
																									// and initialize
																									// the x, the same
																									// car may be
																									// removed from the
																									// array because of
																									// having an
																									// accident with
																									// another car.
																									// Since a car can
																									// make only one
																									// accident we
																									// should check this
																									// condition) and
																									// iterated car is
																									// not equal to the
																									// instance itself
					pathTransition.pause();
					x.pathTransition.pause();
					if (this.condition > 0 && CarPane.getChildren().contains(this)) {//we check CarPane.getChildren().contains(this) because we don't want to count collision of cars of older levels.
						meta.setCrashCounter(meta.getCrashCounter() + 1);
						this.condition = 0;
						x.condition = 0;

					}
					KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), event -> {// after 0.5 seconds these two cars are removed from the CarPane
						cars.remove(x);
						cars.remove(this);
						CarPane.getChildren().removeAll(x, this);
					});
					Timeline timeline = new Timeline(keyFrame);
					timeline.play();
					break;
				}

			}

		}

		// calculate closest distance between cars
		private double calculateClosestDistance(Car car1, Car car2) {
			double minDistance = Double.MAX_VALUE;

			for (int i = 0; i < 4; i++) {
				// get i th corner of rectangle
				double[] point1 = getRotatedCorner(car1, i);

				for (int j = 0; j < 4; j++) {
					// get j th corner of rectangle
					double[] point2 = getRotatedCorner(car2, j);

					// get distance between those 2 corners
					double distance = Math
							.sqrt(Math.pow(point2[0] - point1[0], 2) + Math.pow(point2[1] - point1[1], 2));

					// Update minimum distance
					if (distance < minDistance) {
						minDistance = distance;
					}
				}
			}

			return minDistance;
		}

		// get coordinates of corners
		private double[] getRotatedCorner(Car car, int cornerIndex) {
			double[] point = new double[2];
			double centerX = car.getTranslateX() + car.getWidth() / 2;
			double centerY = car.getTranslateY() + car.getHeight() / 2;

			double angle = Math.toRadians(car.getRotate());
			double cosTheta = Math.cos(angle);
			double sinTheta = Math.sin(angle);

			switch (cornerIndex) {
			case 0: // Left upper corner
				point[0] = centerX + (car.getTranslateX() - centerX) * cosTheta
						- (car.getTranslateY() - centerY) * sinTheta;
				point[1] = centerY + (car.getTranslateX() - centerX) * sinTheta
						+ (car.getTranslateY() - centerY) * cosTheta;
				break;
			case 1: // right upper corner
				point[0] = centerX + (car.getTranslateX() + car.getWidth() - centerX) * cosTheta
						- (car.getTranslateY() - centerY) * sinTheta;
				point[1] = centerY + (car.getTranslateX() + car.getWidth() - centerX) * sinTheta
						+ (car.getTranslateY() - centerY) * cosTheta;
				break;
			case 2: // right lower corner
				point[0] = centerX + (car.getTranslateX() + car.getWidth() - centerX) * cosTheta
						- (car.getTranslateY() + car.getHeight() - centerY) * sinTheta;
				point[1] = centerY + (car.getTranslateX() + car.getWidth() - centerX) * sinTheta
						+ (car.getTranslateY() + car.getHeight() - centerY) * cosTheta;
				break;
			case 3: // left lower corner
				point[0] = centerX + (car.getTranslateX() - centerX) * cosTheta
						- (car.getTranslateY() + car.getHeight() - centerY) * sinTheta;
				point[1] = centerY + (car.getTranslateX() - centerX) * sinTheta
						+ (car.getTranslateY() + car.getHeight() - centerY) * cosTheta;
				break;
			default:
				break;
			}

			return point;
		}

		public void carStop() {
			Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.05), event -> stopPlay()));
			timeline.setCycleCount(Animation.INDEFINITE);
			timeline.play();
		}

		public void stopPlay() {//implements the logic of cars that complete the path and the cars that should be stopped because of the accident of other cars
			for (int j = 0; j < endCircles.length; j++) {
				if (endCircles[j].getBoundsInParent().intersects(this.getBoundsInParent()) && !completed == true
						&& CarPane.getChildren().contains(this)) {
					completed = true;
					meta.setCompletedCarCounter(meta.getCompletedCarCounter() + 1);
					CarPane.getChildren().remove(this);
					cars.remove(this);

				}

			}

			
			boolean causedByCrash = false; //this checks if an accident is happened
			for (int i = 0; i < cars.size(); i++) {

				if (cars.get(i).condition == 0)
					causedByCrash = true;
				
				//implementation of pausing a car which doesn't make any accident but should be pause because there is an accident in front of itself or there is a car which is paused because of an accident
				if (this.condition != 0 && cars.get(i) != this
						&& (cars.get(i).condition == 0 || cars.get(i).condition == 2)
						&& calculateClosestDistance(cars.get(i), this) < 15
						&& cars.get(i).getBoundsInParent().intersects(pathTransition.getPath().getBoundsInParent())) { //an instance is paused if other paused car intersect its path
					condition = 2; //2 means car is paused because of an accident
					pathTransition.pause();
				}
			}
			if (!causedByCrash && condition == 2) {//If there is no accident but car is paused make it play 
				condition = 1;
				pathTransition.play();
			}

		}

		public void trafficStop() {
			//controls for traffic lights and other cars that stopped because of traffic
			Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.01), event -> trafic()));
			timeline.setCycleCount(Animation.INDEFINITE);
			timeline.play();
		}

		public void trafic() {
			for (int i = 0; i < trafficLight.lights.size(); i++) {

				// makes first car that encounter with red traffic light stopped.
				if (trafficLight.lights.get(i).getConditition() == 0
						&& trafficLight.lights.get(i).circle.getBoundsInParent().intersects(this.getBoundsInParent())) {
					CarTL.remove(trafficLight.lights.get(i));
					this.pathTransition.pause();
					this.stoppedLight = i;
					
				}
				//*****************
				if (trafficLight.lights.get(i).getConditition() == 1
						&& trafficLight.lights.get(i).circle.getBoundsInParent().intersects(this.getBoundsInParent())) 	CarTL.remove(trafficLight.lights.get(i));
				//--------------------


			}

			boolean closed = false;// "closed" means it is closed to check it inside the same method call. If
									// statements are valid to make paused car to continue to move then car starts
									// moving in later executions of the method. The reason why we are doing like
									// this is resolving synchronized play animation issue right after the execution
									// of pause animation which is executed when a level is finished. If we didn't
									// specify that some disappeared cars continue moving in the background and
									// cause unnecessary increasment of meta.completedCarCounter

			for (int i = 0; i < cars.size(); i++) {

				// The statement below says: if the car in the list is not equal to the instance
				// itself and iterated car is stopped in one of the lights and if that trafficLight
				// intersects with path of the instance and if trafficLight list of the car
				// instance includes this traffic light and the distance distance between car
				// and the instance itself is less than 15
				if (cars.get(i) != this && cars.get(i).stoppedLight >= 0
						&& calculateClosestDistance(this, cars.get(i)) < 15
						&& trafficLight.lights.get(cars.get(i).stoppedLight).pathsTL
								.contains(this.pathTransition.getPath())) {
					if (CarTL.contains(trafficLight.lights.get(cars.get(i).stoppedLight))) {
						this.pathTransition.pause();
						this.stoppedLight = cars.get(i).stoppedLight;
						closed = true;
					}
				}

			}

			if (this.stoppedLight >= 0 && this.stoppedLight < trafficLight.lights.size()) {// this.stoppedLight <
																							// trafficLight.lights.size()
																							// prevents other cars that
																							// remains in the past level
																							// to check
																							// trafficLight.lights list
																							// of current level. This
																							// prevents any possible
																							// ArrayIndexOutOfBoundsException.
				if ((!closed && !trafficLight.lights.get(stoppedLight).circle.getBoundsInParent()
						.intersects(this.getBoundsInParent()))
						|| trafficLight.lights.get(this.stoppedLight).condition == 1) {
					this.pathTransition.play();
					this.stoppedLight = -1; // -1 means that trafficLight does not make the instance stopped.
				}
			}
		}

		public static boolean spawnable(int a) {
			boolean spawn = true;
			for (int i = 0; i < cars.size(); i++) {
				if (circles[a].getBoundsInParent().intersects(cars.get(i).getBoundsInParent())) {
					spawns[a] = false;
					break;
				}
				spawns[a] = true;
			}
			return spawn;
		}

	}

	class trafficLight extends Line {//trafficLight is a line actually which is binded with a circle in the middle of itself
		public Circle circle;
		public int condition = 1;// 0 for red 1 for green
		public static int id = 0;
		public double x1;
		public double x2;
		public double y1;
		public double y2;
		public static ArrayList<trafficLight> lights = new ArrayList<trafficLight>();
		public ArrayList<Path> pathsTL = new ArrayList<Path>();

		trafficLight(double x1, double y1, double x2, double y2) {
			super(x1, y1, x2, y2);//call superclass constructor to create a line
			this.x1 = x1;
			this.x2 = x2;
			this.y1 = y1;
			this.y2 = y2;

			this.setStroke(Color.BLACK);
			this.circle = new Circle((x1 + x2) / 2, (y1 + y2) / 2, 5);
			TLPane.getChildren().addAll(this, this.circle);
			circle.setFill(Color.GREEN);
			lights.add(this);

			circle.setOnMouseClicked(event -> lightChange());
		}

		//Changes the color of the trafficlight
		public void lightChange() {
			if (condition == 1) {
				this.condition = 0;
				this.circle.setFill(Color.RED);

			} else {
				this.condition = 1;
				this.circle.setFill(Color.GREEN);


			}

		}

		public int getConditition() {
			return this.condition;
		}
	}

	private double calculatePathLength(Path path) {
		double length = 0;
		// find sum of length of each path Elements of a path to find the lentgth of the path 
		// PathElement MoveTo ve LineTo'nun superclassı
		for (int i = 1; i < path.getElements().size(); i++) {
			PathElement pathElement = path.getElements().get(i);
			PathElement pathElementOld = path.getElements().get(i - 1);

			// First element is MoveTo the rest are LineTo so we initialize first two element different then others to find the distance between them
			if (pathElementOld instanceof MoveTo) {
				MoveTo moveTo = (MoveTo) pathElementOld;
				LineTo lineTo = (LineTo) pathElement;
				length += Math
						.sqrt(Math.pow(moveTo.getX() - lineTo.getX(), 2) + Math.pow(moveTo.getY() - lineTo.getY(), 2));

			} else if (pathElementOld instanceof LineTo) {
				LineTo lineToOld = (LineTo) path.getElements().get(i - 1);
				LineTo lineTo = (LineTo) pathElement;
				length += Math.sqrt(
						Math.pow(lineToOld.getX() - lineTo.getX(), 2) + Math.pow(lineToOld.getY() - lineTo.getY(), 2));

			}
		}
		return length;
	}

	//We check if the shape intersects with a path
	private boolean intersects(Path path, Shape tf) {
		for (int i = 1; i < path.getElements().size(); i++) {
			PathElement pathElement = path.getElements().get(i);
			PathElement pathElementOld = path.getElements().get(i - 1);

			// we check only LineTo elements for intersection
			if (pathElementOld instanceof LineTo) {
				// LineTo segment
				LineTo lineToOld = (LineTo) path.getElements().get(i - 1);
				LineTo lineTo = (LineTo) pathElement;
				if (tf.getBoundsInParent()
						.intersects((new Line(lineToOld.getX(), lineToOld.getY(), lineTo.getX(), lineTo.getY()))
								.getBoundsInParent()))
					return true;

			}
		}
		return false;

	}

	public static void main(String[] args) {
		launch(args);
	}

	private void spawnCar() {

		int i = (int) (Math.random() * (paths.length)); // select random index of paths array

		Car.spawnable(i);//
		if (spawns[i] == true) {
			Car car = new Car(14, 7, -50, -50);
			car.pathTransition = new PathTransition();

			car.pathTransition.setPath(paths[i]); // add path and car to pathTransition
			car.pathTransition.setNode(car);
			car.pathTransition.setInterpolator(javafx.animation.Interpolator.LINEAR); // Set to linear to avoiding accelerated motion at the beginning and end of the movement

			double pathLength = calculatePathLength(paths[i]); // To set same constant speed all the cars. Make the duration proportional to length of the path
			double durationSeconds = pathLength * 12;
			car.pathTransition.setDuration(Duration.millis(durationSeconds));
			car.pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT); //adjust the rectangle car rotation related to path 
			car.pathTransition.setAutoReverse(false);
			car.pathTransition.play();
			CarPane.getChildren().add(car);

		}

	}

	double time = 0;// time is increased 0.16 in each update call to accomplish this we declare it global.

	private void createTraffic() {
		AnimationTimer timer = new AnimationTimer() {
			boolean StopCommand = false;//it is true if the game is over so new car won't be spawned anymore

			@Override
			public void handle(long now) {
				if (meta.getCrashCounter() >= meta.getLossCond()
						|| meta.getCompletedCarCounter() >= meta.getWinCond()) {
					StopCommand = true;
				}
				if (!StopCommand)
					update();
			}
		};
		timer.start();
	}
	
	
//the logic of spawning cars in random times
	private void update() {
		time += 0.16;

		if (time > 2) {
			if (Math.random() < 0.2) {

				spawnCar();

			}
			//set time to 0 for next call
			time = 0;
		}

	}
}