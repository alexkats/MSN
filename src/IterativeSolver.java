import java.util.*;

public class IterativeSolver {
    private double r;
    private final double x0;

    public IterativeSolver(double r, double x0) {
        this.r = r;
        this.x0 = x0;
    }

    public List<Double> solve(int n) {
        ArrayList<Double> coords = new ArrayList<>();
        coords.add(x0);
        double xcur = x0;
        for (int i = 0; i < n; i++) {
            xcur = r * xcur * (1 - xcur);
            coords.add(xcur);
        }
        return coords;
    }

    List<Double> getAllLims(int bifurcation, List<Double> coordinates, double eps, boolean firstTry) {
        List<Double> allLims = new ArrayList<>();
        for (int i = 0; i < bifurcation; i++) {
            double numberOfOk = 0.0;
            int maximalSizeWithCurrentBifurcation = -1;
            for (int j = i; j < coordinates.size(); j += bifurcation) {
                for (int k = j; k < coordinates.size(); k += bifurcation) {
                    Double coordinate = coordinates.get(j);
                    Double coordinate2 = coordinates.get(k);
                    if (Math.abs(coordinate - coordinate2) < eps) {
                        numberOfOk++;
                    } else if (numberOfOk > 0) {
                        numberOfOk = 0.0;
                    }
                }
                maximalSizeWithCurrentBifurcation = j;
            }
            if (numberOfOk >= 2.0) {
                allLims.add((coordinates.get(maximalSizeWithCurrentBifurcation) + coordinates.get(maximalSizeWithCurrentBifurcation - i - 1)) / 2);
            } else {
                if (firstTry) {
                    allLims = getAllLims(bifurcation * 2, coordinates, eps, false); // Maybe, it was a bifurcation, so we can't get lims with last bifurcation parameter
                    return allLims;
                } else {
                    return null;
                }
            }
        }
        return allLims;
    }

    public Map<Double, List<Double>> getLims() {
        Map<Double, List<Double>> lims = new HashMap<>();
        final double eps = 1e-5;
        final double step = 0.01;
        final double maximalR = 4.0;
        int bifurcation = 1;
        double r = 0.0;
        while (r < maximalR) {
            this.r = r;
            List<Double> coordinates = solve(1500);
            List<Double> limsForR = getAllLims(bifurcation, coordinates, eps, true);
            if (limsForR != null && limsForR.size() == bifurcation * 2) {
                bifurcation *= 2;
            }
            if (limsForR != null && limsForR.size() != bifurcation && limsForR.size() != bifurcation * 2) {
                throw new RuntimeException("Bug in getLims function: bifurcation = " + bifurcation + ", limsForR.size() = " + limsForR.size());
            }
            if (limsForR != null) {
                limsForR.sort(Comparator.naturalOrder());
            }
            lims.put(r, limsForR);
            r += step;
        }
        return lims;
    }
}
