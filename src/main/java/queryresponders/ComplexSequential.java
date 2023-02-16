package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;


public class ComplexSequential extends QueryResponder {
    int[][] grid;
    int cols;
    int rows;

    public ComplexSequential(CensusGroup[] censusData, int numColumns, int numRows) {
        grid = new int[numColumns + 1][numRows + 1];
        totalPopulation = 0;
        cols = numColumns;
        rows = numRows;

        double west = censusData[0].longitude;
        double east = censusData[0].longitude;
        double north = censusData[0].latitude;
        double south = censusData[0].latitude;

        for (CensusGroup c: censusData) {
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
        double horizontalInc = (east - west) / cols;
        double verticalInc = (north - south) / rows;

        // Makes Initial Grid
        for (CensusGroup c: censusData){
            int hor = (int)((c.longitude - west) / horizontalInc + 1);
            if(hor >= grid.length) hor--;

            int ver = (int)((c.latitude - south) / verticalInc + 1);
            if(ver >= grid[hor].length) ver--;

            grid[hor][numRows - ver] += c.population;
        }
        adjustGrid(grid);
    }

    public static void adjustGrid(int[][] inputGrid){
        for (int i = 1; i < inputGrid.length; i++){
            for (int j = inputGrid[i].length - 2; j >= 0; j--){
                inputGrid[i][j] = inputGrid[i][j] + inputGrid[i - 1][j] + inputGrid[i][j + 1] - inputGrid[i - 1][j + 1];
            }
        }
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
