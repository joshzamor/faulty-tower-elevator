/**
 * Represents a floor limit for an elevator to traverse within.
 * @param min minimum floor (inclusive)
 * @param max maximum floor (inclusive)
 */
public record FloorLimits(int min, int max) {

  /**
   * Creates a FloorLimit
   * @throws IllegalArgumentException if max < min
   */
  public FloorLimits {
    if( max < min ) throw new IllegalArgumentException("Max floor can't be less than min floor");
  }

  /**
   * The number of floors in this set of limits.
   * @return number of floors.
   */
  public int getNumFloors() {
    return Math.abs(max - min);
  }
}
