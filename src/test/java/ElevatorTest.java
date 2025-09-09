import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ElevatorTest {

  private Elevator elevator;
  private List<Integer> floorOrder;

  @BeforeEach
  void setup() {
    elevator = new Elevator(1, new FloorLimits(Integer.MIN_VALUE, Integer.MAX_VALUE));
    elevator.addRequest(new FloorDestination(1, Direction.UP));
    elevator.addRequest(new FloorDestination(3));
    elevator.addRequest(new FloorDestination(5, Direction.DOWN));

    floorOrder = List.of(1, 3, 5);
  }

  @Test
  void testAddRequestDenied() {
    var smallElevator = new Elevator(1, new FloorLimits(0, 1));
    assertFalse(smallElevator.addRequest(new FloorDestination(-1)));
  }

  @Test
  void testNextFloorEmpty() {
    var emptyElev = new Elevator(1, new FloorLimits(0, 1));

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

  @Test
  void testCalcBidCostAtRest() {
    var floor = new FloorDestination(1);
    var toFloor = new FloorDestination(10);

    var lower = Elevator.calcBidCostRequest(
        floor,
        new ElevatorState(1, toFloor, Direction.REST, 0, 1, 10));
    var higher = Elevator.calcBidCostRequest(
        floor,
        new ElevatorState(2, toFloor, Direction.UP, 0, 1, 10));


    assertTrue(lower < higher);
  }

  @Test
  void testCalcBidCostMoreWork() {
    var floor = new FloorDestination(1);
    var toFloor = new FloorDestination(10);

    var lower = Elevator.calcBidCostRequest(
        floor,
        new ElevatorState(1, toFloor, Direction.UP, 0, 1, 10));
    var higher = Elevator.calcBidCostRequest(
        floor,
        new ElevatorState(2, toFloor, Direction.UP, 0, 2, 10));

    assertTrue(lower < higher);
  }

  @Test
  void testCalcBidCostDirection() {
    var floor = new FloorDestination(1);
    var toFloorInDirection = new FloorDestination(10, Direction.UP);
    var toFloorOtherDirection = new FloorDestination(-10, Direction.DOWN);

    var lower = Elevator.calcBidCostRequest(
        floor,
        new ElevatorState(1, toFloorInDirection, Direction.UP, 0, 1, 10));
    var higher = Elevator.calcBidCostRequest(
        floor,
        new ElevatorState(2, toFloorOtherDirection, Direction.UP, 0, 2, 10));

    assertTrue(lower < higher);
  }

  @Test
  void testCalcBidCostHasPriority() {
    var floor = new FloorDestination(1);

    var priorityCost = Elevator.calcBidCostRequest(
        floor,
        new ElevatorState(1, floor, Direction.UP, 1, 1, 1));
    var regCost = Elevator.calcBidCostRequest(
        floor,
        new ElevatorState(2, floor, Direction.UP, 0, 10, 1));


    assertTrue(priorityCost > regCost );
  }

  @Test
  void testCalcBidCostPriorityFaceoff() {
    var floor = new FloorDestination(1);

    var morePriority = Elevator.calcBidCostRequest(
        floor,
        new ElevatorState(1, floor, Direction.UP, 2, 1, 1));
    var lessPriority = Elevator.calcBidCostRequest(
        floor,
        new ElevatorState(2, floor, Direction.UP, 1, 10, 1));


    assertTrue(morePriority > lessPriority );
  }

  @Test
  void testCalcBidCostAtFloor() {
    var floor = new FloorDestination(1);

    var first = Elevator.calcBidCostRequest(
        floor,
        new ElevatorState(1, floor, Direction.UP, 0, 1, 1));
    var second = Elevator.calcBidCostRequest(
        floor,
        new ElevatorState(2, floor, Direction.UP, 0, 10, 10));


    assertTrue( 1 > Math.abs(first - second) );
  }
}
