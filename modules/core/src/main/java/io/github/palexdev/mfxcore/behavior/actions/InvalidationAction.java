/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.behavior.actions;

import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.behavior.DisposableAction;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 * An {@code InvalidationAction} is a convenience bean which implements {@link DisposableAction}
 * used by {@link BehaviorBase} to register an {@link InvalidationListener} on a certain {@link Observable}
 * and dispose it once it's not needed anymore.
 */
public class InvalidationAction implements DisposableAction {
	//================================================================================
	// Properties
	//================================================================================
	private Observable observable;
	private InvalidationListener listener;

	//================================================================================
	// Constructors
	//================================================================================
	public InvalidationAction(Observable observable, InvalidationListener listener) {
		this.observable = observable;
		this.listener = listener;
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Equivalent to {@link #InvalidationAction(Observable, InvalidationListener)} but this also
	 * adds the listener to the observable already.
	 */
	public static InvalidationAction of(Observable observable, InvalidationListener listener) {
		InvalidationAction ia = new InvalidationAction(observable, listener);
		observable.addListener(listener);
		return ia;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Removes the {@link InvalidationListener} from the observable then
	 * sets both the fields to null.
	 */
	@Override
	public void dispose() {
		observable.removeListener(listener);
		listener = null;
		observable = null;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	public Observable getObservable() {
		return observable;
	}

	public InvalidationListener getListener() {
		return listener;
	}
}
