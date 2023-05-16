package io.github.palexdev.mfxcomponents.behaviors;

import io.github.palexdev.mfxcomponents.controls.buttons.MFXSegmentedButton;
import io.github.palexdev.mfxcomponents.skins.MFXSegmentedButtonSkin.MFXSegment;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;

/**
 * Default behavior used by {@link MFXSegmentedButton}, it's however empty, as the event target is not the button
 * itself but its segments. But since {@link MFXSegment} is a common selectable, it uses behaviors of type
 * {@link MFXSelectableBehaviorBase}.
 */
public class MFXSegmentedButtonBehavior extends BehaviorBase<MFXSegmentedButton> {

    public MFXSegmentedButtonBehavior(MFXSegmentedButton node) {
        super(node);
    }
}
