package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;

/*
   1) This class is used in version 5 to create the initial grid holding the total population for each grid cell
        - You should not be using the ForkJoin framework but instead should make use of threads and locks
        - Note: the resulting grid after all threads have finished running should be the same as the final grid from
          PopulateGridTask.java
 */

public class PopulateLockedGridTask extends Thread {
    CensusGroup[] censusGroups;
    int lo, hi, numRows, numColumns;
    MapCorners corners;
    double cellWidth, cellHeight;
    int[][] populationGrid;
    Integer[][] lockGrid;

    public PopulateLockedGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners,
                                  double cellWidth, double cellHeight, int[][] popGrid, Integer[][] lockGrid) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.corners = corners;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.populationGrid = popGrid;
        this.lockGrid = lockGrid;
    }

    @Override
    public void run() {
        // Makes Initial Grid
        for (CensusGroup c: Arrays.copyOfRange(censusGroups, lo, hi)){
            int hor = (int)((c.longitude - corners.west) / cellWidth + 1);
            if(hor >= populationGrid.length) hor--;

            int ver = (int)((c.latitude - corners.south) / cellHeight + 1);
            if(ver >= populationGrid[hor].length) ver--;

            synchronized (lockGrid[hor][numRows - ver]) {
                populationGrid[hor][numRows - ver] += c.population;
            }
        }
    }
}
