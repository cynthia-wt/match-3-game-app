package model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActiveBrickState {
    int horizontalPosition;
    int verticalPosition;
    boolean landed;
}
