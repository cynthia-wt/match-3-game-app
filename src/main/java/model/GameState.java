package model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class GameState {
    int frameNumber;
    int brickIndex;
    ActiveBrick activeBrick;
}
