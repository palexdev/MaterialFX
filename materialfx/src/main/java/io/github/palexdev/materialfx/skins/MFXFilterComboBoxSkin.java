package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.collections.TransformableList;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXFilterComboBoxCell;
import io.github.palexdev.virtualizedfx.cell.Cell;
import io.github.palexdev.virtualizedfx.flow.simple.SimpleVirtualFlow;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Skin associated with every {@link MFXFilterComboBox} by default.
 * <p>
 * Extends {@link MFXComboBoxSkin}.
 * <p>
 * This skin mainly overrides the {@link #createPopupContent()} and implements the
 * method responsible for filtering the popup's listview.
 */
public class MFXFilterComboBoxSkin<T> extends MFXComboBoxSkin<T> {

	//================================================================================
	// Constructors
	//================================================================================
	public MFXFilterComboBoxSkin(MFXFilterComboBox<T> comboBox, ReadOnlyBooleanWrapper floating) {
		super(comboBox, floating);
		popup.setContent(createPopupContent());
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Responsible for filtering the popup's listview.
	 * <p></p>
	 * What it really does is to use the {@link MFXFilterComboBox#filterFunctionProperty()}
	 * to produce a {@link Predicate}, which is then set on the {@link MFXFilterComboBox#getFilterList()}.
	 * <p></p>
	 * This means that since it is not bound you can even set your own predicate on that list,
	 * but everytime the text is changed in the search field it will be replaced.
	 */
	protected void filter(String text) {
		MFXFilterComboBox<T> comboBox = getComboBox();
		Function<String, Predicate<T>> filterFunction = comboBox.getFilterFunction();
		if (filterFunction == null) return;

		Predicate<T> filter = filterFunction.apply(text);
		comboBox.getFilterList().setPredicate(filter);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected void initialize() {}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * The content is slightly different from the {@link MFXComboBoxSkin} one.
	 * <p>
	 * In the previous combo box skin, a text field was positioned on top of the combo's field
	 * to input the search text.
	 * <p>
	 * This time I decided to do it another way. The popup contains both the search field
	 * and the listview, contained in a VBox, this way the control is easier to maintain,
	 * and also more appealing.
	 */
	@Override
	protected Node createPopupContent() {
		MFXFilterComboBox<T> comboBox = getComboBox();
		TransformableList<T> filterList = comboBox.getFilterList();

		MFXTextField searchField = new MFXTextField("", "Search...");
		searchField.getStyleClass().add("search-field");
		searchField.textProperty().addListener(invalidated -> filter(searchField.getText()));
		searchField.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> searchField.requestFocus());

		SimpleVirtualFlow<T, Cell<T>> virtualFlow = SimpleVirtualFlow.Builder.create(
				filterList,
				t -> new MFXFilterComboBoxCell<>(comboBox, filterList, t),
				Orientation.VERTICAL
		);
		virtualFlow.cellFactoryProperty().bind(comboBox.cellFactoryProperty());
		virtualFlow.prefWidthProperty().bind(comboBox.widthProperty());
		virtualFlow.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (popup.isShowing()) {
				popup.hide();
				searchField.setText("");
			}
		});

		VBox container = new VBox(10, searchField, virtualFlow);
		container.getStyleClass().add("search-container");
		container.setAlignment(Pos.TOP_CENTER);
		return container;
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Overridden to cast to {@code MFXFilterComboBox}.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MFXFilterComboBox<T> getComboBox() {
		return (MFXFilterComboBox<T>) getSkinnable();
	}


}
