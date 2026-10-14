package model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@ToString
@Getter
public class InitializationInput {
    int boardHeight;
    int boardWidth;
    List<Brick> bricks;
}
