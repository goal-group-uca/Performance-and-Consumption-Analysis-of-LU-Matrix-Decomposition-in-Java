/*
 * Base algorithm: https://www.geeksforgeeks.org/doolittle-algorithm-lu-decomposition/
 */

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DoolittleParallelVersion implements Runnable
{
    private static double[][] A, L, U;
    private static int i, N, cores;
    private static CountDownLatch latch;

    private int idCore, targetMatrix;
    private int j, k;
    private double sum;

    public DoolittleParallelVersion(int idCore, int targetMatrix)
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
                for(j = i + idCore; j < N; j+=cores)
                {
                    sum = 0;
                    for (k = 0; k < i; k++)
                    {
                        sum = sum + L[i][k] * U[k][j];
                    }
                    U[i][j] = A[i][j] - sum;
                }
            }
            else if(targetMatrix == 1)
            {
                for(j = i + idCore; j < N; j+=cores)
                {
                    sum = 0;
                    for(k = 0; k < i; k++)
                    {
                        sum = sum + L[j][k] * U[k][i];
                    }
                    if(U[i][i] == 0)
                    {
                        throw new ArithmeticException("Division by zero.");
                    }
                    L[j][i] = (A[j][i] - sum) / U[i][i];
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
        DoolittleParallelVersion.U = U;
        DoolittleParallelVersion.L = L;
        DoolittleParallelVersion.A = A;
        DoolittleParallelVersion.N = N;
        DoolittleParallelVersion.cores = cores;

        ExecutorService pool = Executors.newFixedThreadPool(cores);

        for(int i = 0; i < N; i++)
            L[i][i] = 1;

        for(int i = 0; i < N; i++)
        {
            DoolittleParallelVersion.i = i;

            latch = new CountDownLatch(cores);

            for(int task = 0; task < cores; task++)
                pool.execute(new DoolittleParallelVersion(task, 0));

            try{latch.await();}
            catch(InterruptedException e){}

            latch = new CountDownLatch(cores);

            for(int task = 0; task < cores; task++)
                pool.execute(new DoolittleParallelVersion(task, 1));

            try{latch.await();}
            catch(InterruptedException e){}
        }

        pool.shutdown();
    }
}
