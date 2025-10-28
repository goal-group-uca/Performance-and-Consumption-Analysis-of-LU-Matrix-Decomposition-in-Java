/*
 * Based on algorithm: https://github.com/JaiJaveria/Parallel_Crout_Decomposition/
 * (Transcripted to Java)
 */

public class CroutSequentialVersion
{
    public static void decomposition(double[][] A, double[][] L, double[][] U, int n)
    {
        int i, j, k;
        double sum = 0;
        for(i = 0; i < n; i++)
        {
            U[i][i] = 1;
        }
        for(j = 0; j < n; j++)
        {
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
