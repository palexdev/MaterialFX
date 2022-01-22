/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.demo.model;

import io.github.palexdev.materialfx.utils.RandomUtils;
import javafx.beans.property.*;

public class Device {
	public enum State {
		ONLINE, OFFLINE
	}

	private final IntegerProperty id = new SimpleIntegerProperty();
	private final StringProperty name = new SimpleStringProperty("");
	private final StringProperty ip = new SimpleStringProperty("");
	private final StringProperty owner = new SimpleStringProperty("");
	private final ObjectProperty<State> state = new SimpleObjectProperty<>();

	public Device(int id, String name, String ip, String owner, State state) {
		setID(id);
		setName(name);
		setIP(ip);
		setOwner(owner);
		setState(state);
	}

	public int getID() {
		return id.get();
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public void setID(int id) {
		this.id.set(id);
	}

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getIP() {
		return ip.get();
	}

	public StringProperty ipProperty() {
		return ip;
	}

	public void setIP(String ip) {
		this.ip.set(ip);
	}

	public String getOwner() {
		return owner.get();
	}

	public StringProperty ownerProperty() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner.set(owner);
	}

	public State getState() {
		return state.get();
	}

	public ObjectProperty<State> stateProperty() {
		return state;
	}

	public void setState(State state) {
		this.state.set(state);
	}

	public static int randomID() {
		return RandomUtils.random.nextInt(100000, 1000000);
	}
}
