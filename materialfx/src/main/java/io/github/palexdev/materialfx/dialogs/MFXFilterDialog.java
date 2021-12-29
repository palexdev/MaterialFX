package io.github.palexdev.materialfx.dialogs;

import io.github.palexdev.materialfx.beans.FilterBean;
import io.github.palexdev.materialfx.controls.MFXFilterPane;
import io.github.palexdev.materialfx.filter.base.AbstractFilter;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.function.Predicate;

/**
 * Dialog that shows a {@link MFXFilterPane} to produce a filter, {@link Predicate}.
 */
public class MFXFilterDialog<T> extends MFXGenericDialog {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-filter-dialog";
	private final MFXFilterPane<T> filterPane;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXFilterDialog(MFXFilterPane<T> filterPane) {
		this.filterPane = filterPane;
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setHeaderText("");
		setHeaderIcon(null);
		buildContent();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected void buildContent() {
		setContent(filterPane);
	}

	@Override
	protected void buildScrollableContent(boolean smoothScrolling) {
		buildContent();
	}

	@Override
	protected double computeMinWidth(double height) {
		return snappedLeftInset() + filterPane.prefWidth(-1) + snappedRightInset();
	}

	//================================================================================
	// Delegate Methods
	//================================================================================
	public Predicate<T> filter() {
		return filterPane.filter();
	}

	public ObservableList<AbstractFilter<T, ?>> getFilters() {
		return filterPane.getFilters();
	}

	public ObservableList<FilterBean<T, ?>> getActiveFilters() {
		return filterPane.getActiveFilters();
	}

	public void setOnFilter(EventHandler<MouseEvent> onFilter) {
		filterPane.setOnFilter(onFilter);
	}

	public void setOnReset(EventHandler<MouseEvent> onReset) {
		filterPane.setOnReset(onReset);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public MFXFilterPane<T> getFilterPane() {
		return filterPane;
	}
}
