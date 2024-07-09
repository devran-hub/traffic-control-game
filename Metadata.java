import java.io.FileNotFoundException;

import javafx.application.Application;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;

//Devrim Polat 150122051
//Erhan Özer 150122001
//Burak Demirer 150122030
public class Metadata extends GridPane {
	private double height;
	private double width;
	private int numOfRows;
	private int numOfColumns;
	private int numOfPaths;
	private int winCond;
	private int lossCond;
	private int crashCounter;
	private int completedCarCounter;
	

	public int getCrashCounter() {
		return crashCounter;
	}

	public void setCrashCounter(int crashCounter) {
		this.crashCounter = crashCounter;
	}

	public int getCompletedCarCounter() {
		return completedCarCounter;
	}

	public void setCompletedCarCounter(int completedCarCounter) {
		this.completedCarCounter = completedCarCounter;
	}

	public double Height() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double Width() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public int getNumOfRows() {
		return numOfRows;
	}

	public void setNumOfRows(int numOfRows) {
		this.numOfRows = numOfRows;
	}
	
	public int getNumOfColumns() {
		return numOfColumns;
	}

	public void setNumOfColumns(int numOfColumns) {
		this.numOfColumns = numOfColumns;
	}

	public int getNumOfPaths() {
		return numOfPaths;
	}

	public void setNumOfPaths(int numOfPaths) {
		this.numOfPaths = numOfPaths;
	}

	public int getWinCond() {
		return winCond;
	}

	public void setWinCond(int winCond) {
		this.winCond = winCond;
	}

	public int getLossCond() {
		return lossCond;
	}

	public void setLossCond(int lossCond) {
		this.lossCond = lossCond;
	}



	Metadata(double h, double w, int nor, int noc, int len, int win, int loss) {
		height = h;
		width = w;
		numOfRows = nor;
		numOfColumns = noc;
		numOfPaths = len;
		winCond = win;
		lossCond = loss;
		double strokeSize = h / nor * 0.01;
		for (int i = 0; i < nor; i++) {
			for (int j = 0; j < noc; j++) {
				Rectangle square = new Rectangle(h / nor - strokeSize, w / noc - strokeSize, Color.rgb(165, 198, 221));
				square.setStroke(Color.rgb(154, 183, 205));
				square.setStrokeWidth(h / nor * 0.01);

				add(square, j, i);

			}

		}
		setHgap(0);
		setVgap(0);

	}

}
