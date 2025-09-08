import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ElevatorTest {

  private Elevator elevator;
  private List<Integer> floorOrder;

  @BeforeEach
  void setup() {
    elevator = new Elevator(new FloorLimits(Integer.MIN_VALUE, Integer.MAX_VALUE));
    elevator.addRequest(new FloorDestination(1, Direction.UP));
    elevator.addRequest(new FloorDestination(3));
    elevator.addRequest(new FloorDestination(5, Direction.DOWN));

    floorOrder = List.of(1, 3, 5);
  }

  @Test
  void testAddRequestDenied() {
    var smallElevator = new Elevator(new FloorLimits(0, 1));
    assertFalse(smallElevator.addRequest(new FloorDestination(-1)));
  }

  @Test
  void testNextFloorEmpty() {
    var emptyElev = new Elevator(new FloorLimits(0, 1));

    assertTrue(emptyElev.nextFloor().isEmpty());
  }

  @Test
  void testNextFloorDone() {
    while(elevator.nextFloor().isPresent()) { /* drain */ }

    assertTrue(elevator.nextFloor().isEmpty());
  }

  @Test
  void testNextFloorPriorityHighest() {
    elevator.addRequest(new FloorDestination(10, Direction.UP, Integer.MAX_VALUE));

    var nextFloor = elevator.nextFloor();
    assertTrue( nextFloor.isPresent() );
    assertTrue( nextFloor.get().hasFloorNumber(10) );
  }

  @Test
  void testNextFloorInOrder() {
    for (int floorOrderNumber : floorOrder) {
      var optNextFloor = elevator.nextFloor();
      assertTrue(optNextFloor.isPresent());
      assertTrue(optNextFloor.get().hasFloorNumber(floorOrderNumber));
    }
  }

  @Test
  void testRunInOrder() {
    var floorRun = elevator.run();
    assertEquals(floorOrder.size(), floorRun.size());
    for( int i = 0; i < floorOrder.size(); ++i ) {
      assertEquals(floorOrder.get(i) + "", floorRun.get(i));
    }
  }

}
