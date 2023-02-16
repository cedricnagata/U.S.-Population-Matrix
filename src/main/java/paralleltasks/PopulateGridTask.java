package paralleltasks;

import cse332.types.CensusGroup;
import cse332.types.MapCorners;
import queryresponders.ComplexParallel;
import queryresponders.ComplexSequential;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
/*
   1) This class is used in version 4 to create the initial grid holding the total population for each grid cell
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) Note that merging the grids from the left and right subtasks should NOT be done in this class.
      You will need to implement the merging in parallel using a separate parallel class (MergeGridTask.java)
 */

public class PopulateGridTask extends RecursiveTask<int[][]> {
    final static int SEQUENTIAL_CUTOFF = 10000;
    private static final ForkJoinPool POOL = new ForkJoinPool();

    CensusGroup[] censusGroups;
    int lo, hi, numRows, numColumns;
    MapCorners corners;
    double horizontalInc, verticalInc;

    public PopulateGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns,
                            MapCorners corners, double horizontalInc, double verticalInc) {

        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.corners = corners;
        this.horizontalInc = horizontalInc;
        this.verticalInc = verticalInc;
    }

    @Override
    protected int[][] compute() {
        if (hi - lo <= SEQUENTIAL_CUTOFF) {
            return sequentialPopulateGrid(censusGroups, corners);
        }
        int mid = lo + (hi - lo) / 2;

        PopulateGridTask left = new PopulateGridTask(censusGroups, lo, mid, numRows, numColumns,
                corners, horizontalInc, verticalInc);
        PopulateGridTask right = new PopulateGridTask(censusGroups, mid, hi, numRows, numColumns,
                corners, horizontalInc, verticalInc);

        left.fork();
        int[][] rightResult = right.compute();
        int[][] leftResult = left.join();

        POOL.invoke(new MergeGridTask(leftResult, rightResult,
                0, numColumns + 1, 0, numRows + 1));
        return leftResult;
    }

    private int[][] sequentialPopulateGrid(CensusGroup[] censusData, MapCorners corners) {
        int[][] grid = new int[numColumns + 1][numRows + 1];

        // Makes Initial Grid
        for (CensusGroup c: Arrays.copyOfRange(censusData, lo, hi)){
            int hor = (int)((c.longitude - corners.west) / horizontalInc + 1);
            if(hor >= grid.length) hor--;

            int ver = (int)((c.latitude - corners.south) / verticalInc + 1);
            if(ver >= grid[hor].length) ver--;

            grid[hor][numRows - ver] += c.population;
        }

        return grid;
    }
}

