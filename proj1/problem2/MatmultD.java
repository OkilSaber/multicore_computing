import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatmultD {
  private static Scanner sc = new Scanner(System.in);
  private static int nbThreads;
  private static Matrix ans;
  private static Matrix matrixA;
  private static Matrix matrixB;
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
    matrixA = new Matrix();
    matrixB = new Matrix();
    long startTime = System.currentTimeMillis();
    long endTime;
    if (matrixA.height == 0) {
      endTime = System.currentTimeMillis();
      System.out.printf("[nbThreads]:%2d , [Time]:%4d ms\n", nbThreads, endTime - startTime);
      return;
    }
    if (matrixA.width != matrixB.height)
      return;
    ans = new Matrix(matrixB.width, matrixA.height);
    ExecutorService es = Executors.newCachedThreadPool();
    for (int i = 0; i < nbThreads; i++) {
      es.execute(new MyThread(i));
    }
    es.shutdown();
    while (!es.isTerminated()) {
    }
    endTime = System.currentTimeMillis();

    printMatrix(ans.matrix);
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
      for (; a < matrixA.height;) {
        for (; b < matrixB.width;) {
          for (; c < matrixA.width;) {
            c++;
            return new int[] { a, b, c - 1 };
          }
          c = 0;
          b++;
        }
        b = 0;
        a++;
      }
    }
    return null;
  }

  public static class Matrix {
    public int height;
    public int width;
    public int[][] matrix;

    public Matrix() {
      height = sc.nextInt();
      width = sc.nextInt();
      matrix = readMatrix(height, width);
    }

    public Matrix(int width, int height) {
      this.height = height;
      this.width = width;
      this.matrix = new int[height][width];
    }

    public int getNumber(int y, int x) {
      return matrix[y][x];
    }
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
          ans.matrix[numbers[0]][numbers[1]] += matrixA.getNumber(numbers[0], numbers[2])
              * matrixB.getNumber(numbers[2], numbers[1]);
        }
      }
      long endTime = System.currentTimeMillis();
      long timeDiff = endTime - startTime;
      System.out.println("Thread#" + id + " Execution Time: " + timeDiff + "ms");
    }
  }
}
