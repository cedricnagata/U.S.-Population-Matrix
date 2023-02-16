package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.PopulateGridTask;

import java.util.concurrent.ForkJoinPool;

import static queryresponders.ComplexSequential.adjustGrid;

public class ComplexParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();

    CensusGroup[] cData;

    int[][] grid;
    int cols;
    int rows;

    public ComplexParallel(CensusGroup[] censusData, int numColumns, int numRows) {
        cData = censusData;
        cols = numColumns;
        rows = numRows;
        totalPopulation = 0;

        CornerFindingResult cornerResult = POOL.invoke(new CornerFindingTask(censusData,0, censusData.length));
        MapCorners corners = cornerResult.getMapCorners();

        double horizontalInc = (corners.east - corners.west) / cols;
        double verticalInc = (corners.north - corners.south) / rows;

        grid = POOL.invoke(new PopulateGridTask(cData,0, cData.length, numRows, numColumns,
                corners, horizontalInc, verticalInc));
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
