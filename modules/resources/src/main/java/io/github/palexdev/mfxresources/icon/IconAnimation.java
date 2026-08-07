/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxresources.icon;

import io.github.palexdev.mfxeffects.animations.Animations;
import javafx.animation.Animation;

/// Helper class to manage a [MFXIconWrapper] state when switching icons through an animation.
/// It has three properties: the actual animation, the old icon, and the new icon.
///
/// @see #onStopped(MFXIconWrapper)
public class IconAnimation {
    //================================================================================
    // Properties
    //================================================================================
    private final Animation animation;
    private final MFXFontIcon oldIcon;
    private final MFXFontIcon newIcon;

    //================================================================================
    // Constructors
    //================================================================================
    public IconAnimation(Animation animation, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
        this.animation = animation;
        this.oldIcon = oldIcon;
        this.newIcon = newIcon;
    }

    //================================================================================
    // Methods
    //================================================================================
    public void play(MFXIconWrapper wrapper) {
        if (animation != null) {
            Animations.onStopped(animation, () -> onStopped(wrapper), true);
            animation.play();
        }
    }

    public void stop() {
        if (!Animations.isStopped(animation))
            animation.stop();
    }

    /// When the animation is played by [#play(MFXIconWrapper)] a listener is added to its state property.
    /// When the animation ends or is stopped, this method is invoked. By default, the only action performed here is removing
    /// the old icon from the children's list, thus ensuring the wrapper is in a consistent state.
    ///
    /// Animation authors may want to override this and add code to reset the new icon state if the animation is interrupted.
    protected void onStopped(MFXIconWrapper wrapper) {
        wrapper.getChildren().remove(oldIcon);
    }

    //================================================================================
    // Getters
    //================================================================================
    public Animation animation() {return animation;}

    public MFXFontIcon oldIcon() {return oldIcon;}

    public MFXFontIcon newIcon() {return newIcon;}
}
