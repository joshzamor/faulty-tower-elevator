import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElevatorBankTest {

  @Test
  void testAddRequestBankOfOne() {
    var bank = new ElevatorBank(1, new FloorLimits(0, 1));
    assertEquals(1, bank.addRequest(new FloorDestination(1)));
  }

  @Test
  void testAddRequestBankOf2() {
    var bank = new ElevatorBank(1, new FloorLimits(0, 2));
    bank.addRequest(new FloorDestination(1, Direction.UP));
  }

  @Test
  void testRunBankOfOne() {
    var bank = new ElevatorBank(1, new FloorLimits(-10, 10));
    bank.addRequest(new FloorDestination(10, Direction.DOWN));

    assertEquals(1, bank.runBank().size());
  }

  @Test
  void testRunBankEven() {
    var bank = new ElevatorBank(2, new FloorLimits(-10, 10));
    bank.addRequest(new FloorDestination(10, Direction.DOWN));
    bank.addRequest(new FloorDestination(-10, Direction.UP));
    var results = bank.runBank();
    assertEquals(2, elevatorMoves(results));
  }

  @Test
  void testRunBankUneven() {
    var bank = new ElevatorBank(2, new FloorLimits(-10, 10));
    bank.addRequest(new FloorDestination(10, Direction.DOWN));
    bank.addRequest(new FloorDestination(5, Direction.DOWN));
    bank.addRequest(new FloorDestination(-10, Direction.UP));
    var results = bank.runBank();
    assertEquals(2, results.size()); // 2 elevators
    assertEquals(2, results.getFirst().size());
    assertEquals(1, results.getLast().size());
    assertEquals(3, elevatorMoves(results));
  }

  static int elevatorMoves(List<List<String>> runResults) {
    return runResults.stream().flatMap(List::stream).toList().size();
  }
}
