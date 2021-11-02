/*
 * Copyright (C) 2021 Parisi Alessandro
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

package io.github.palexdev.materialfx.validation;

import io.github.palexdev.materialfx.utils.StringUtils;
import io.github.palexdev.materialfx.validation.base.AbstractMFXValidator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is a concrete implementation of {@link AbstractMFXValidator}.
 * <p>
 * The idea of this is to have a validator which automatically updates its message according to the
 * state of the conditions.
 * <p>
 * A {@link MapProperty} is used to map the added conditions to a user defined message/string,
 */
public class MFXPriorityValidator extends AbstractMFXValidator {
    //================================================================================
    // Properties
    //================================================================================
    protected final MapProperty<BooleanProperty, String> messagesMap;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXPriorityValidator() {
        messagesMap = new SimpleMapProperty<>(FXCollections.observableMap(new LinkedHashMap<>()));
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        validatorMessageProperty().bind(Bindings.createStringBinding(
                this::findMessage,
                valid, messagesMap)
        );
    }

    /**
     * Finds the first property in the {@link MapProperty} which is false and returns the associated message.
     * <p></p>
     * If nothing is found then proceeds to evaluate all the dependencies' maps.
     * In case nothing is found even in the dependencies then returns an empty string.
     */
    private String findMessage() {
        String message = messagesMap.entrySet().stream()
                .filter(entry -> !entry.getKey().get())
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse("");

        if (!message.isEmpty()) {
            return message;
        }

        List<MFXPriorityValidator> dependencies = getDependencies().stream()
                .filter(validator -> !validator.isValid())
                .filter(validator -> validator instanceof MFXPriorityValidator)
                .map(validator -> (MFXPriorityValidator) validator)
                .collect(Collectors.toList());

        return dependencies.stream().map(dependency -> dependency.messagesMap.entrySet().stream()
                .filter(entry -> !entry.getKey().get())
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse("")).filter(dependencyMessage -> !dependencyMessage.isEmpty()).findFirst().orElse("");
    }

    /**
     * Adds a new boolean condition to the list with the corresponding message in case it is false.
     *
     * @param property The new boolean condition
     * @param message  The message to show in case it is false
     */
    public void add(BooleanProperty property, String message) {
        super.conditions.add(property);
        this.messagesMap.put(property, message);
    }

    /**
     * Removes the given property and the corresponding message from the list.
     */
    public void remove(BooleanProperty property) {
        messagesMap.remove(property);
        super.conditions.remove(property);
    }

    /**
     * Checks the messages list and if the corresponding boolean condition is false
     * adds the message to the {@code StringBuilder}.
     */
    public String getUnmetMessages() {
        StringBuilder sb = new StringBuilder();
        for (BooleanProperty property : messagesMap.keySet()) {
            if (!property.get()) {
                sb.append(messagesMap.get(property)).append(",\n");
            }
        }
        return StringUtils.replaceLast(sb.toString(), ",", ".");
    }
}
