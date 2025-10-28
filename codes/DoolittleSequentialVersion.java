/*
 * Base algorithm: https://www.geeksforgeeks.org/doolittle-algorithm-lu-decomposition/
 */

public class DoolittleSequentialVersion
{
    public static void decomposition(double[][] A, double[][] L, double[][] U, int n)
    {
        int i, j, k;
        double sum = 0;
        for(i = 0; i < n; i++)
        {
            L[i][i] = 1;
        }
        for(i = 0; i < n; i++)
        {
            for(j = i; j < n; j++)
            {
                sum = 0;
                for (k = 0; k < i; k++)
                {
                    sum = sum + L[i][k] * U[k][j];
                }
                U[i][j] = A[i][j] - sum;
            }
            for(j = i; j < n; j++)
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
}
