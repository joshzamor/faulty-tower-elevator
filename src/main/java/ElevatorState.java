import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

public record ElevatorState(int id,
                            FloorDestination currentFloor,
                            Direction currentDirection,
                            int prioritySize,
                            int workSize,
                            int floorCount,
                            Set<FloorDestination> requests,
                            LocalDateTime created) {


  public ElevatorState {
    created = LocalDateTime.now();
  }

  public ElevatorState(int id,
                       FloorDestination currentFloor,
                       Direction currentDirection,
                       int prioritySize,
                       int workSize,
                       int floorCount,
                       Set<FloorDestination> requests) {
    this(id,
        currentFloor,
        currentDirection,
        prioritySize,
        workSize,
        floorCount,
        requests,
        null);
  }

  public ElevatorState(int id,
                       FloorDestination currentFloor,
                       Direction currentDirection,
                       int prioritySize,
                       int workSize,
                       int floorCount) {
    this(id,
        currentFloor,
        currentDirection,
        prioritySize,
        workSize,
        floorCount,
        new LinkedHashSet<>(),
        null);
  }

  public boolean hasRequest(FloorDestination request) {
    return requests.contains(request);
  }
}
