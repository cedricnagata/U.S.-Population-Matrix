package experiment.AboveAndBeyond;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import org.junit.BeforeClass;
import org.junit.Test;
import queryresponders.ComplexSequential;


public class NewDataTests {

    static final int QUERYSIZE = 20;
    static final int WARMUPSIZE = 5;
    static final int NUMTRIALS = 15;
    static int east, north;
    static CensusGroup[] data;
    static CensusGroup[] oldData;

    @BeforeClass
    public static void init(){
        data = experiment.AboveAndBeyond.PopulationQuery.parse("CenPop2020_Mean_ST.txt");
        oldData = main.PopulationQuery.parse("CenPop2010.txt");
    }

    @Test
    public void test() {
        QueryResponder New = new ComplexSequential(data, 500, 100);
        int num = New.getPopulation(1,1,500,100);
        System.out.println("TotalPop: " + num);
        System.out.println("North-East Quadrant Population: " + New.getPopulation(251,51,500,100));
        System.out.println("South-East Quadrant Population: " + New.getPopulation(251,1,500,50));
        System.out.println("North-West Quadrant Population: " + New.getPopulation(1,51,250,100));
        System.out.println("South-West Quadrant Population: " + New.getPopulation(1,1,250,50));

        QueryResponder old = new ComplexSequential(oldData, 500, 100);
        num = old.getPopulation(1,1,500,100);
        System.out.println("TotalPop: " + num);
        System.out.println("North-East Quadrant Population: " + old.getPopulation(251,51,500,100));
        System.out.println("South-East Quadrant Population: " + old.getPopulation(251,1,500,50));
        System.out.println("North-West Quadrant Population: " + old.getPopulation(1,51,250,100));
        System.out.println("South-West Quadrant Population: " + old.getPopulation(1,1,250,50));
    }
}
