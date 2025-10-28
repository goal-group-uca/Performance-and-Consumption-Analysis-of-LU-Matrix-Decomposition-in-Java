/*
 * Based on algorithm: https://github.com/JaiJaveria/Parallel_Crout_Decomposition/
 * (Transcripted to Java)
 */

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LUPParallelVersion implements Runnable
{
    private static double[][] A, L, U;
    private static int j, N, cores, pivot;
    private static CountDownLatch latch;
    private static Object lock = new Object();
    private static double maxValue;

    private int idCore, targetMatrix;
    private int i, k;
    private double sum;

    public LUPParallelVersion(int idCore, int targetMatrix)
    {
        this.idCore = idCore;
        this.targetMatrix = targetMatrix;
    }

    public void run()
    {
        try
        {
            if(targetMatrix == -1)
            {
                double localMax = 0;
                int localPivot = j;

                for(i = j + idCore + 1; i < N; i += cores)
                {
                    double absValue = Math.abs(A[i][j]);
                    if (absValue > localMax)
                    {
                        localMax = absValue;
                        localPivot = i;
                    }
                }
                if(localMax > 0)
                {
                    synchronized(lock)
                    {
                        if (localMax > maxValue)
                        {
                            maxValue = localMax;
                            pivot = localPivot;
                        }
                    }
                }
            }
            else if(targetMatrix == 0)
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

    public static void decomposition(double[][] A, double[][] L, double[][] U, double[][] P, int N, int cores)
    {
        LUPParallelVersion.U = U;
        LUPParallelVersion.L = L;
        LUPParallelVersion.A = A;
        LUPParallelVersion.N = N;
        LUPParallelVersion.cores = cores;

        ExecutorService pool = Executors.newFixedThreadPool(cores);

        for(int i = 0; i < N; i++)
            U[i][i] = 1;

        for(int j = 0; j < N; j++)
        {
            LUPParallelVersion.j = j;
            LUPParallelVersion.maxValue = Math.abs(A[j][j]);
            LUPParallelVersion.pivot = j;

            latch = new CountDownLatch(cores);

            for(int task = 0; task < cores; task++)
                pool.execute(new LUPParallelVersion(task, -1));

            try{latch.await();}
            catch(InterruptedException e){}

            if(pivot != j)
            {
                double[] auxarr;
                double auxdbl;

                auxarr = A[j];
                A[j] = A[pivot];
                A[pivot] = auxarr;

                for(int k = 0; k < j; k++)
                {
                    auxdbl = L[j][k];
                    L[j][k] = L[pivot][k];
                    L[pivot][k] = auxdbl;
                }

                auxarr = P[j];
                P[j] = P[pivot];
                P[pivot] = auxarr;
            }

            latch = new CountDownLatch(cores);

            for(int task = 0; task < cores; task++)
                pool.execute(new LUPParallelVersion(task, 0));

            try{latch.await();}
            catch(InterruptedException e){}

            latch = new CountDownLatch(cores);

            for(int task = 0; task < cores; task++)
                pool.execute(new LUPParallelVersion(task, 1));

            try{latch.await();}
            catch(InterruptedException e){}
        }

        pool.shutdown();
    }
}
