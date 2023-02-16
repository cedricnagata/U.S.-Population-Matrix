package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

public class SimpleSequential extends QueryResponder {
    CensusGroup[] cData;
    MapCorners corners;
    int cols;
    int rows;

    public SimpleSequential(CensusGroup[] censusData, int numColumns, int numRows) {
        totalPopulation = 0;
        cData = censusData;
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
        corners = new MapCorners(west, east, north, south);
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        if (east > cols || north > rows || west < 0 || south < 0 || east < west || north < south) {
            throw new IllegalArgumentException("Input is not a vaild rectangle");
        }

        double horizontalInc = (corners.east - corners.west) / cols;
        double verticalInc = (corners.north - corners.south) / rows;

        if (east == cols){
            east++;
        }
        if (north == rows){
            north++;
        }

        double westCoor = ((west - 1) * horizontalInc) + corners.west;
        double eastCoor = (east * horizontalInc) + corners.west;
        double southCoor = ((south - 1) * verticalInc) + corners.south;
        double northCoor = (north * verticalInc) + corners.south;

        int totalPopulation = 0;

        for (CensusGroup c: cData) {
            if (c.longitude >= westCoor && c.longitude < eastCoor &&
                    c.latitude >= southCoor && c.latitude < northCoor) {
                totalPopulation += c.population;
            }
        }

        return totalPopulation;
    }
}
