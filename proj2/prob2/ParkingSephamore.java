import java.util.concurrent.Semaphore;

class ParkingGarage {
  private int places;
  private Semaphore sem;

  public ParkingGarage(int places) {
    if (places < 0)
      places = 0;
    this.places = places;
    this.sem = new Semaphore(this.places);
  }

  public void enter() {
    try {
      this.sem.acquire();
      this.places--;
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void leave() {
    this.places++;
    this.sem.release();
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

  private void justEntered() {
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
      }
      tryingEnter();
      parkingGarage.enter();
      justEntered();
      try {
        sleep((int) (Math.random() * 20000));
      } catch (InterruptedException e) {
      }
      aboutToLeave();
      parkingGarage.leave();
      Left();

    }
  }
}

public class ParkingSephamore {
  public static void main(String[] args) {
    ParkingGarage parkingGarage = new ParkingGarage(7);
    for (int i = 1; i <= 10; i++) {
      new Car("Car " + i, parkingGarage);
    }
  }
}
