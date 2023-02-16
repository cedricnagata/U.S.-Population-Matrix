package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.PopulateGridTask;
import paralleltasks.PopulateLockedGridTask;

import java.util.concurrent.ForkJoinPool;

import static queryresponders.ComplexSequential.adjustGrid;

public class ComplexLockBased extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool(); // only to invoke CornerFindingTask
    public int NUM_THREADS = 4;

    CensusGroup[] cData;

    int[][] grid;
    Integer[][] lockGrid;

    int cols;
    int rows;

    public ComplexLockBased(CensusGroup[] censusData, int numColumns, int numRows) {
        cData = censusData;
        cols = numColumns;
        rows = numRows;
        totalPopulation = 0;

        CornerFindingResult cornerResult = POOL.invoke(new CornerFindingTask(censusData,0, censusData.length));
        MapCorners corners = cornerResult.getMapCorners();

        grid = new int[numColumns + 1][numRows + 1];
        lockGrid = new Integer[numColumns + 1][numRows + 1];

        for (int i = 0; i < lockGrid.length; i++) {
            for (int j = 0; j < lockGrid[0].length; j++)
                lockGrid[i][j] = 0;
        }

        double cellWidth = (corners.east - corners.west) / cols;
        double cellHeight = (corners.north - corners.south) / rows;

        PopulateLockedGridTask[] threads = new PopulateLockedGridTask[NUM_THREADS];

        for (int i = 0; i < (NUM_THREADS - 1); i++) {
            threads[i] = new PopulateLockedGridTask(cData, i * (cData.length / NUM_THREADS),
                    (i + 1) * (cData.length / NUM_THREADS), numRows, numColumns, corners,
                    cellWidth, cellHeight, grid, lockGrid);

            threads[i].start();
        }

        threads[NUM_THREADS - 1] = new PopulateLockedGridTask(cData, (NUM_THREADS - 1) * (cData.length / NUM_THREADS),
                cData.length, numRows, numColumns, corners, cellWidth, cellHeight, grid, lockGrid);

        threads[NUM_THREADS - 1].start();

        for (int i = 0; i < (NUM_THREADS); i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        adjustGrid(grid);
        totalPopulation = grid[numColumns][0];
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        if (east > cols || north > rows || west < 0 || south < 0 || east < west || north < south) {
            throw new IllegalArgumentException("Input is not a vaild rectangle");
        }

        return grid[east][rows - north] - grid[east][rows - south + 1] -
                grid[west - 1][rows - north] + grid[west - 1][rows - south + 1];
    }

}
