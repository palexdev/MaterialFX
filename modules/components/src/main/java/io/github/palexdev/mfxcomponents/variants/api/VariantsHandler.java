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

package io.github.palexdev.mfxcomponents.variants.api;

import java.util.LinkedHashSet;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.css.Styleable;

public class VariantsHandler<S extends Styleable & WithVariants> {
    //================================================================================
    // Properties
    //================================================================================

    private final S styleable;
    private final ObservableMap<Class<?>, Variant> variantsMap;
    private ObservableMap<Class<?>, Variant> _unmodifiable;
    private boolean batchUpdate = false;

    //================================================================================
    // Constructors
    //================================================================================
    public VariantsHandler(S styleable) {
        this.styleable = styleable;
        this.variantsMap = FXCollections.observableHashMap();
        variantsMap.addListener(this::update);
    }

    //================================================================================
    // Methods
    //================================================================================

    protected void update(MapChangeListener.Change<? extends Class<?>, ? extends Variant> change) {
        if (batchUpdate) return;
        if (change.wasRemoved()) {
            styleable.getStyleClass().remove(change.getValueRemoved().variantStyleClass());
        }
        if (change.wasAdded()) {
            styleable.getStyleClass().add(change.getValueAdded().variantStyleClass());
        }
    }

    @SafeVarargs
    public final <E extends Enum<?> & Variant> void setVariants(E... variants) {
        batchUpdate = true;
        Set<String> tmp = new LinkedHashSet<>(styleable.getStyleClass());
        for (E variant : variants) {
            setVariant(variant);
            tmp.add(variant.variantStyleClass());
        }
        styleable.getStyleClass().setAll(tmp);
        batchUpdate = false;
    }

    public <E extends Enum<?> & Variant> void setVariant(E variant) {
        variantsMap.put(variant.getClass(), variant);
    }

    public <E extends Enum<?> & Variant> void unsetVariant(Class<E> klass) {
        variantsMap.remove(klass);
    }

    public final void clearVariants() {
        variantsMap.clear();
    }

    public ObservableMap<Class<?>, Variant> getAppliedVariants() {
        return variantsMap;
    }

    public ObservableMap<Class<?>, Variant> getAppliedVariantsUnmodifiable() {
        if (_unmodifiable == null) {
            _unmodifiable = FXCollections.unmodifiableObservableMap(variantsMap);
        }
        return _unmodifiable;
    }
}
