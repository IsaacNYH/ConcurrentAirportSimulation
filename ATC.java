package APAirlines;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;

public class ATC implements Runnable {
    private final Semaphore runway = new Semaphore(1, true);
    private final ConcurrentLinkedDeque<Plane> landingQueue;
    private final Gate[] gates;

    public ATC(int numGates) {
        gates = new Gate[numGates];
        for (int i = 0; i < numGates; i++) {
            gates[i] = new Gate(i + 1);
        }

        landingQueue = new ConcurrentLinkedDeque<>();
    }

    public void requestLanding(Plane plane) throws InterruptedException {
        synchronized (landingQueue) {
            if (plane.isEmergencyLanding()) {
                landingQueue.addFirst(plane);
            } else {
                landingQueue.addLast(plane);
            }
            System.out.println("ATC: Landing requested by Plane " + plane.getId() + (plane.isEmergencyLanding() ? " (Emergency)" : ""));
            printQueue();
            landingQueue.notifyAll();
        }
    }

    public void assignRunway(Plane plane) throws InterruptedException {
        runway.acquire();
        System.out.println("ATC: Runway assigned to Plane " + plane.getId());
    }

    public void releaseRunway() {
        runway.release();
        System.out.println("ATC: Runway released");
    }

    public synchronized Gate assignGate() {
        for (Gate gate : gates) {
            if (!gate.isOccupied()) {
                gate.occupy();
                return gate;
            }
        }
        return null;
    }

    public synchronized void releaseGate(Gate gate) {
        gate.release();
        notifyAll(); // Notify planes waiting for a gate
    }

    public void manageLandingQueue() throws InterruptedException {
        while (true) {
            synchronized (landingQueue) {
                while (landingQueue.isEmpty() || getAvailableGate() == null) {
                    landingQueue.wait();
                }
                Plane plane = landingQueue.poll();
                // Commenting out the line that prints the queue
                // printQueue();
                if (plane != null) {
                    new Thread(() -> {
                        try {
                            plane.land();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        }
    }

    private Gate getAvailableGate() {
        for (Gate gate : gates) {
            if (!gate.isOccupied()) {
                return gate;
            }
        }
        return null;
    }

    public synchronized void checkPendingLandings() {
        synchronized (landingQueue) {
            while (!landingQueue.isEmpty() && getAvailableGate() != null) {
                Plane plane = landingQueue.poll();
                // Commenting out the line that prints the queue
                // printQueue();
                if (plane != null) {
                    new Thread(() -> {
                        try {
                            plane.land();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        }
    }

    public synchronized void printQueue() {
        if (!landingQueue.isEmpty()) {
            System.out.println("Current Landing Queue:");
            for (Plane plane : landingQueue) {
                System.out.println("Plane " + plane.getId() + (plane.isEmergencyLanding() ? " (Emergency)" : ""));
            }
        }
    }

    @Override
    public void run() {
        try {
            manageLandingQueue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}