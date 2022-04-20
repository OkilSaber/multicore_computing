import java.util.*;
import java.lang.*;

public class MatmultD {
  private static Scanner sc = new Scanner(System.in);

  public static void main(String[] args) {
    int thread_no = 0;
    if (args.length == 1) {
      thread_no = Integer.valueOf(args[0]);
    } else {
      thread_no = 1;
    }

    int a[][] = readMatrix();
    int b[][] = readMatrix();

    long startTime = System.currentTimeMillis();
    int[][] c = multMatrix(a, b);
    long endTime = System.currentTimeMillis();

    printMatrix(a);
    printMatrix(b);
    printMatrix(c);

    System.out.printf("[thread_no]:%2d , [Time]:%4d ms\n", thread_no, endTime - startTime);
  }

  public static int[][] readMatrix() {
    int rows = sc.nextInt();
    int cols = sc.nextInt();
    int[][] result = new int[rows][cols];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        result[i][j] = sc.nextInt();
      }
    }
    return result;
  }

  public static void printMatrix(int[][] mat) {
    System.out.println("Matrix[" + mat.length + "][" + mat[0].length + "]");
    int rows = mat.length;
    int columns = mat[0].length;
    int sum = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        System.out.printf("%4d ", mat[i][j]);
        sum += mat[i][j];
      }
      System.out.println();
    }
    System.out.println();
    System.out.println("Matrix Sum = " + sum + "\n");
  }

  public static int[][] multMatrix(int a[][], int b[][]) {// a[m][n], b[n][p]
    if (a.length == 0)
      return new int[0][0];
    if (a[0].length != b.length)
      return null;

    int n = a[0].length;
    int m = a.length;
    int p = b[0].length;
    int ans[][] = new int[m][p];

    for (int i = 0; i < m; i++) {
      for (int j = 0; j < p; j++) {
        for (int k = 0; k < n; k++) {
          ans[i][j] += a[i][k] * b[k][j];
        }
      }
    }
    return ans;
  }

  public static class MyThread implements Runnable {
    public MyThread() {
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("Thread#" + id + " Execution Time: " + timeDiff + "ms");
    }
}
}
