package experiment;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import main.PopulationQuery;
import org.junit.BeforeClass;
import org.junit.Test;
import queryresponders.SimpleSequential;
import queryresponders.ComplexSequential;


public class RunTimeTests {

    static final int QUERYSIZE = 20;
    static final int WARMUPSIZE = 5;
    static final int NUMTRIALS = 15;
    static int east, north;
    static CensusGroup[] data;

    @BeforeClass
    public static void init(){
        data = PopulationQuery.parse("CenPop2010.txt");
        east = (int)Math.random()*(500-3)+2;
        north = (int)Math.random()*(100-3)+2;
    }

    @Test
    public void testSequentials() {
        //Simple Sequential
        long totalRuntime = 0;
        for (int j = 0; j < NUMTRIALS + WARMUPSIZE; j++){
            long startTime = System.nanoTime();
            QueryResponder STUDENT_100_500Complex = new ComplexSequential(data, 500, 100);
            for (int i = 0; i < QUERYSIZE; i++){
                STUDENT_100_500Complex.getPopulation(1,1,500,100);
            }
            long stopTime = System.nanoTime();

            if (j >= WARMUPSIZE){
//                System.out.println("Complex " + (j - WARMUPSIZE + 1) + ": " + (stopTime - startTime));
                totalRuntime += (stopTime - startTime);
            }
        }
        System.out.println("Averaged Complex: " + (totalRuntime / NUMTRIALS));

        //Complex Sequential
        totalRuntime = 0;
        for (int j = 0; j < NUMTRIALS + WARMUPSIZE; j++){
            long startTime = System.nanoTime();
            QueryResponder STUDENT_100_500Simple = new SimpleSequential(data, 500, 100);
            for (int i = 0; i < QUERYSIZE; i++){
                STUDENT_100_500Simple.getPopulation(1,1,500,100);
            }
            long stopTime = System.nanoTime();

            if (j >= WARMUPSIZE){
//                System.out.println("Simple " + (j - WARMUPSIZE + 1) + ": " + (stopTime - startTime));
                totalRuntime += (stopTime - startTime);
            }
        }
        System.out.println("Averaged Simple: " + (totalRuntime / NUMTRIALS));
    }


}
