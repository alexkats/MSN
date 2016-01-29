import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.commons.math3.complex.Complex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;
    private static final int POS_X = 200;
    private static final int POS_Y = 50;
    private ArrayList<Button> topButtons = new ArrayList<>();
    private ArrayList<Button> leftButtons = new ArrayList<>();
    private ArrayList<HBox> leftTextBox = new ArrayList<>();
    private Canvas canvas;
    private static final int ITERATIVE = 0;
    private static final int NEWTON = 1;
    private static final int CLEAR = 2;
    private static final int LIMITS = 0;
    private static final int SERIES = 1;
    private static final int TRAJECTORY = 2;
    private TextField X0TextField;
    private TextField RTextField;
    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Homework #2");

        HBox topButtonsField = new HBox(50);
        HBox mainInfo = new HBox(100);
        topButtonsField.setPadding(new Insets(20, 0, 20, 0));
        topButtonsField.setAlignment(Pos.CENTER);
        topButtonsField.setStyle("-fx-background-color: #f7fff2");
        topButtons.add(new Button("Iterative method"));
        topButtons.add(new Button("Newton method"));
        topButtons.add(new Button("Clear"));
        leftButtons.add(new Button("Draw limits plot"));
        leftButtons.add(new Button("Draw plot for X_n series"));
        leftButtons.add(new Button("Draw trajectory plot"));
        HBox X0Box = new HBox(10);
        Text X0Text = new Text("x0");
        X0TextField = new TextField("0.5");
        X0Box.getChildren().addAll(X0Text, X0TextField);
        HBox RBox = new HBox(10);
        Text RText = new Text("  r ");
        RTextField = new TextField("1.5");
        RBox.getChildren().addAll(RText, RTextField);
        LineChart<Number, Number> chart = new LineChart<>(new NumberAxis(), new NumberAxis());
        chart.setCreateSymbols(true);
        chart.setMinSize(600, 600);
        chart.setVisible(false);
        canvas = new Canvas(600, 600);
        VBox leftButtonsField = new VBox(20);
        leftButtonsField.setPadding(new Insets(120, 0, 0, 50));
        leftTextBox.add(X0Box);
        leftTextBox.add(RBox);
        leftButtonsField.getChildren().addAll(leftTextBox);
        leftButtonsField.getChildren().addAll(leftButtons);
        Group group = new Group(chart, canvas);
        mainInfo.getChildren().addAll(leftButtonsField, group);

        topButtons.get(NEWTON).setOnAction(event -> {
            executor.execute(() -> {
                Platform.runLater(() -> {
                    chart.setVisible(false);
                    canvas.setVisible(true);
                    canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                });

                leftButtonsField.setVisible(false);
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                PixelWriter pixelWriter = writableImage.getPixelWriter();
                NewtonSolver newtonSolver = new NewtonSolver((int) canvas.getWidth(), (int) canvas.getHeight());
                Map<Complex, Integer> newtonResults = newtonSolver.solve();

                for (Map.Entry<Complex, Integer> curr : newtonResults.entrySet()) {
                    Color color = Color.BLACK;

                    switch (curr.getValue()) {
                        case 0:
                            color = Color.AQUA;
                            break;
                        case 1:
                            color = Color.BLUEVIOLET;
                            break;
                        case 2:
                            color = Color.BLUE;
                            break;
                    }

                    pixelWriter.setColor((int) (curr.getKey().getReal() + canvas.getWidth() / 2), (int) (curr.getKey().getImaginary() + canvas.getHeight() / 2), color);
                }

                Platform.runLater(() -> canvas.getGraphicsContext2D().drawImage(writableImage, 0, 0));
            });
        });

        topButtons.get(ITERATIVE).setOnAction(event -> {
            chart.setVisible(false);
            canvas.setVisible(true);
            leftButtonsField.setVisible(true);
        });

        topButtons.get(CLEAR).setOnAction(event -> {
            chart.getData().clear();
            chart.setVisible(false);
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        });

        leftButtons.get(LIMITS).setOnAction(event -> {
            chart.setVisible(false);
            canvas.setVisible(true);
            double r = Double.parseDouble(RTextField.getText());
            double x0 = Double.parseDouble(X0TextField.getText());
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            IterativeSolver iterativeSolver = new IterativeSolver(r, x0);
            Map<Double, List<Double>> results = iterativeSolver.getLims();
            IterativeLimits iterativeLimits = new IterativeLimits(results, canvas);
            iterativeLimits.draw();
        });

        leftButtons.get(SERIES).setOnAction(event -> {
            chart.setVisible(true);
            canvas.setVisible(false);
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            XYChart.Series series = new XYChart.Series();
            ObservableList<XYChart.Data> datas = FXCollections.observableArrayList();
            double r = Double.parseDouble(RTextField.getText());
            double x0 = Double.parseDouble(X0TextField.getText());
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            IterativeSolver iterativeSolver = new IterativeSolver(r, x0);
            List<Double> results = iterativeSolver.solve(150);

            for (int i = 0; i < results.size(); i++) {
                datas.add(new XYChart.Data<>(i, results.get(i)));
            }

            series.setData(datas);
            series.setName("x0 = " + x0 + "; r = " + r);
            chart.getData().add(series);
        });

        leftButtons.get(TRAJECTORY).setOnAction(event -> {
            chart.setVisible(false);
            canvas.setVisible(true);
            double r = Double.parseDouble(RTextField.getText());
            double x0 = Double.parseDouble(X0TextField.getText());
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            IterativeSolver iterativeSolver = new IterativeSolver(r, x0);
            List<Double> results = iterativeSolver.solve(150);
            IterativeTrajectory iterativeTrajectory = new IterativeTrajectory(results, r, canvas);
            iterativeTrajectory.draw();
        });

        topButtonsField.getChildren().addAll(topButtons);
        VBox vBox = new VBox(20);
        vBox.getChildren().add(topButtonsField);
        vBox.getChildren().add(mainInfo);
        vBox.setStyle("-fx-background-color: white");
        primaryStage.setX(POS_X);
        primaryStage.setY(POS_Y);
        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMinHeight(HEIGHT);
        primaryStage.setScene(new Scene(vBox, WIDTH, HEIGHT, Color.BLACK));
        primaryStage.show();
    }
}
