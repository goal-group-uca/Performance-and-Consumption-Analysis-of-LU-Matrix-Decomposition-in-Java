/*
 * Based on algorithm: https://github.com/JaiJaveria/Parallel_Crout_Decomposition/
 * (Transcripted to Java)
 */

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CroutParallelVersion implements Runnable
{
    private static double[][] A, L, U;
    private static int j, N, cores;
    private static CountDownLatch latch;

    private int idCore, targetMatrix;
    private int i, k;
    private double sum;

    public CroutParallelVersion(int idCore, int targetMatrix)
    {
        this.idCore = idCore;
        this.targetMatrix = targetMatrix;
    }

    public void run()
    {
        try
        {
            if(targetMatrix == 0)
            {
                for(i = j + idCore; i < N; i+=cores)
                {
                    sum = 0;
                    for (k = 0; k < j; k++)
                    {
                        sum = sum + L[i][k] * U[k][j];
                    }
                    L[i][j] = A[i][j] - sum;
                }
            }
            else if(targetMatrix == 1)
            {
                for(i = j + idCore; i < N; i+=cores)
                {
                    sum = 0;
                    for(k = 0; k < j; k++)
                    {
                        sum = sum + L[j][k] * U[k][i];
                    }
                    if(L[j][j] == 0)
                    {
                        throw new ArithmeticException("Division by zero.");
                    }
                    U[j][i] = (A[j][i] - sum) / L[j][j];
                }
            }
        }
        finally
        {
            latch.countDown();
        }
    }

    public static void decomposition(double[][] A, double[][] L, double[][] U, int N, int cores)
    {
        CroutParallelVersion.U = U;
        CroutParallelVersion.L = L;
        CroutParallelVersion.A = A;
        CroutParallelVersion.N = N;
        CroutParallelVersion.cores = cores;

        ExecutorService pool = Executors.newFixedThreadPool(cores);

        for(int i = 0; i < N; i++)
            U[i][i] = 1;

        for(int j = 0; j < N; j++)
        {
            CroutParallelVersion.j = j;

            //Bucle L
            latch = new CountDownLatch(cores);

            for(int task = 0; task < cores; task++)
                pool.execute(new CroutParallelVersion(task, 0));

            try{latch.await();}
            catch(InterruptedException e){}

            //Bucle U
            latch = new CountDownLatch(cores);

            for(int task = 0; task < cores; task++)
                pool.execute(new CroutParallelVersion(task, 1));

            try{latch.await();}
            catch(InterruptedException e){}
        }

        pool.shutdown();
    }
}
