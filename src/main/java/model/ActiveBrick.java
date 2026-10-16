package model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActiveBrick {
    Brick brick;
    int horizontalPosition;
    int verticalPosition;
    boolean landed;
}
