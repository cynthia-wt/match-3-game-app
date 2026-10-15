package model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class ActiveBrick {
    Brick brick;
    int horizontalPosition;
    int verticalPosition;
    boolean landed;
}
