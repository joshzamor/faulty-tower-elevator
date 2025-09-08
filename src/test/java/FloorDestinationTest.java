import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class FloorDestinationTest {

  private FloorDestination curFloor;
  private TreeSet<FloorDestination> floorDestSorted;
  private List<FloorDestination> naturalOrder;

  @BeforeEach
  void setUp() {
    floorDestSorted = new TreeSet<>();

    var f1 = new FloorDestination(1);
    var f1dup = new FloorDestination(1, Direction.UP);
    var f3 = new FloorDestination(3);
    var f9 = new FloorDestination(9);

    curFloor = f3;

    floorDestSorted.add(f1);
    floorDestSorted.add(f1dup);
    floorDestSorted.add(f3);
    floorDestSorted.add(f9);

    naturalOrder = List.of(f1, f3, f9);
  }

  @Test
  void testIsOutsideFloorLimits() {
    var inside = new FloorDestination(0);
    var outside = new FloorDestination(2);
    var floorLimits = new FloorLimits(0,1);

    assertFalse(inside.isOutsideFloorLimit(floorLimits));
    assertTrue(outside.isOutsideFloorLimit(floorLimits));
  }

  @Test
  void testUp() {
    for(int i = 0; i < floorDestSorted.size(); ++i) {
      var floor = floorDestSorted.pollFirst();
      assertEquals(naturalOrder.get(i), floor);
    }
  }

  @Test
  void testDown() {
    for(int i = floorDestSorted.size() - 1; i >= 0; --i) {
      var floor = floorDestSorted.pollLast();
      assertEquals(naturalOrder.get(i), floor);
    }
  }

  @Test
  void testHasPriorityDefault() {
    var defaultPriority = new FloorDestination(1);

    assertFalse(defaultPriority.hasPriority());
  }

  @Test
  void testPrioritySort() {
    var firstPriority = new FloorDestination(10, Direction.REST, Integer.MAX_VALUE);
    floorDestSorted.add(firstPriority);

    assertEquals(firstPriority, floorDestSorted.pollFirst());
  }

  @Test
  void testIsAboveInDirectionAtFloor() {
    var buttonPressAt = new FloorDestination(3);

    assertTrue(curFloor.isAboveInDirection(buttonPressAt, Direction.UP));
    assertTrue(curFloor.isAboveInDirection(buttonPressAt, Direction.REST));
    assertTrue(curFloor.isAboveInDirection(buttonPressAt, Direction.DOWN));
  }

  @Test
  void testIsAboveInDirectionFloorCallsBehind() {
    var behindUp = new FloorDestination(2, Direction.UP);
    var behindDown = new FloorDestination(2, Direction.DOWN);
    var aheadDown = new FloorDestination(4, Direction.DOWN);

    assertFalse(curFloor.isAboveInDirection(behindUp, Direction.UP));
    assertFalse(curFloor.isAboveInDirection(behindDown, Direction.UP));
    assertFalse(curFloor.isAboveInDirection(aheadDown, Direction.UP));
  }

  @Test
  void testIsAboveInDirectionFloorCallAhead() {
    var aheadUp = new FloorDestination(4, Direction.UP);
    assertTrue(curFloor.isAboveInDirection(aheadUp, Direction.UP));
  }

  @Test
  void testIsAboveInDirectionButtonPressAhead() {
    var buttonPressAhead = new FloorDestination(4);
    assertTrue(curFloor.isAboveInDirection(buttonPressAhead, Direction.UP));
  }

  @Test
  void testIsAboveInDirectionButtonPressBehind() {
    var buttonPressBelow = new FloorDestination(2);
    assertFalse(curFloor.isAboveInDirection(buttonPressBelow, Direction.UP));
  }
}
