package paralleltasks;

import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;

import java.util.Arrays;
import java.util.concurrent.RecursiveTask;

/*
   1) This class will do the corner finding from version 1 in parallel for use in versions 2, 4, and 5
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) The compute method returns a result of a MapCorners and an Integer.
        - The MapCorners will represent the extremes/bounds/corners of the entire land mass (latitude and longitude)
        - The Integer value should represent the total population contained inside the MapCorners
 */

public class CornerFindingTask extends RecursiveTask<CornerFindingResult> {
    final int SEQUENTIAL_CUTOFF = 10000;
    CensusGroup[] censusGroups;
    int lo, hi;

    public CornerFindingTask(CensusGroup[] censusGroups, int lo, int hi) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
    }

    // Returns a pair of MapCorners for the grid and Integer for the total population
    // Key = grid, Value = total population
    @Override
    protected CornerFindingResult compute() {
        if (hi - lo <= SEQUENTIAL_CUTOFF) {
            return sequentialCornerFinding(censusGroups, lo, hi);
        }
        int mid = lo + (hi - lo) / 2;

        CornerFindingTask left = new CornerFindingTask(censusGroups, lo, mid);
        CornerFindingTask right = new CornerFindingTask(censusGroups, mid, hi);

        left.fork();
        CornerFindingResult rightResult = right.compute();
        CornerFindingResult leftResult = left.join();

        MapCorners combinedMapCorners = leftResult.getMapCorners().encompass(rightResult.getMapCorners());
        return new CornerFindingResult(combinedMapCorners, leftResult.getTotalPopulation() + rightResult.getTotalPopulation());

    }

    private CornerFindingResult sequentialCornerFinding(CensusGroup[] censusGroups, int lo, int hi) {
        double west = censusGroups[lo].longitude;
        double east = censusGroups[lo].longitude;
        double north = censusGroups[lo].latitude;
        double south = censusGroups[lo].latitude;

        MapCorners corners;
        int totalPopulation = 0;
        for (CensusGroup c: Arrays.copyOfRange(censusGroups, lo, hi)) {
            if (c.longitude > east) {
                east = c.longitude;
            }
            if (c.longitude < west) {
                west = c.longitude;
            }

            if (c.latitude > north) {
                north = c.latitude;
            }
            if (c.latitude < south) {
                south = c.latitude;
            }
            totalPopulation += c.population;

        }
        corners = new MapCorners(west, east, north, south);
        return new CornerFindingResult(corners, totalPopulation);
    }
}

