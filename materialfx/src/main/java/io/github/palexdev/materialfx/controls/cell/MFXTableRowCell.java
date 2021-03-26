/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls.cell;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

/**
 * This is the implementation of the row cells used in every {@link io.github.palexdev.materialfx.controls.MFXTableView} row.
 * The row cell is built by the corresponding {@link MFXTableColumnCell}.
 * <p>
 * Each row cell is a {@code Label} and provides a series of constructors offering support from simple {@code Strings} to
 * JavaFX's {@code StringProperties} and {@code StringBindings}. This allows to use {@code MFXTableView} with models which
 * don't use JavaFX's properties.
 * <p></p>
 * Beware though, the data won't be automatically updated if it changes.
 * <p>
 * In that case you must manually update the table firing a TableViewEvent.FORCE_UPDATE event on it.
 * <p>
 * After the data changed like this:
 * <pre>
 * {@code
 * ObservableList<Items> list = ...;
 * MFXTableView tableView = new MFXTableView(list);
 *
 * tableView.fireEvent(new MFXTableView.TableViewEvent(MFXTableView.TableViewEvent.FORCE_UPDATE_EVENT));
 * }
 * </pre>
 */
public class MFXTableRowCell extends Label {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-table-row-cell";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-table-row-cell.css");

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTableRowCell(String text) {
        super(text);
        initialize();
    }

    public MFXTableRowCell(StringProperty stringProperty) {
        textProperty().bind(stringProperty);
        initialize();
    }

    public MFXTableRowCell(StringBinding stringBinding) {
        textProperty().bind(stringBinding);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
