import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatmultD {
  private static Scanner sc = new Scanner(System.in);
  private static int nbThreads;
  private static int nbThreadsWorking;
  private static float tmpLinesPerThread;
  private static double linesPerThread;
  private static Matrix ans;
  private static Matrix matrixA;
  private static Matrix matrixB;

  public static void main(String[] args) {
    // Check args
    if (args.length == 1) {
      nbThreads = Integer.valueOf(args[0]);
    } else {
      nbThreads = 1;
    }

    // Creates matrices
    matrixA = new Matrix();
    matrixB = new Matrix();

    // Start program
    long startTime = System.currentTimeMillis();

    // Check if matrices are correct
    if (matrixA.height == 0) {
      long endTime = System.currentTimeMillis();
      System.out.printf("[nbThreads]:%2d , [Time]:%4d ms\n", nbThreads, endTime - startTime);
      return;
    }
    if (matrixA.width != matrixB.height)
      return;

    // Init final matrix
    ans = new Matrix(matrixB.width, matrixA.height);

    tmpLinesPerThread = matrixA.height / nbThreads;

    if (tmpLinesPerThread < 1) {
      linesPerThread = 1;
      nbThreadsWorking = matrixA.height;
    } else if (matrixA.height % nbThreads != 0) {
      linesPerThread = Math.floor(tmpLinesPerThread);
      nbThreadsWorking = nbThreads;
    } else {
      linesPerThread = tmpLinesPerThread;
      nbThreadsWorking = nbThreads;
    }
    // Start Thread pool
    ExecutorService es = Executors.newCachedThreadPool();
    int rest = matrixA.width % nbThreadsWorking;
    // Execute threads
    for (int i = 0; i < nbThreads; i++) {
      if (i < nbThreadsWorking) {
        if (i + 1 == nbThreadsWorking && rest != 0) {
          es.execute(new MyThread(i, (int) linesPerThread, rest));
        } else {
          es.execute(new MyThread(i, (int) linesPerThread, 0));
        }
      } else {
        es.execute(new MyThread(i));
      }
    }

    // No more threads are expected to run
    es.shutdown();

    // Wait for all threads to finish
    while (!es.isTerminated()) {
    }

    // Stop chrono
    long endTime = System.currentTimeMillis();

    // Print
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
    private int nbLines = 0;
    private boolean runnable = false;
    private int more = 0;

    public MyThread(int id, int lines, int more) {
      this.id = id;
      nbLines = lines;
      this.runnable = true;
      this.more = more;
    }

    public MyThread(int id, int lines) {
      this.id = id;
      nbLines = lines;
      this.runnable = true;
    }

    public MyThread(int id) {
      this.id = id;
      this.runnable = false;
    }

    public void run() {
      long startTime = System.currentTimeMillis();
      if (this.runnable) {
        int linesDone = 0;

        for (int i = id; linesDone < (nbLines + more);) {
          for (int j = 0; j < matrixB.width; j++) {
            for (int x = 0; x < matrixB.width; x++) {
              ans.matrix[i][j] += matrixA.getNumber(i, x) * matrixB.getNumber(x, j);
            }
          }
          linesDone++;
          if (linesDone < nbLines) {
            i += nbThreadsWorking;
          } else {
            i++;
          }
        }
      }
      long endTime = System.currentTimeMillis();
      long timeDiff = endTime - startTime;
      System.out.println("Thread#" + id + " Execution Time: " + timeDiff + "ms");
    }
  }
}
