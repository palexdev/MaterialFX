package io.github.palexdev.materialfx.effects;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;

/**
 * Utility class which manages a preset number of {@code DropShadow} effects ordered by {@code DepthLevel}.
 * <p></p>
 * {@link DepthLevel}
 */
public class MFXDepthManager {

    /**
     * Retrieves the {@code DropShadow} associated with the specified {@code DepthLevel}.
     * @param level The desired {@code DepthLevel} between 1 and 5
     * @return The desired {@code DropShadow} effect
     */
    public static DropShadow shadowOf(DepthLevel level) {
        return new DropShadow(
                BlurType.GAUSSIAN,
                level.getColor(),
                level.getRadius(),
                level.getSpread(),
                level.getOffsetX(),
                level.getOffsetY()
        );
    }

    /**
     * Retrieves the {@code DropShadow} associated with the specified {@code DepthLevel} added to delta.
     * <p></p>
     * Example 1: for a depth level equal to 3 and a delta equal to 2, the returned {@code DropShadow} effect is
     * the effected associated to a depth level of 5.
     * <p></p>
     * Example 2: for a depth level equal to 5 and a delta equal to whatever integer, the returned {@code DropShadow} effect is
     * the effected associated to a depth level of 5.
     * @param level The desired {@code DepthLevel} between 1 and 5
     * @param delta The number of levels to shift
     * @return The final {@code DropShadow} effect}
     * <p></p>
     * {@link #nextLevel(DepthLevel)}
     */
    public static DropShadow shadowOf(DepthLevel level, int delta) {
        DepthLevel endLevel = level;
        for (int i = 0; i < delta; i++) {
            endLevel = nextLevel(endLevel);
        }
        return shadowOf(endLevel);
    }

    /**
     * From a starting {@code DepthLevel} retrieves the {@code DropShadow} effect associated to the next {@code DepthLevel}.
     * @param startLevel The starting {@code DepthLevel}
     * @return The {@code DropShadow} effect associated to the next {@code DepthLevel}
     * @see DepthLevel
     */
    private static DepthLevel nextLevel(DepthLevel startLevel) {
        return !(startLevel.equals(DepthLevel.LEVEL5)) ? startLevel.next() : DepthLevel.LEVEL5;
    }
}
