package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.GetPopulationTask;

import java.util.concurrent.ForkJoinPool;

public class SimpleParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();

    CensusGroup[] cData;
    CornerFindingResult cornerResult;
    MapCorners corners;
    int cols;
    int rows;

    public SimpleParallel(CensusGroup[] censusData, int numColumns, int numRows) {
        cData = censusData;
        cols = numColumns;
        rows = numRows;
        cornerResult = POOL.invoke(new CornerFindingTask(censusData,0, censusData.length));
        corners = cornerResult.getMapCorners();
    }

    @Override
    public int getTotalPopulation(){
        return cornerResult.getTotalPopulation();
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
        return POOL.invoke(new GetPopulationTask(cData,0, cData.length, westCoor, southCoor, eastCoor, northCoor, corners));

    }
}
