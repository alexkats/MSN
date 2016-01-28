import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IterativeTrajectory {
    private double scaleX = 0.005;
    private double scaleY = 0.005;
    private final int lengthX;
    private final int lengthY;
    private final int indentX = 0;
    private final int indentY = 10;
    private static final int interval = 4;
    private final String nameY = "Y";
    private final String nameX = "X";
    private int zeroX;
    private int zeroY;
    private final double r;
    private final double step = 0.03;
    private final java.util.List<Double> coordinates;
    private final Canvas canvas;
    private GraphicsContext gc;
    private Set<Integer> written;

    public IterativeTrajectory(List<Double> coordinates, double r, Canvas canvas) {
        this.coordinates = coordinates;
        this.r = r;
        this.canvas = canvas;
        this.lengthX = (int) canvas.getWidth();
        this.lengthY = (int) canvas.getHeight();
        this.gc = canvas.getGraphicsContext2D();
        this.written = new HashSet<>();
    }

    private void drawCoordinateAxis() {
        // Y
        gc.setStroke(Color.GRAY);
        gc.strokeLine(lengthX / 2 + indentX, indentY, lengthX / 2 + indentX, lengthY + indentY);
        gc.strokeLine(lengthX / 2 + indentX, indentY, lengthX / 2 + indentX - 3, indentY + 10);
        gc.strokeLine(lengthX / 2 + indentX, indentY, lengthX / 2 + indentX + 3, indentY + 10);
        gc.strokeText(nameY, lengthX / 2 + indentX - 20, indentY + 10);
        zeroX = lengthX / 2 + indentX;
        // X
        gc.strokeLine(indentX, lengthY / 2 + indentY, lengthX + indentX, lengthY / 2 + indentY);
        gc.strokeLine(lengthX + indentX, lengthY / 2 + indentY, lengthX + indentX - 10, lengthY / 2 + indentY - 3);
        gc.strokeLine(lengthX + indentX, lengthY / 2 + indentY, lengthX + indentX - 10, lengthY / 2 + indentY + 3);
        gc.strokeText(nameX, lengthX + indentX - 10, lengthY / 2 + indentY - 10);
        zeroY = lengthY / 2 + indentY;
        gc.strokeText("0", zeroX - 13, zeroY - 8);
    }

    private void drawYEqualsX() {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeLine(indentX, zeroX - indentX + zeroY, indentX + lengthX, zeroX - indentX - lengthX + zeroY);
    }

    private void setScale() {
        scaleX = (double) (0.005 + 0.0003 * r + 0.005 * (r / 10));
        scaleY = (double) (0.005 + 0.0003 * r + 0.005 * (r / 10));
    }

    private void drawGraphic() {
        int lX = lengthX / 2;
        int lY = lengthY / 2;
        int to = (int) (Math.min(100, lX) / step);
        int from = -to;
        for (int i = from; i < to; i++) {
            double j = (double) i * step;
            double y = r * j * (1 - j);
            double nextY = r * ((i + 1) * step) * (1 - (i + 1) * step);
            gc.strokeLine(indentX + lX + ((int) (i * step / scaleX)), indentY + lY - (int) (y / scaleY), indentX + lX + (int) ((i + 1) * step / scaleX), indentY + lY - (int) (nextY / scaleY));
        }
    }

    private void drawTrajectory() {
        Integer k = 1;
        for (Double coordinate : coordinates) {
            gc.setStroke(Color.GREEN);
            double x = coordinate;
            double y = r * x * (1 - x);
            boolean needDraw = true;
            for (int i = indentX + lengthX / 2 + (int) (x / scaleX) - interval; i < indentX + lengthX / 2 + (int) (x / scaleX) + interval; i++) {
                needDraw &= !written.contains(i);
            }
            if (needDraw) {
                gc.strokeLine(indentX + lengthX / 2 + (int) (x / scaleX), zeroY, indentX + lengthX / 2 + (int) (x / scaleX), zeroY - (int) (y / scaleY));
                gc.setStroke(Color.BLUE);
                gc.strokeLine(indentX + lengthX / 2 + (int) (x / scaleX), zeroY - (int) (y / scaleY), zeroX + (int) (y / scaleX), zeroY - (int) (y / scaleY));
                gc.setStroke(Color.RED);
                gc.strokeLine(zeroX + (int) (y / scaleX), zeroY - (int) (y / scaleY), zeroX + (int) (y / scaleX), zeroY);
                gc.fillText(k.toString(), indentX + lengthX / 2 + (int) (x / scaleX) - 5, zeroY + 15, 5);
                gc.setFill(Color.DARKBLUE);
                written.add(indentX + lengthX / 2 + (int) (x / scaleX));
                k++;
            }
        }
    }


    public void draw() {
        drawCoordinateAxis();
        drawYEqualsX();
        setScale();
        drawGraphic();
        drawTrajectory();
    }
}
