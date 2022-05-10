import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class ParkingGarage {
  private int places;
  private BlockingQueue<Car> queue;

  public ParkingGarage(int places) {
    if (places < 0)
      places = 0;
    this.places = places;
    this.queue = new ArrayBlockingQueue<Car>(this.places);
  }

  public void enter(Car car) {
    while (true) {
      if (queue.offer(car)) {
        return;
      }
    }
  }

  public void leave(Car car) {
    queue.remove(car);
  }
}

class Car extends Thread {
  private ParkingGarage parkingGarage;

  public Car(String name, ParkingGarage p) {
    super(name);
    this.parkingGarage = p;
    start();
  }

  private void tryingEnter() {
    System.out.println(getName() + ": trying to enter");
  }

  public void justEntered() {
    System.out.println(getName() + ": just entered");

  }

  private void aboutToLeave() {
    System.out.println(getName() + ":                                     about to leave");
  }

  private void Left() {
    System.out.println(getName() + ":                                     have been left");
  }

  public void run() {
    while (true) {
      try {
        sleep((int) (Math.random() * 10000));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      tryingEnter();
      parkingGarage.enter(this);
      justEntered();
      try {
        sleep((int) (Math.random() * 20000));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      aboutToLeave();
      parkingGarage.leave(this);
      Left();
    }
  }
}

public class ParkingGarageOperation {
  public static void main(String[] args) {
    ParkingGarage parkingGarage = new ParkingGarage(7);
    for (int i = 1; i <= 10; i++) {
      new Car("Car " + i, parkingGarage);
    }
  }
}
