import java.util.Random;

public class Person implements Runnable {
    public int id;
    public int requestedFloor;
    public Thread person;

    // initializes a person with a randomly selected desired floor between 2 and 10
    public Person(int id) {
        Random rand = new Random();
        this.id = id;
        requestedFloor = rand.nextInt(9) + 2;
        person = new Thread(this);
        person.start();
    }

    // person gets on the elevator
    public void enterElevator(Person person) {
        Elevator.elevator.add(person);
        System.out.println("Person " + person.id + " enters elevator to go to floor " + person.requestedFloor);
        Elevator.numOnboard++;
    }

    // person gets off the elevator
    public void leaveElevator(Person person) {
        Elevator.elevator.remove(person);
        System.out.println("Person " + person.id + " leaves elevator");
        Elevator.numOnboard--;
        Elevator.numLeft--;
    }

    // checks if there are 7 people onboard the elevator
    public void isElevatorFull() {
        if (Elevator.numOnboard == 7) {
            Main.atCapacity.release();
        }
    }

    @Override
    public void run() {
        try {
            // wait for 7 people to get on the elevator
            Main.canEnter.acquire();
            // enter the elevator
            enterElevator(this);
            // if the elevator is full then signal it
            isElevatorFull();
            // wait until person reaches their desired floor
            Main.desiredFloor[this.id].acquire();
            // leave elevator
            leaveElevator(this);
            // signal that 1+ person has left the elevator
            Main.canLeave.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
