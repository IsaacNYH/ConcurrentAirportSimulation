package APAirlines;
import java.util.ArrayList;
import java.util.List;

public class Statistics {
    private final List<Integer> waitingTimes = new ArrayList<>();
    private int totalPlanesServed = 0;
    private int totalPassengers = 0;

    public synchronized void addWaitingTime(int time) {
        waitingTimes.add(time);
    }

    public synchronized void incrementPlanesServed() {
        totalPlanesServed++;
    }

    public synchronized void addPassengers(int passengers) {
        totalPassengers += passengers;
    }

    public void printStatistics() {
        int maxWait = waitingTimes.stream().max(Integer::compareTo).orElse(0);
        int minWait = waitingTimes.stream().min(Integer::compareTo).orElse(0);
        double avgWait = waitingTimes.stream().mapToInt(Integer::intValue).average().orElse(0);

        System.out.println("Statistics:");
        System.out.println("Max waiting time: " + maxWait);
        System.out.println("Min waiting time: " + minWait);
        System.out.println("Average waiting time: " + avgWait);
        System.out.println("Total planes served: " + totalPlanesServed);
        System.out.println("Total passengers boarded: " + totalPassengers);
    }
}