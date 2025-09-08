import java.util.*;

public class Elevator {

  private final FloorLimits floorLimit;
  private FloorDestination currentFloor;
  private Direction curDir;
  protected final TreeSet<FloorDestination> up;
  protected final TreeSet<FloorDestination> down;
  protected final PriorityQueue<FloorDestination> priorityQueue;

  public Elevator(FloorLimits floorLimits) {
    this.floorLimit = floorLimits;
    this.currentFloor = new FloorDestination(0);
    this.curDir = Direction.REST;
    this.up = new TreeSet<>();
    this.down = new TreeSet<>();
    this.priorityQueue = new PriorityQueue<>();
  }

  /**
   * Add floor request.
   * @param floor floor request to add.
   * @return true if floor request added, false otherwise.
   */
  public boolean addRequest(FloorDestination floor) {
    if( floor.isOutsideFloorLimit(floorLimit) ) return false;
    if (floor.hasPriority()) {
      priorityQueue.add(floor);
      return true;
    }

    if( currentFloor.isAboveInDirection(floor, curDir) ) up.add(floor);
    else down.add(floor);

    return true;
  }

  /**
   * Returns and removes the next floor in up.
   * @return the next in up. Or empty.
   */
  protected Optional<FloorDestination> getNextUp() {
    return Optional.ofNullable(up.pollFirst());
  }

  /**
   * Returns and removes the next floor in down.
   * @return the next in down.  Or empty.
   */
  protected Optional<FloorDestination> getNextDown() {
    return Optional.ofNullable(down.pollLast());
  }

  /**
   * Simulates a run of the elevator using the current requests.
   * @return an ordered list of floor strings by first to last visit.
   */
  public final List<String> run() {
    List<String> floorRun = new ArrayList<>();
    while( !priorityQueue.isEmpty() || !up.isEmpty()|| !down.isEmpty() ) {
      var nextFloor = nextFloor();
      nextFloor.ifPresent(floor -> floorRun.add(floor.floorAsString()));
    }

    return floorRun;
  }

  /**
   * Moves to the next floor request given the elevator's current floor and direction
   * using a basic LOOK algorithm (i.e. move all the way in one direction until
   * last request, then reverse and do the same, or park where we're at if no
   * requests).  If high-priority requests are present, will use those in priority
   * first.
   * @return the next floor that the elevator has moved to.  Or empty if it
   *         didn't move.
   */
  protected Optional<FloorDestination> nextFloor() {
    if( !priorityQueue.isEmpty() ) {
      var nextFloor = priorityQueue.poll();
      goToNextFloor(nextFloor);
      return Optional.of( nextFloor );
    }
    if( up.isEmpty() && down.isEmpty() ) {
      goToNextFloor(null);
      return Optional.empty();
    }

    var nextFloor = switch (curDir) {
      case Direction.REST -> up.size() >= down.size() ? getNextUp() : getNextDown();
      case Direction.UP -> {
          var nextUp = getNextUp();
          yield nextUp.isPresent() ? nextUp : getNextDown();
      }
      case Direction.DOWN -> {
        var nextDown = getNextDown();
        yield nextDown.isPresent() ? nextDown : getNextUp();
      }
    };

    goToNextFloor(nextFloor.orElse(null));
    return nextFloor;
  }

  /**
   * Goes to next floor.
   * @param nextFloor floor to go to, if null will stay and set {@link Direction#REST}.
   * @throws IllegalArgumentException if nextFloor is outside floor limits.
   */
  protected final void goToNextFloor(FloorDestination nextFloor) {
    if( null == nextFloor ) {
      curDir = Direction.REST;
      return;
    }

    if( nextFloor.isOutsideFloorLimit(floorLimit) ) {
      throw new IllegalArgumentException("Next floor is outside floor limits");
    }

    var floorsAway = currentFloor.floorsAway(nextFloor);
    if( 0 > floorsAway ) curDir = Direction.DOWN;
    else if ( 0 < floorsAway ) curDir = Direction.UP;

    currentFloor = nextFloor;
  }
}
