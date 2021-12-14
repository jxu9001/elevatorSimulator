import java.util.concurrent.Semaphore;

public class Main {
    // semaphore to allow people to enter the elevator
    public static Semaphore canEnter = new Semaphore(0,true);
    // semaphore to allow people to leave the elevator
    public static Semaphore canLeave = new Semaphore(0,true);
    // semaphore array to indicate if a person has reached their desired floor
    public static Semaphore[] desiredFloor = new Semaphore[49];
    // semaphore to indicate if the elevator is at capacity
    public static Semaphore atCapacity = new Semaphore(0,true);

    public static void main(String[] args) {
        // create and the start the elevator thread
        Thread elevatorThread = new Thread(new Elevator());
        elevatorThread.start();

        // create and start the person threads
        Thread[] personThread = new Thread[49];
        for (int i = 0; i < 49; i++) {
            personThread[i] = new Thread(new Person(i));
        }

        // join the person threads
        for (Thread thread : personThread) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // join the elevator thread
        try {
            elevatorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
