import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Elevator implements Runnable {
    // arraylist that contains the people currently in the elevator
    public static ArrayList<Person> elevator = new ArrayList<>();
    // the floor that the elevator is currently on
    public static int currentFloor;
    // number of people currently on the elevator
    public static int numOnboard;
    // number of people who haven't reached their desired floors
    public static int numLeft = 49;

    // initializes an empty elevator at floor 1
    public Elevator() {
        elevator.clear();
        numOnboard = 0;
        currentFloor = 1;
    }

    // initialize the semaphore array indicating if a person has reached their desired floor
    public static void initDesiredFloorArray() {
        for (int i = 0; i < 49; i++) {
            Main.desiredFloor[i] = new Semaphore(0,true);
        }
    }

    // since we have an arraylist of people on the elevator
    // the next floor that the elevator stops at is the lowest of their requested floors
    public int getNextFloor() {
        if (elevator.isEmpty()) {
            return 1;
        }
        int nextFloor = Integer.MAX_VALUE;
        for (Person person : elevator) {
            nextFloor = Math.min(person.requestedFloor, nextFloor);
        }
        return nextFloor;
    }

    // runs the elevator until the simulation ends
    public void runElevator() throws InterruptedException {
        int nextFloor = getNextFloor();
        // while there are still people onboard, move the elevator up
        while (!elevator.isEmpty()) {
            currentFloor = nextFloor;
            System.out.println("Elevator door opens at floor " + currentFloor);
            // let all the people who want to get off at the current floor get off
            while (nextFloor == getNextFloor()) {
                for (int i = 0; i < numOnboard; i++)
                    if (elevator.get(i).requestedFloor == currentFloor) {
                        Main.desiredFloor[elevator.get(i).id].release();
                        Main.canLeave.acquire();
                    }
            }
            // close the elevator door and go up to the next floor
            System.out.println("Elevator door closes");
            nextFloor = getNextFloor();
        }
        // elevator is empty, so go back down floor 1
        currentFloor = 1;
    }

    // end the simulation
    private void endSimulation() {
        if (numLeft == 0) {
            System.out.println("Simulation Done");
            System.exit(0);
        }
    }

    @Override
    public void run() {
        initDesiredFloorArray();
        try {
            while (true) {
                System.out.println("Elevator door opens at floor " + currentFloor);
                // signal 7 people to get on the elevator
                Main.canEnter.release(7);
                // wait until elevator is full
                Main.atCapacity.acquire();
                System.out.println("Elevator door closes");
                // run the elevator until all 49 people have reached their desired floors
                runElevator();
                // once 49 people have arrived at their desired floors, end the simulation
                endSimulation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
