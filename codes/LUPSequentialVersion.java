/*
 * Based on algorithm: https://github.com/JaiJaveria/Parallel_Crout_Decomposition/
 * (Transcripted to Java)
 */

public class LUPSequentialVersion
{
    public static void decomposition(double[][] A, double[][] L, double[][] U, double[][] P, int n)
    {
        int i, j, k;
        double sum = 0;
        for(i = 0; i < n; i++)
        {
            U[i][i] = 1;
        }
        for(j = 0; j < n; j++)
        {
            int pivot = j;
            double max = Math.abs(A[j][j]);
            for(i = j + 1; i < n; i++)
            {
                if (Math.abs(A[i][j]) > max)
                {
                    max = Math.abs(A[i][j]);
                    pivot = i;
                }
            }
            if(pivot != j)
            {
                double[] auxarr;
                double auxdbl;

                auxarr = A[j];
                A[j] = A[pivot];
                A[pivot] = auxarr;

                for(k = 0; k < j; k++)
                {
                    auxdbl = L[j][k];
                    L[j][k] = L[pivot][k];
                    L[pivot][k] = auxdbl;
                }

                auxarr = P[j];
                P[j] = P[pivot];
                P[pivot] = auxarr;
            }

            for(i = j; i < n; i++)
            {
                sum = 0;
                for (k = 0; k < j; k++)
                {
                    sum = sum + L[i][k] * U[k][j];
                }
                L[i][j] = A[i][j] - sum;
            }
            for(i = j; i < n; i++)
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
}
