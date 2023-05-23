/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcomponents.controls.fab;

import io.github.palexdev.mfxcomponents.behaviors.MFXFabBehavior;
import io.github.palexdev.mfxcomponents.skins.MFXFabSkin;
import io.github.palexdev.mfxcomponents.theming.base.WithVariants;
import io.github.palexdev.mfxcomponents.theming.enums.FABVariants;
import io.github.palexdev.mfxcomponents.theming.enums.MFXThemeManager;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.SceneBuilderIntegration;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;

import java.util.List;

/**
 * Extension of {@link MFXFabBase}. This variant only allows icons to be showed, text will always be
 * set to empty.
 * <p></p>
 * Implements the {@link WithVariants} API, since these type of FABs have slightly different versions, the
 * variants are described by {@link FABVariants}.
 * <p></p>
 * <b>Important note:</b> users should not use {@link #addVariants(FABVariants...)} or {@link #setVariants(FABVariants...)}
 * to make the FAB extended ({@link FABVariants#EXTENDED}) because this could lead to invisible text. Use {@link #setExtended(boolean)} instead.
 *
 * @see MFXFabSkin
 * @see MFXFabBehavior
 */
public class MFXFab extends MFXFabBase implements WithVariants<MFXFab, FABVariants> {

    //================================================================================
    // Constructors
    //================================================================================
    public MFXFab() {
    }

    public MFXFab(String text) {
        super(text);
    }

    public MFXFab(MFXFontIcon icon) {
        super(icon);
    }

    public MFXFab(String text, MFXFontIcon icon) {
        super(text, icon);
    }

    /**
     * Creates a new {@link MFXFab} which is extended (shows text).
     */
    public static MFXFab extended() {
        MFXFab fab = new MFXFab();
        fab.setExtended(true);
        return fab;
    }

    //================================================================================
    // Variants
    //================================================================================
    public MFXFab small() {
        setVariants(FABVariants.SMALL);
        return this;
    }

    public MFXFab large() {
        setVariants(FABVariants.LARGE);
        return this;
    }

    public MFXFab surface() {
        setVariants(FABVariants.SURFACE);
        return this;
    }

    public MFXFab secondary() {
        setVariants(FABVariants.SECONDARY);
        return this;
    }

    public MFXFab tertiary() {
        setVariants(FABVariants.TERTIARY);
        return this;
    }

    public MFXFab lowered() {
        setVariants(FABVariants.LOWERED);
        return this;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-button", "fab-base", "fab");
    }

    @Override
    public MFXFab addVariants(FABVariants... variants) {
        WithVariants.addVariants(this, variants);
        onInitSizesChanged();
        return this;
    }

    @Override
    public MFXFab setVariants(FABVariants... variants) {
        WithVariants.setVariants(this, variants);
        onInitSizesChanged();
        return this;
    }

    @Override
    public MFXFab removeVariants(FABVariants... variants) {
        WithVariants.removeVariants(this, variants);
        onInitSizesChanged();
        return this;
    }

    @Override
    protected void sceneBuilderIntegration() {
        SceneBuilderIntegration.ifInSceneBuilder(() -> {
            String theme = MFXThemeManager.PURPLE_LIGHT.load();
            When.onChanged(sceneProperty())
                .condition((o, n) -> n != null && !n.getStylesheets().contains(theme))
                .then((o, n) -> n.getStylesheets().add(theme))
                .oneShot()
                .listen();
        });
        // TODO theme integration with SceneBuilder will change once base themes and MFXThemeManager are implemented
    }
}
