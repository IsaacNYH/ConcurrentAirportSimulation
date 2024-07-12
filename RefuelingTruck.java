package APAirlines;

import java.util.concurrent.Semaphore;

public class RefuelingTruck implements Runnable {
    private final Semaphore truck = new Semaphore(1, true);
    private Plane planeToRefuel;

    public void setPlaneToRefuel(Plane plane) {
        this.planeToRefuel = plane;
    }

    public void refuel(Plane plane) throws InterruptedException {
        truck.acquire();
        System.out.println("RefuelingTruck: Refueling Plane " + plane.getId());
        Thread.sleep(2000); // Time to refuel
        truck.release();
        System.out.println("RefuelingTruck: Plane " + plane.getId() + " refueled");
    }

    @Override
    public void run() {
        try {
            if (planeToRefuel != null) {
                refuel(planeToRefuel);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
