package model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameState {
    char[][] currentFrame;
    int currentHorizontalPosition;
    int currentVerticalPosition;
}
