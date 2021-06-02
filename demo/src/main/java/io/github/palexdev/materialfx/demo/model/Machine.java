package io.github.palexdev.materialfx.demo.model;

import io.github.palexdev.materialfx.filter.IFilterable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Machine implements IFilterable {

    public enum State {
        ONLINE, OFFLINE
    }

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty ip = new SimpleStringProperty("");
    private final StringProperty owner = new SimpleStringProperty("");
    private final ObjectProperty<State> state = new SimpleObjectProperty<>();

    public Machine(String name, String ip, String owner, State state) {
        setName(name);
        setIp(ip);
        setOwner(owner);
        setState(state);
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

    public String getIp() {
        return ip.get();
    }

    public StringProperty ipProperty() {
        return ip;
    }

    public void setIp(String ip) {
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

    @Override
    public String toFilterString() {
        return getName() + " " + getIp() + " " + getOwner() + " " + getState().name();
    }
}
