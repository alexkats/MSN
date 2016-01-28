import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;

public class IterativeLimits {
    private static final double scaleX = 0.008;
    private static final double scaleY = 0.002;
    private final int lengthX;
    private final int lengthY;
    private static final int indentX = 10;
    private static final int indentY = 10;
    private static final String nameY = "lim(xn)";
    private static final String nameX = "r";
    private int zeroX;
    private int zeroY;
    private static final double step = 0.01;
    private final Map<Double, List<Double>> coordinates;
    private GraphicsContext gc;

    public IterativeLimits(Map<Double, List<Double>> coordinates, Canvas canvas) {
        this.coordinates = coordinates;
        this.lengthX = (int) canvas.getWidth();
        this.lengthY = (int) canvas.getHeight();
        this.gc = canvas.getGraphicsContext2D();
    }

    private void drawCoordinateAxis() {
        // Y
        gc.setStroke(Color.GRAY);
        gc.strokeLine(indentX, indentY, indentX, lengthY - indentY);
        gc.strokeLine(indentX, indentY, indentX - 3, indentY + 10);
        gc.strokeLine(indentX, indentY, indentX + 3, indentY + 10);
        gc.strokeText(nameY, indentX + 10, indentY + 10);
        zeroX = indentX;
        // X
        gc.strokeLine(indentX, lengthY - indentY, lengthX, lengthY - indentY);
        gc.strokeLine(lengthX, lengthY - indentY, lengthX - 10, lengthY - indentY - 3);
        gc.strokeLine(lengthX, lengthY - indentY, lengthX - 10, lengthY - indentY + 3);
        gc.strokeText(nameX, lengthX - 10, lengthY - indentY - 10);
        zeroY = lengthY - indentY;
        gc.strokeText("0", zeroX - 10, zeroY + 2 * indentY - 10);
    }

    private void drawLimits() {
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1.5);
        List<Double> last = coordinates.get(0.0);
        for (Double coordinate = step; coordinate < 4.0; coordinate += step) {
            double lastX = coordinate - step;
            List<Double> now = coordinates.get(coordinate);
            if (now != null) {
                if (last.size() == now.size()) {
                    int i = 0;
                    for (Double lastElem : last) {
                        gc.strokeLine(indentX + (int) (lastX / scaleX), zeroY - (int) (lastElem / scaleY), indentX + (int) (coordinate / scaleX), zeroY - (int) (now.get(i) / scaleY));
                        i++;
                    }
                } else if (last.size() * 2 == now.size()) {
                    int i = 0;
                    for (Double lastElem : last) {
                        gc.strokeLine(indentX + (int) (lastX / scaleX), zeroY - (int) (lastElem / scaleY), indentX + (int) (coordinate / scaleX), zeroY - (int) (now.get(2 * i) / scaleY));
                        gc.strokeLine(indentX + (int) (lastX / scaleX), zeroY - (int) (lastElem / scaleY), indentX + (int) (coordinate / scaleX), zeroY - (int) (now.get(2 * i + 1) / scaleY));
                        i++;
                    }
                }
                last = now;
            } else {
                gc.setStroke(Color.GREEN);
                gc.strokeLine(indentX + (int) (coordinate / scaleX), indentY, indentX + (int) (coordinate / scaleX), zeroY);
                gc.strokeText(coordinate.toString().substring(0, Math.min(((Double) step).toString().length() + 1, coordinate.toString().length())), zeroX - 10 + (int) (coordinate / scaleX), zeroY + 2 * indentY - 10);
                return;
            }
        }
    }


    public void draw() {
        drawCoordinateAxis();
        drawLimits();
    }
}
