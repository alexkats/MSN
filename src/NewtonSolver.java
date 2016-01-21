import org.apache.commons.math3.complex.Complex;

import java.util.*;

public class NewtonSolver {
    private final int width;
    private final int height;
    public static final double EPS = 1e-9;
    private final ArrayList<Complex> roots;

    public NewtonSolver(int width, int height) {
        this.width = width;
        this.height = height;
        roots = new ArrayList<>();
        roots.add(new Complex(1.0, 0.0));
        roots.add(new Complex(-0.5, Math.sqrt(3.0) / 2.0));
        roots.add(new Complex(-0.5, -Math.sqrt(3.0) / 2.0));
    }

    public Map<Complex, Integer> solve() {
        Map<Complex, Integer> colors = new HashMap<>();
        final int maximalNumberOfIterations = 500;
        for (int x = -width / 2; x < width / 2; x++) {
            for (int y = -height / 2; y < height / 2; y++) {
                Complex complex = new Complex(x, y);
                int iterationNumber = 0;
                while (true) {
                    Complex dividend = complex.pow(3.0).subtract(1.0);
                    Complex divider = complex.pow(2.0).multiply(3.0);
                    complex = complex.subtract(dividend.divide(divider)); // next complex
                    boolean ok = false;
                    for (int i = 0; i < roots.size(); i++) {
                        Complex root = roots.get(i);
                        if (Math.abs(root.getReal() - complex.getReal()) < EPS && Math.abs(root.getImaginary() - complex.getImaginary()) < EPS) {
                            colors.put(new Complex(x, y), i);
                            ok = true;
                            break;
                        }
                    }
                    if (ok) {
                        break;
                    }
                    iterationNumber++;
                    if (iterationNumber > maximalNumberOfIterations) {
                        colors.put(new Complex(x, y), 3);
                        break;
                    }
                }
            }
        }
        return colors;
    }
}
