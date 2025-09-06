import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FloorDestinationTest {

  private TreeSet<FloorDestination> floorDestSorted;
  private List<FloorDestination> naturalOrder;

  @BeforeEach
  void setUp() {
    floorDestSorted = new TreeSet<>();

    var f1 = new FloorDestination(1);
    var f1dup = new FloorDestination(1, Direction.UP);
    var f3 = new FloorDestination(3);
    var f9 = new FloorDestination(9);

    floorDestSorted.add(f1);
    floorDestSorted.add(f1dup);
    floorDestSorted.add(f3);
    floorDestSorted.add(f9);

    naturalOrder = List.of(f1, f3, f9);
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
  void testPriority() {
    var firstPriority = new FloorDestination(10, Direction.REST, Integer.MAX_VALUE);
    floorDestSorted.add(firstPriority);

    assertEquals(firstPriority, floorDestSorted.pollFirst());
  }
}
