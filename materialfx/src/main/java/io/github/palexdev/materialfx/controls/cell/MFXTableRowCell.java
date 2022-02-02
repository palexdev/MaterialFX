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

package io.github.palexdev.materialfx.controls.cell;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableRow;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.skins.MFXTableRowCellSkin;
import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.util.StringConverter;

import java.util.Objects;
import java.util.function.Function;

/**
 * This is the implementation of the row cells used by {@link MFXTableView} to fill a {@link MFXTableRow}.
 * <p>
 * The cell is built by the corresponding column that defines the factory, {@link MFXTableColumn#rowCellFactoryProperty()}.
 * <p>
 * To make it easily customizable, extends {@link Labeled} and provides a new default skin, {@link  MFXTableRowCellSkin}.
 * <p>
 * The default skin, {@link MFXTableRowCellSkin}, also allows placing up to two nodes in the cell. These nodes are specified by
 * the following properties, {@link #leadingGraphicProperty()}, {@link #trailingGraphicProperty()}.
 * <p>
 * A little side note, also to respond to some GitHub issues. It is not recommended to use big nodes. It is not recommended to
 * use too many nodes, that's why it's limited to two. If you need a lot of controls then consider having specific columns which build cells only with graphic
 * like here <a href="https://bit.ly/2SzjrVu">Example</a>.
 * <p>
 * Since it now extends {@link Labeled} you can easily define your own skin and do whatever you like with the
 * control, just keep in mind that tables are designed to mostly show text.
 * <p>
 * The new implementation doesn't bind the text anymore, but requires the user to specify the {@link Function} to
 * extract from an object of type T (which is the same type of the table view) the data E the cell will represent.
 * This way the cell's content is not updated anymore via observables/bindings, but it's managed by the parent {@link MFXTableRow},
 * or updated programmatically when requested by the table view (but the update process always starts from the row though).
 * The cell's text is computed using a {@link StringConverter}, the default converter uses {@link Objects#toString(Object)}
 * The update process is handled by {@link #update(Object)}.
 */
public class MFXTableRowCell<T, E> extends Labeled {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-table-row-cell";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXTableView.css");

	private final Function<T, E> extractor;
	private final StringConverter<E> converter;
	private final ObjectProperty<Node> leadingGraphic = new SimpleObjectProperty<>();
	private final ObjectProperty<Node> trailingGraphic = new SimpleObjectProperty<>();

	//================================================================================
	// Constructors
	//================================================================================
	public MFXTableRowCell(Function<T, E> extractor) {
		this(extractor, Objects::toString);
	}

	public MFXTableRowCell(Function<T, E> extractor, Function<E, String> converter) {
		this.extractor = extractor;
		this.converter = FunctionalStringConverter.to(converter);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setAlignment(Pos.CENTER_LEFT);
	}

	/**
	 * Responsible for updating the cell's text.
	 */
	public void update(T item) {
		if (extractor == null) return;
		E data = extractor.apply(item);
		setText(converter.toString(data));
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Overridden to make it public.
	 * <p>
	 * Needed by the table view to compute the cell's width at which the content is not truncated.
	 * The width computation is handled by the skin's computePrefWidth(...) method.
	 */
	public double computePrefWidth(double height) {
		return super.computePrefWidth(height);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXTableRowCellSkin<>(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public Node getLeadingGraphic() {
		return leadingGraphic.get();
	}

	/**
	 * Specifies the cell's leading node
	 */
	public ObjectProperty<Node> leadingGraphicProperty() {
		return leadingGraphic;
	}

	public void setLeadingGraphic(Node leadingGraphic) {
		this.leadingGraphic.set(leadingGraphic);
	}

	public Node getTrailingGraphic() {
		return trailingGraphic.get();
	}

	/**
	 * Specifies the cell's trailing node
	 */
	public ObjectProperty<Node> trailingGraphicProperty() {
		return trailingGraphic;
	}

	public void setTrailingGraphic(Node trailingGraphic) {
		this.trailingGraphic.set(trailingGraphic);
	}
}
