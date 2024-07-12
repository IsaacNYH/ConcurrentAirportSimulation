package APAirlines;

import java.util.Random;

public class Plane implements Runnable {
    private final int id;
    private final ATC atc;
    private final RefuelingTruck refuelTruck;
    private final boolean emergencyLanding;
    private final Statistics statistics;
    private final int passengers;
    private long arrivalTime;

    public Plane(int id, ATC atc, RefuelingTruck refuelTruck, boolean emergencyLanding, Statistics statistics) {
        this.id = id;
        this.atc = atc;
        this.refuelTruck = refuelTruck;
        this.emergencyLanding = emergencyLanding;
        this.statistics = statistics;
        this.passengers = new Random().nextInt(21) + 30; 
        this.arrivalTime = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public boolean isEmergencyLanding() {
        return emergencyLanding;
    }

    public int getPassengers() {
        return passengers;
    }

    @Override
    public void run() {
        try {
            atc.requestLanding(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void land() throws InterruptedException {
        Gate gate = null;
        while (gate == null) {
            synchronized (atc) {
                gate = atc.assignGate();
                if (gate == null) {
                    System.out.println(Thread.currentThread().getName() + ": No gate available for Plane " + id + ", waiting...");
                    atc.wait(); // Wait until notified that a gate is available
                }
            }
        }

        atc.assignRunway(this);
        System.out.println(Thread.currentThread().getName() + ": Plane " + id + " is landing.");
        Thread.sleep(2000); // Time to land
        atc.releaseRunway();

        System.out.println(Thread.currentThread().getName() + ": Plane " + id + " is coasting to gate " + gate.getId());
        Thread.sleep(1000); // Time to coast to gate

        System.out.println(Thread.currentThread().getName() + ": Plane " + id + " is at gate " + gate.getId());

        embarkDisembark();
        refillAndClean();
        refuel();

        System.out.println(Thread.currentThread().getName() + ": Plane " + id + " is ready for takeoff");
        atc.assignRunway(this);
        Thread.sleep(2000); // Time to takeoff
        atc.releaseRunway();

        synchronized (atc) {
            atc.releaseGate(gate);
            atc.notifyAll(); // Notify ATC about gate availability
        }

        // Record statistics
        long waitingTime = System.currentTimeMillis() - arrivalTime;
        statistics.addWaitingTime((int) waitingTime);
        statistics.incrementPlanesServed();
        statistics.addPassengers(passengers);

        // Notify ATC to check for pending planes
        atc.checkPendingLandings();

        // Commenting out the line that prints the queue
        // synchronized (atc) {
        //    atc.printQueue();
        // }
    }

    private void embarkDisembark() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + ": Plane " + id + " is disembarking " + passengers + " passengers.");
        Thread.sleep(2000); // Time to disembark passengers

        int boardingPassengers = new Random().nextInt(21) + 30; // Random passengers between 0 and 50
        System.out.println(Thread.currentThread().getName() + ": Plane " + id + " is embarking " + boardingPassengers + " passengers.");
        Thread.sleep(2000); // Time to embark passengers
    }

    private void refillAndClean() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + ": Plane " + id + " is refilling supplies and cleaning.");
        Thread.sleep(2000); // Time to refill and clean
    }

    private void refuel() throws InterruptedException {
        refuelTruck.refuel(this);
    }
}