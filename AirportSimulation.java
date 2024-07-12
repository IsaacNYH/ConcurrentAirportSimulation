package APAirlines;

import java.util.Random;
public class AirportSimulation {
    public static void main(String[] args) {
        ATC atc = new ATC(3);
        RefuelingTruck refuelTruck = new RefuelingTruck();
        Statistics statistics = new Statistics();

        // Start ATC thread to manage landing queue
        new Thread(atc).start();

        // Creating 6 planes
        for (int i = 0; i < 6; i++) {
            int id = i + 1;
            boolean emergencyLanding = (i == 5); // 6th plane is emergency
            Plane plane = new Plane(id, atc, refuelTruck, emergencyLanding, statistics);
            new Thread(plane).start();

            // Random arrival interval 0, 1, or 2 seconds
            try {
                Thread.sleep(new Random().nextInt(2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Allow some time for simulation to complete
        try {
            Thread.sleep(45000); // 45 seconds for all operations to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Print statistics
        statistics.printStatistics();
    }
}