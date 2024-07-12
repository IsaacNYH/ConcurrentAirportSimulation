package APAirlines;

public class Gate {
    private final int id;
    private boolean occupied;

    public Gate(int id) {
        this.id = id;
        this.occupied = false;
    }

    public synchronized int getId() {
        return id;
    }

    public synchronized boolean isOccupied() {
        return occupied;
    }

    public synchronized void occupy() {
        System.out.println("Gate: Gate " + id + " is now occupied");
        occupied = true;
    }

    public synchronized void release() {
        System.out.println("Gate: Gate " + id + " is released");
        occupied = false;
        notifyAll(); // Notify planes waiting for the gate
    }
}
