package paralleltasks;

import java.util.concurrent.RecursiveAction;

/*
   1) This class is used by PopulateGridTask to merge two grids in parallel
   2) SEQUENTIAL_CUTOFF refers to the maximum number of grid cells that should be processed by a single parallel task
 */

public class MergeGridTask extends RecursiveAction {
    final static int SEQUENTIAL_CUTOFF = 10;
    int[][] left, right;
    int rowLo, rowHi, colLo, colHi;

    public MergeGridTask(int[][] left, int[][] right, int rowLo, int rowHi, int colLo, int colHi) {
        this.left = left;
        this.right= right;

        this.rowLo = rowLo;
        this.rowHi = rowHi;
        this.colLo = colLo;
        this.colHi = colHi;

    }

    @Override
    protected void compute() {
        if ((rowHi - rowLo) * (colHi - colLo) < SEQUENTIAL_CUTOFF) {
            sequentialMergeGird();
        }
        else{
            int rowMid = rowLo + (rowHi - rowLo) / 2;
            int colMid = colLo + (colHi - colLo) / 2;

            MergeGridTask Q1 = new MergeGridTask(left, right, rowLo, rowMid, colLo, colMid);
            MergeGridTask Q2 = new MergeGridTask(left, right, rowMid, rowHi, colLo, colMid);

            MergeGridTask Q3 = new MergeGridTask(left, right, rowLo, rowMid, colMid, colHi);
            MergeGridTask Q4 = new MergeGridTask(left, right, rowMid, rowHi, colMid, colHi);

            Q1.fork();
            Q2.fork();
            Q3.fork();
            Q4.compute();
            Q1.join();
            Q2.join();
            Q3.join();
        }
    }

    // according to google gird means "prepare oneself for something difficult or challenging" so this typo is intentional :)
    private void sequentialMergeGird() {
        for (int i = colLo; i < colHi; i++) {
            for (int j = rowLo; j < rowHi; j++)
                left[j][i] += right[j][i];
        }
    }
}
