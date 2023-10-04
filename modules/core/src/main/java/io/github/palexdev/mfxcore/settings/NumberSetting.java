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

package io.github.palexdev.mfxcore.settings;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.prefs.Preferences;

public class NumberSetting<N extends Number> extends Setting<N> {
	//================================================================================
	// Properties
	//================================================================================
	protected Function<Preferences, N> fetcher;
	protected BiConsumer<Preferences, N> updater;

	//================================================================================
	// Constructors
	//================================================================================
	protected NumberSetting(String name, String description, N defaultValue, Settings container) {
		super(name, description, defaultValue, container);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public N get() {
		return fetcher.apply(container.prefs());
	}

	@Override
	public void set(N val) {
		updater.accept(container.prefs(), val);
	}

	//================================================================================
	// Impl
	//================================================================================
	public static NumberSetting<Double> forDouble(String name, String description, double defaultVal, Settings container) {
		NumberSetting<Double> setting = new NumberSetting<>(name, description, defaultVal, container);
		setting.setFetcher(p -> p.getDouble(name, defaultVal));
		setting.setUpdater((p, v) -> p.putDouble(name, v));
		return setting;
	}

	public static NumberSetting<Float> forFloat(String name, String description, float defaultValue, Settings container) {
		NumberSetting<Float> setting = new NumberSetting<>(name, description, defaultValue, container);
		setting.setFetcher(p -> p.getFloat(name, defaultValue));
		setting.setUpdater((p, v) -> p.putFloat(name, v));
		return setting;
	}

	public static NumberSetting<Integer> forInteger(String name, String description, int defaultValue, Settings container) {
		NumberSetting<Integer> setting = new NumberSetting<>(name, description, defaultValue, container);
		setting.setFetcher(p -> p.getInt(name, defaultValue));
		setting.setUpdater((p, v) -> p.putInt(name, v));
		return setting;
	}

	public static NumberSetting<Long> forLong(String name, String description, long defaultValue, Settings container) {
		NumberSetting<Long> setting = new NumberSetting<>(name, description, defaultValue, container);
		setting.setFetcher(p -> p.getLong(name, defaultValue));
		setting.setUpdater((p, v) -> p.putLong(name, v));
		return setting;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public Function<Preferences, N> getFetcher() {
		return fetcher;
	}

	public void setFetcher(Function<Preferences, N> fetcher) {
		this.fetcher = fetcher;
	}

	public BiConsumer<Preferences, N> getUpdater() {
		return updater;
	}

	public void setUpdater(BiConsumer<Preferences, N> updater) {
		this.updater = updater;
	}
}
