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
