public record FloorLimits(int min, int max) {
  public FloorLimits {
    if( max < min ) throw new IllegalArgumentException("Max floor can't be less than min floor");
  }
}
