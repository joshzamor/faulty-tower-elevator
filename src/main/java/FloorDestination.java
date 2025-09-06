import java.time.LocalDateTime;
import java.util.Objects;

public class FloorDestination implements Comparable<FloorDestination> {

  private final int floor;
  private final LocalDateTime requestTime;
  private final Direction direction;
  private final int priority;

  /**
   * Create FloorDestination, request time will always be {@link LocalDateTime#now()}.
   *
   * @param floor floor number, natural Int order
   * @param direction direction of call
   * @param priority priority, bigger int is more priority, see {@link #hasPriority()}
   */
  public FloorDestination(int floor, Direction direction, int priority) {
    this.floor = floor;
    this.requestTime = LocalDateTime.now();
    this.direction = direction;
    this.priority = Math.abs(priority);
  }

  /**
   * Create FloorDestination w/ default priority, see {@link #hasPriority()}.
   * @param floor floor number, natural Int order
   * @param direction direction of call
   */
  public FloorDestination(int floor, Direction direction) {
    this(floor, direction, 1);
  }

  /**
   * Create FloorDestination with default direction {@link Direction#REST}
   * and priority, see {@link #hasPriority()}.
   * @param floor floor number, natural Int order
   */
  public FloorDestination(int floor) {
    this(floor, Direction.REST);
  }

  /**
   * get Direction this floor call is for, or {@link Direction#REST} by default.
   * @return direction the call is for.
   */
  public Direction getDirection() {
    return this.direction;
  }

  /**
   * Determines if this has a priority other than the default.
   * @return true if priority > 0 (default).
   */
  public boolean hasPriority() {
    return priority > 0;
  }

  /**
   * Compares utilizing first priority (reverse natural int order), and then
   * floor (natural Int order).
   * <p>
   * When priority is absent, floor 0 is before floor 1, etc.
   * With priority, the largest priority comes first.
   *
   * @param other the object to be compared.
   * @return negative, 0, or positive integer if this FloorDestination is before
   *   equal to, or after the other.
   */
  @Override
  public int compareTo(FloorDestination other) {
    if( null == other ) throw new NullPointerException("Compare to Null not allowed");
    int byPriority = Integer.compare(other.priority, this.priority);

    return 0 != byPriority ? byPriority : Integer.compare(this.floor, other.floor);
  }

  @Override
  public boolean equals(Object other) {
    if( this == other ) return true;
    if( !(other instanceof FloorDestination otherAs) ) return false;
    return 0 == compareTo(otherAs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(priority, floor);
  }

  @Override
  public String toString() {
    return String.format("Floor %d, direction %s, priority %d, requested at %s",
        floor,
        direction.toString(),
        priority,
        requestTime.toString());
  }
}