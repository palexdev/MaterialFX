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

import io.github.palexdev.mfxeffects.animations.AnimationFactory;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxresources.icon.IconClip.ClipShape;
import javafx.scene.CacheHint;

public enum AnimationPresets {
    FADING {
        @Override
        public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
            newIcon.setOpacity(0.0);
            newIcon.setVisible(true);
            M3Motion.MotionPreset motion = M3Motion.EXPRESSIVE_DEFAULT_EFFECTS;
            return new IconAnimation(
                new Animations.SequentialBuilder()
                    .addIf(oldIcon != null, AnimationFactory.FADE_OUT.build(oldIcon, motion.duration(), motion.curve()))
                    .add(AnimationFactory.FADE_IN.build(newIcon, motion.duration(), motion.curve()))
                    .getAnimation(),
                oldIcon,
                newIcon
            );
        }
    },

    SCALE {
        @Override
        public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
            oldIcon.setCacheHint(CacheHint.SCALE);
            oldIcon.setCache(true);
            newIcon.setCacheHint(CacheHint.SCALE);
            newIcon.setCache(true);
            newIcon.setScaleX(0);
            newIcon.setScaleY(0);
            newIcon.setOpacity(0.0);
            newIcon.setVisible(true);
            M3Motion.MotionPreset motion = M3Motion.EXPRESSIVE_FAST_SPATIAL;
            return new IconAnimation(
                Animations.TimelineBuilder.build()
                    .add(
                        Animations.KeyFrames.of(motion.duration(), oldIcon.opacityProperty(), 0.0, motion.curve()),
                        Animations.KeyFrames.of(motion.duration(), oldIcon.scaleXProperty(), 0.0, motion.curve()),
                        Animations.KeyFrames.of(motion.duration(), oldIcon.scaleYProperty(), 0.0, motion.curve())
                    )
                    .add(
                        Animations.KeyFrames.of(motion.duration(), newIcon.opacityProperty(), 1.0, motion.curve()),
                        Animations.KeyFrames.of(motion.duration(), newIcon.scaleXProperty(), 1.0, motion.curve()),
                        Animations.KeyFrames.of(motion.duration(), newIcon.scaleYProperty(), 1.0, motion.curve())
                    ).getAnimation(),
                oldIcon,
                newIcon
            ) {
                @Override
                protected void onStopped(MFXIconWrapper wrapper) {
                    super.onStopped(wrapper);
                    newIcon.setCacheHint(CacheHint.QUALITY);
                    newIcon.setOpacity(1.0);
                    newIcon.setScaleX(1.0);
                    newIcon.setScaleY(1.0);
                }
            };
        }
    },

    SLIDE_TOP {
        @Override
        public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
            return slide(wrapper, oldIcon, AnimationFactory.SLIDE_OUT_TOP, newIcon, AnimationFactory.SLIDE_IN_TOP);
        }
    },

    SLIDE_BOTTOM {
        @Override
        public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
            return slide(wrapper, oldIcon, AnimationFactory.SLIDE_OUT_BOTTOM, newIcon, AnimationFactory.SLIDE_IN_BOTTOM);
        }
    },

    SLIDE_BOTTOM_TOP {
        @Override
        public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
            return slide(wrapper, oldIcon, AnimationFactory.SLIDE_OUT_BOTTOM, newIcon, AnimationFactory.SLIDE_IN_TOP);
        }
    },

    SLIDE_TOP_BOTTOM {
        @Override
        public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
            return slide(wrapper, oldIcon, AnimationFactory.SLIDE_OUT_TOP, newIcon, AnimationFactory.SLIDE_IN_BOTTOM);
        }
    },

    SLIDE_RIGHT {
        @Override
        public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
            return slide(wrapper, oldIcon, AnimationFactory.SLIDE_OUT_RIGHT, newIcon, AnimationFactory.SLIDE_IN_RIGHT);
        }
    },

    SLIDE_LEFT {
        @Override
        public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
            return slide(wrapper, oldIcon, AnimationFactory.SLIDE_OUT_LEFT, newIcon, AnimationFactory.SLIDE_IN_LEFT);
        }
    },

    SLIDE_RIGHT_LEFT {
        @Override
        public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
            return slide(wrapper, oldIcon, AnimationFactory.SLIDE_OUT_RIGHT, newIcon, AnimationFactory.SLIDE_IN_RIGHT);
        }
    },

    SLIDE_LEFT_RIGHT {
        @Override
        public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
            return slide(wrapper, oldIcon, AnimationFactory.SLIDE_OUT_LEFT, newIcon, AnimationFactory.SLIDE_IN_LEFT);
        }
    },
    ;

    public abstract IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon);

    protected IconAnimation slide(MFXIconWrapper wrapper,
                                  MFXFontIcon oldIcon, AnimationFactory oldSlide,
                                  MFXFontIcon newIcon, AnimationFactory newSlide
    ) {
        clip(wrapper);
        newIcon.setOpacity(0.0);
        newIcon.setVisible(true);
        M3Motion.MotionPreset motion = M3Motion.EXPRESSIVE_FAST_SPATIAL;
        return new IconAnimation(
            Animations.TimelineBuilder.build()
                .add(Animations.KeyFrames.of(motion.duration(), oldIcon.opacityProperty(), 0.0, motion.curve()))
                .add(oldSlide.keyFrames(oldIcon, motion.millis(), motion.curve()))
                .add(Animations.KeyFrames.of(motion.duration(), newIcon.opacityProperty(), 1.0, motion.curve()))
                .add(newSlide.keyFrames(newIcon, motion.millis(), motion.curve()))
                .getAnimation(),
            oldIcon,
            newIcon
        ) {
            @Override
            protected void onStopped(MFXIconWrapper wrapper) {
                super.onStopped(wrapper);
                if (!wrapper.preserveClip) wrapper.setIconClip(null);
                newIcon.setTranslateX(0);
                newIcon.setTranslateY(0);
                newIcon.setOpacity(1.0);
            }
        };
    }

    /// Used by the slide animations to clip the [MFXIconWrapper] so that icons that go outside its bounds are
    /// hidden. The animations automatically remove it once they stop/end.
    protected void clip(MFXIconWrapper wrapper) {
        if (wrapper.getClip() != null) {
            wrapper.preserveClip = true;
            return;
        }
        wrapper.setIconClip(IconClip.of(ClipShape.SQUARED, 0.0));
        wrapper.preserveClip = false;
    }
}
