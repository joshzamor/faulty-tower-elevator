import java.util.*;

/**
 * Represents a bassic Elevator that can go to hall calls, button presses,
 * and priority events (e.g. fire).  Implements a basic gossip protocol for
 * decentralized coordination with other Elevators, when other elevators are
 * known.
 */
public class Elevator {

  private final int id;
  private final FloorLimits floorLimit;
  private FloorDestination currentFloor;
  private Direction curDir;

  protected final TreeSet<FloorDestination> up;
  protected final TreeSet<FloorDestination> down;
  protected final PriorityQueue<FloorDestination> priorityQueue;

  protected final List<Elevator> peers;
  protected final Map<Elevator, ElevatorState> elevatorStates;

  /**
   * Constructs an elevator.
   * @param id this elevators id (negative values not allowed).
   * @param floorLimits the lower and upper floors this elevator can go to.
   */
  public Elevator(int id, FloorLimits floorLimits) {
    this.id = Math.abs(id);
    this.floorLimit = floorLimits;
    this.currentFloor = new FloorDestination(0);
    this.curDir = Direction.REST;

    this.up = new TreeSet<>();
    this.down = new TreeSet<>();
    this.priorityQueue = new PriorityQueue<>();

    this.peers = new ArrayList<>();
    this.elevatorStates = new HashMap<>();
  }

  /**
   * Sets the elevator bank this elevator is part of.
   * @param bank the bank of elevators this one belongs.
   * @throws IllegalArgumentException if bank is empty or has different floor limits.
   */
  public final void setElevatorBank(List<Elevator> bank) {
    if( bank.isEmpty() ) throw new IllegalArgumentException("Bank must have one member");
    bank.forEach(e -> {
      if( !e.floorLimit.equals(this.floorLimit) ){
        throw new IllegalArgumentException("Elevator in bank has floor limits inconsistent with this");
      }
      if( !this.equals(e) ) peers.add(e);
    });
  }

  /**
   * Gets the ElevatorState for this Elevator at this moment.
   * @return ElevatorState representing current state
   */
  protected ElevatorState getElevatorState() {
    Set<FloorDestination> requests = new LinkedHashSet<>(priorityQueue.size() + up.size() + down.size());
    requests.addAll(Set.copyOf(priorityQueue));
    requests.addAll(Set.copyOf(up));
    requests.addAll(Set.copyOf(down));
    return new ElevatorState(id,
        currentFloor,
        curDir,
        priorityQueue.size(),
        up.size() + down.size(),
        floorLimit.getNumFloors(),
        requests);
  }

  private void updateSelfElevatorState() {
    elevatorStates.put(this, getElevatorState());
  }

  /**
   * Add floor request.
   * @param floor floor request to add.
   * @return true if floor request added, false otherwise.
   */
  public boolean addRequest(FloorDestination floor) {
    if( floor.isOutsideFloorLimit(floorLimit) ) return false;

    // determine bids and if we should accept
    gossip();
    if( peerHasRequest(floor) ) return false;
    double myBidCost = Elevator.calcBidCostRequest(floor, getElevatorState());
    double lowestInBank = allBidCosts(floor)
        .stream()
        .min(Double::compareTo)
        .orElse(myBidCost);
    if( myBidCost > lowestInBank ) return false;

    if (floor.hasPriority()) {
      priorityQueue.add(floor);
      return true;
    }

    if( currentFloor.isAboveInDirection(floor, curDir) ) up.add(floor);
    else down.add(floor);

    return true;
  }

  private boolean peerHasRequest(FloorDestination request) {
    for( var entry : elevatorStates.entrySet() ) {
      if( entry.getKey().id == this.id ) continue;

      var peerState = entry.getValue();
      if( peerState.hasRequest(request) ) return true;
    }
    return false;
  }

  private List<Double> allBidCosts(FloorDestination request) {
    List<Double> bidCosts = new ArrayList<>(elevatorStates.size());
    elevatorStates.forEach((elev, eState) -> {
      var bidCost = Elevator.calcBidCostRequest(request, eState);
      System.out.printf("Elevator %d bids %f%n", eState.id(), bidCost);
      bidCosts.add(bidCost);
    });
    return bidCosts;
  }

  private void gossip() {
    if( peers.isEmpty() ) return;

    Collections.shuffle(peers);
    Set<Elevator> partners = new LinkedHashSet<>(2);
    int numPartners = Math.min(2, peers.size());
    for(int i = 0; i < numPartners; ++i) {
      partners.add(peers.get(i));
    }

    System.out.printf("Gossip peers selected of %d, are: %s%n", id, partners);
    partners.forEach(peer -> {
        peer.updateElevatorStates(getKnownElevatorStates());
        updateElevatorStates(peer.getKnownElevatorStates());
    });
  }

  /**
   * Updates our knowledge of the other elevator states, IFF the other state is
   * more recent than our own (and not our own).
   * @param peerStates other states.
   */
  protected void updateElevatorStates(Map<Elevator, ElevatorState> peerStates) {
    for( var entry : peerStates.entrySet() ) {
      var peerElev = entry.getKey();
      var peerState = entry.getValue();

      if( !elevatorStates.containsKey(peerElev) ||
           elevatorStates.get(peerElev).created().isBefore(peerState.created()) ) {
        elevatorStates.put(peerElev, peerState);
      }
    }
    updateSelfElevatorState();
  }

  protected Map<Elevator, ElevatorState> getKnownElevatorStates() {
    updateSelfElevatorState();
    return Map.copyOf(elevatorStates);
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
      gossip();
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

  /**
   * Calculates a bid cost for this Elevator to accept a request.  Higher bid
   * cost means less likely bid will be accepted.
   * <p>
   * Elevators at rest will have low bid costs.
   * Elevators at the same floor will have low bid costs.
   * Elevators with lots of other work or priority requests will bid high.
   * Elevators in the same direction have lower bid costs.
   *
   * @param request the floor request.
   * @return the bid cost.
   */
  protected static double calcBidCostRequest(FloorDestination request,
                                           ElevatorState elevatorState) {
    var floorsAway = Math.abs(elevatorState.currentFloor().floorsAway(request));
    double tieBreak = (elevatorState.id() % 1000) / 10000.0; // small nudge for banks

    // priority
    if(elevatorState.prioritySize() > 0) {
      return Double.MAX_VALUE
          - ((Double.MAX_VALUE) / (tieBreak + elevatorState.prioritySize() + 1));
    }

    double loadMultiplier = 1 + ((double) elevatorState.workSize()
        / Math.max(1.0, elevatorState.floorCount()));

    // rest or at floor
    if( Direction.REST == elevatorState.currentDirection()
        || elevatorState.currentFloor().hasFloorNumber(request) ) {
      return tieBreak + floorsAway * loadMultiplier;
    }

    // direction
    double dirMultiplier =
        request.hasSameDirection(elevatorState.currentDirection()) ? 1.1 : 2;

    return tieBreak + floorsAway * dirMultiplier * loadMultiplier;
  }

  @Override
  public String toString() {
    return String.format("Elevator state is: %s",
        getElevatorState());
  }
}
