import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents either a hall call (at floor in direction), or a button press
 * (go to this floor inside elevator).
 */
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
    this(floor, direction, 0);
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
   * Determines if this FloorDestination has the given floor number.
   * @param floorNumber floor number to test against.
   * @return true if the floor number is the same, false otherwise.
   */
  public boolean hasFloorNumber(int floorNumber) {
    return this.floor == floorNumber;
  }

  /**
   * Determines if this and other FloorDestination have the same floor number.
   * @param other FloorDestination to test against.
   * @return true if same floor numbers, false otherwise.
   */
  public boolean hasFloorNumber(FloorDestination other) {
    return this.floor == other.floor;
  }

  /**
   * Determines if this FloorDestination is outside the given FloorLimits.
   * @param floorLimits Lowest and highest floors, inclusive.
   * @return true if outside limits, false otherwise.
   */
  public boolean isOutsideFloorLimit(FloorLimits floorLimits) {
    return this.floor < floorLimits.min() || this.floor > floorLimits.max();
  }

  /**
   * Determines if the given Direction is the "same" as our direction,
   * which is true when both Direction are equal, or either one is
   * {@link Direction#REST}
   * @param otherDirection the Direction to test against.
   * @return true if both in same direction, false otherwise.
   */
  public boolean hasSameDirection(Direction otherDirection) {
    if( !hasDirection() || otherDirection == Direction.REST ) return true;
    return direction.equals(otherDirection);
  }

  /**
   * If this FloorDestination has a Direction.
   * @return true if not {@link Direction#REST}, false otherwise.
   */
  public boolean hasDirection() {
    return Direction.REST != this.direction;
  }

  /**
   * Determines if the given FloorDirection is above and in the same direction
   * as this FloorDirection.  This Direction of this FloorDestination is
   * not considered, only the floor number of this FloorDestination and the given one,
   * as well as the Direction of the given FloorDestination, and the
   * given parameter Direction.
   *
   * @param other The other FloorDestination
   * @param travelDirection the given Direction of travel of this FloorDestination
   * @return true if the given FloorDestination is reachable by this FloorDestination
   * traveling in the given Direction.  False otherwise.
   */
  public boolean isAboveInDirection(FloorDestination other, Direction travelDirection) {
    if( null == other || null == travelDirection ) return false;
    if( this.floor == other.floor ) return true;
    if( this.floor < other.floor ) {
      return other.hasSameDirection(travelDirection);
    }

    return false;
  }

  /**
   * Determines if this has a priority other than the default.
   * @return true if priority > 0 (default).
   */
  public boolean hasPriority() {
    return priority > 0;
  }

  /**
   * The number of floors the given floor is away from our floor, as in
   * a direction on a number line.
   * @param other the other floor.
   * @return > 0 for floors "above" us, < 0 for floors below us.  Or 0.
   */
  public int floorsAway(FloorDestination other) {
    return other.floor - this.floor;
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

  public String floorAsString() {
    return floor + "";
  }
}