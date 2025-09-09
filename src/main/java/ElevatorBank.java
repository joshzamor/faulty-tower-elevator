import java.util.ArrayList;
import java.util.List;

/**
 * Basic elevator bank, allows for a number of elevators to be setup,
 * and for those elevators to accept requests and run the elevator bank.
 * <p>
 * This is an oversimplified example, where in practice for decentralized
 * elevators they'd run independantly, and requests would likely be consumed
 * via a message bus.
 */
public class ElevatorBank {
  public final List<Elevator> bank;

  /**
   * Create a bank of elevators.
   * @param numElevators number of elevators in bank.
   * @param floorLimits floor limits for each elevator in bank.
   */
  public ElevatorBank(int numElevators, FloorLimits floorLimits) {
    bank = new ArrayList<>(numElevators);
    for(int i = 0; i < numElevators; ++i) {
      var elev = new Elevator(i, floorLimits);
      bank.add(elev);
    }

    var immutableBank = List.copyOf(bank);
    bank.forEach(e -> e.setElevatorBank(immutableBank));
  }

  /**
   * Add a request to the bank, elevators will bid, and one or more
   * should accept the request.
   * @param request the requested floor.
   * @return number of elevators that accepted.  Will be 0 if outside floor
   * limits of bank.
   */
  public int addRequest(FloorDestination request) {
    System.out.println("----------");
    System.out.printf("Elevators bidding on: %s%n", request);
    System.out.println("----------");
    int accepted = 0;
    for( Elevator e : bank) {
      if( e.addRequest(request) ) {
        accepted++;
        System.out.println("elevator accepted request: " + e);
      }
    }

    System.out.printf("%d elevators accepted request: %s%n", accepted, request);
    return accepted;
  }

  /**
   * Runs the bank - i.e. each elevator will process its work until done.
   * @return a list of ordered floor visits per elevator.
   */
  public List<List<String>> runBank() {
    return bank.stream()
        .map(Elevator::run)
        .toList();
  }
}
