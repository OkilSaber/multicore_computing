import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.*;

public class MatmultD {
  private static Scanner sc = new Scanner(System.in);
  private static int nbThreads;
  private static int ans[][];
  private static int matrixA[][];
  private static int matrixB[][];
  private static int heightA;
  private static int widthA;
  private static int heightB;
  private static int widthB;
  private static int a = 0;
  private static int b = 0;
  private static int c = 0;
  private static Object lock = new Object();

  public static void main(String[] args) {
    if (args.length == 1) {
      nbThreads = Integer.valueOf(args[0]);
    } else {
      nbThreads = 1;
    }

    heightA = sc.nextInt();
    widthA = sc.nextInt();
    matrixA = readMatrix(heightA, widthA);
    heightB = sc.nextInt();
    widthB = sc.nextInt();
    matrixB = readMatrix(heightB, widthB);
    long startTime = System.currentTimeMillis();
    long endTime;
    if (heightA == 0) {
      endTime = System.currentTimeMillis();
      System.out.printf("[nbThreads]:%2d , [Time]:%4d ms\n", nbThreads, endTime - startTime);
      return;
    }
    ans = new int[0][0];
    if (widthA != heightB)
      return;
    ans = new int[heightA][widthB];
    ExecutorService es = Executors.newCachedThreadPool();
    for (int i = 0; i < nbThreads; i++) {
      es.execute(new MyThread(i));
    }
    es.shutdown();
    while (!es.isTerminated()) {
    }
    endTime = System.currentTimeMillis();

    // printMatrix(ans);
    System.out.printf("[nbThreads]:%2d , [Time]:%4d ms\n", nbThreads, endTime - startTime);
  }

  public static int[][] readMatrix(int rows, int cols) {
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

  public static int[] multMatrix() {
    synchronized (lock) {
      for (; a < heightA;) {
        for (; b < widthB;) {
          for (; c < widthA;) {
            c++;
            return new int[] { a, b, c - 1 };
          }
          c = 0;
          b++;
        }
        b=0;
        a++;
      }
    }
    return null;
  }

  public static class MyThread implements Runnable {
    private int id;

    public MyThread(int i) {
      id = i;
    }

    public void run() {
      long startTime = System.currentTimeMillis();
      int[] numbers;
      while ((numbers = multMatrix()) != null) {
        synchronized (lock) {
          ans[numbers[0]][numbers[1]] += matrixA[numbers[0]][numbers[2]] * matrixB[numbers[2]][numbers[1]];
        }
      }
      long endTime = System.currentTimeMillis();
      long timeDiff = endTime - startTime;
      System.out.println("Thread#" + id + " Execution Time: " + timeDiff + "ms");
    }
  }
}
