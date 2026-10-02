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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

public abstract class Settings {

    //================================================================================
    // Properties
    //================================================================================

    private final String node;
    protected final Preferences prefs;
    private final Set<Setting<?>> settings = new LinkedHashSet<>();

    //================================================================================
    // Constructors

    //================================================================================
    protected Settings(String node) {
        if (node == null || node.isBlank())
            throw new IllegalArgumentException("Node cannot be null or blank");
        this.node = node;
        prefs = init();
    }

    //================================================================================
    // Methods
    //================================================================================

    protected Preferences init() {
        if (prefs == null) return Preferences.userRoot().node(node());
        return prefs;
    }

    protected <E extends Enum<E>> EnumSetting<E> registerEnum(String name, String description, E defaultValue) {
        EnumSetting<E> setting = EnumSetting.of(name, description, defaultValue, this);
        settings.add(setting);
        return setting;
    }
    
    protected StringSetting registerString(String name, String description, String defaultValue) {
        StringSetting setting = StringSetting.of(name, description, defaultValue, this);
        settings.add(setting);
        return setting;
    }

    protected BooleanSetting registerBoolean(String name, String description, boolean defaultValue) {
        BooleanSetting setting = BooleanSetting.of(name, description, defaultValue, this);
        settings.add(setting);
        return setting;
    }

    protected NumberSetting<Double> registerDouble(String name, String description, double defaultValue) {
        NumberSetting<Double> setting = NumberSetting.forDouble(name, description, defaultValue, this);
        settings.add(setting);
        return setting;
    }

    protected NumberSetting<Float> registerFloat(String name, String description, float defaultValue) {
        NumberSetting<Float> setting = NumberSetting.forFloat(name, description, defaultValue, this);
        settings.add(setting);
        return setting;
    }

    protected NumberSetting<Integer> registerInteger(String name, String description, int defaultValue) {
        NumberSetting<Integer> setting = NumberSetting.forInteger(name, description, defaultValue, this);
        settings.add(setting);
        return setting;
    }

    protected NumberSetting<Long> registerLong(String name, String description, long defaultValue) {
        NumberSetting<Long> setting = NumberSetting.forLong(name, description, defaultValue, this);
        settings.add(setting);
        return setting;
    }
    
    protected ByteArraySetting registerByteArray(String name, String description, byte[] defaultValue) {
        ByteArraySetting setting = ByteArraySetting.of(name, description, defaultValue, this);
        settings.add(setting);
        return setting;
    }

    public void onChange(PreferenceChangeListener pcl) {
        prefs.addPreferenceChangeListener(pcl);
    }

    public void removeOnChange(PreferenceChangeListener pcl) {
        prefs.removePreferenceChangeListener(pcl);
    }

    public void reset() {
        settings.forEach(Setting::reset);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    protected String node() {
        return node;
    }

    protected Preferences prefs() {
        return prefs;
    }

    public Set<Setting<?>> settings() {
        return Collections.unmodifiableSet(settings);
    }
}
