package paralleltasks;

import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.Arrays;
import java.util.concurrent.RecursiveTask;

/*
   1) This class is the parallel version of the getPopulation() method from version 1 for use in version 2
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) The double parameters(w, s, e, n) represent the bounds of the query rectangle
   4) The compute method returns an Integer representing the total population contained in the query rectangle
 */
public class GetPopulationTask extends RecursiveTask<Integer> {
    final static int SEQUENTIAL_CUTOFF = 1000;
    CensusGroup[] censusGroups;
    int lo, hi;
    double w, s, e, n;
    MapCorners grid;

    public GetPopulationTask(CensusGroup[] censusGroups, int lo, int hi, double w, double s, double e, double n, MapCorners grid) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.w = w;
        this.s = s;
        this.e = e;
        this.n = n;
        this.grid = grid;

    }

    // Returns a number for the total population
    @Override
    protected Integer compute() {
        if (hi - lo <= SEQUENTIAL_CUTOFF) {
            return sequentialGetPopulation(censusGroups, lo, hi, w, s, e, n);
        }
        int mid = lo + (hi - lo) / 2;

        GetPopulationTask left = new GetPopulationTask(censusGroups, lo, mid, w, s, e, n, grid);
        GetPopulationTask right = new GetPopulationTask(censusGroups, mid, hi, w, s, e, n, grid);

        left.fork();
        int rightResult = right.compute();
        int leftResult = left.join();

        return leftResult + rightResult;
    }

    private Integer sequentialGetPopulation(CensusGroup[] censusGroups, int lo, int hi, double w, double s, double e, double n) {
        int totalPopulation = 0;

        for (CensusGroup c: Arrays.copyOfRange(censusGroups, lo, hi)) {
            if (c.longitude >= w && c.longitude < e &&
                    c.latitude >= s && c.latitude < n) {
                totalPopulation += c.population;
            }
        }
        return totalPopulation;
    }
}
