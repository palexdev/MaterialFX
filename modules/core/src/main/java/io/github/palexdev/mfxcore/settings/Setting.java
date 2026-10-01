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

package io.github.palexdev.mfxcore.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class Setting<T> {

    //================================================================================
    // Properties
    //================================================================================

    protected final String name;
    protected final String description;
    protected final T defaultValue;
    protected final Settings container;
    protected boolean avoidEmpty = false;
    private final List<Consumer<T>> listeners = new ArrayList<>();

    //================================================================================
    // Constructors
    //================================================================================

    protected Setting(String name, String description, T defaultValue, Settings container) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.container = container;
    }

    //================================================================================
    // Abstract Methods
    //================================================================================

    public abstract T get();

    protected abstract boolean write(T val);

    //================================================================================
    // Methods
    //================================================================================

    public final void set(T val) {
        if (write(val)) listeners.forEach(l -> l.accept(val));
    }

    public void onChange(Consumer<T> listener) {
        listeners.add(listener);
    }

    public void removeOnChange(Consumer<T> listener) {
        listeners.remove(listener);
    }

    public void reset() {
        set(defaultValue);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public boolean equals(Object o) {
        return o instanceof Setting<?> other
               && name.equals(other.name)
               && container.prefs().absolutePath().equals(other.container.prefs().absolutePath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, container.prefs().absolutePath());
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public T defValue() {
        return defaultValue;
    }

    public Settings container() {
        return container;
    }

    public boolean isAvoidEmpty() {
        return avoidEmpty;
    }

    public Setting<T> setAvoidEmpty(boolean avoidEmpty) {
        this.avoidEmpty = avoidEmpty;
        return this;
    }
}
