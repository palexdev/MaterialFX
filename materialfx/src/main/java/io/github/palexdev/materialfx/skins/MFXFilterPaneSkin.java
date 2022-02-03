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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.beans.BiPredicateBean;
import io.github.palexdev.materialfx.beans.FilterBean;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.dialogs.MFXDialogs;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.dialogs.MFXStageDialogBuilder;
import io.github.palexdev.materialfx.effects.ripple.RippleClipType;
import io.github.palexdev.materialfx.enums.ChainMode;
import io.github.palexdev.materialfx.enums.FloatMode;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.factories.RippleClipTypeFactory;
import io.github.palexdev.materialfx.filter.BooleanFilter;
import io.github.palexdev.materialfx.filter.EnumFilter;
import io.github.palexdev.materialfx.filter.base.AbstractFilter;
import io.github.palexdev.materialfx.filter.base.NumberFilter;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.i18n.I18N;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;

/**
 * This is the skin associated with every {@link MFXFilterPane}.
 * <p></p>
 * The control is made of four parts:
 * <p> - The header which contains a label, and two icons to confirm the filter or reset the filter pane
 * <p> - The filter builder which contains the necessary controls to produce active filters
 * <p> - A label which acts as a separator
 * <p> - A {@link FlowPane} to show the currently built active filters
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class MFXFilterPaneSkin<T> extends SkinBase<MFXFilterPane<T>> {
	//================================================================================
	// Properties
	//================================================================================
	private final VBox container;
	private final Region filterBuilder;
	private final MFXScrollPane filtersContainer;
	private final FlowPane activeFiltersPane;

	private final MFXGenericDialog dialogContent;
	private final MFXStageDialog dialog;
	private final StringProperty query = new SimpleStringProperty();
	private boolean avoidQueryReset = false;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXFilterPaneSkin(MFXFilterPane<T> filterPane) {
		super(filterPane);

		dialogContent = MFXDialogs.error()
				.setShowAlwaysOnTop(false)
				.get();
		dialog = MFXStageDialogBuilder.build()
				.setContent(dialogContent)
				.initOwner(filterPane.getScene().getWindow())
				.initModality(Modality.WINDOW_MODAL)
				.get();

		Region header = buildHeader();

		Label filtersLabel = new Label(I18N.getOrDefault("filterPane.activeFilters"));
		filtersLabel.getStyleClass().add("header-label");
		VBox.setMargin(filtersLabel, InsetsFactory.top(15));

		filterBuilder = buildFilterBuilder();
		activeFiltersPane = buildActiveFilters();

		filtersContainer = new MFXScrollPane(activeFiltersPane) {
			@Override
			public String getUserAgentStylesheet() {
				return filterPane.getUserAgentStylesheet();
			}
		};
		filtersContainer.setFitToWidth(true);
		filtersContainer.maxWidthProperty().bind(filterPane.widthProperty());

		container = new VBox(10, header, filterBuilder, filtersLabel, filtersContainer);
		getChildren().setAll(container);
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void addListeners() {
		MFXFilterPane<T> filterPane = getSkinnable();
		filterPane.getActiveFilters().addListener((InvalidationListener) invalidated -> updateFilters());
	}

	/**
	 * For each filter in the {@link MFXFilterPane#getActiveFilters()} list builds
	 * a node that represents the filter. Filters are spaced by another node
	 * which represents how to filters are chained, by clicking on this icon the
	 * chain mode can be changed.,
	 *
	 * @see #buildFilter(FilterBean)
	 * @see #buildAndOrIcon(FilterBean)
	 */
	protected void updateFilters() {
		MFXFilterPane<T> filterPane = getSkinnable();
		ObservableList<FilterBean<T, ?>> filters = filterPane.getActiveFilters();
		ObservableList<Node> children = FXCollections.observableArrayList();

		for (int i = 0; i < filters.size(); i++) {
			int previous = i - 1;
			FilterBean<T, ?> filter;
			if (previous >= 0) {
				filter = filters.get(previous);
				children.add(buildAndOrIcon(filter));
			}

			filter = filters.get(i);
			children.add(buildFilter(filter));
		}

		activeFiltersPane.getChildren().setAll(children);
	}

	/**
	 * Builds a node that represents the given {@link FilterBean}.
	 * <p>
	 * It is composed by:
	 * <p> - A label that shows the object's data type/name
	 * <p> - A label that shows which function is used to compare the input with the object's field value
	 * <p> - A label that shows which is the input
	 * <p> - An icon to remove the filter
	 */
	protected Region buildFilter(FilterBean<T, ?> filter) {
		Label filterLabel = new Label(filter.getFilterName());
		Label functionLabel = new Label(filter.getPredicateName());
		Label queryLabel = new Label(filter.getQuery());
		functionLabel.getStyleClass().add("function-text");
		MFXFontIcon remove = new MFXFontIcon("mfx-x-alt", 12);
		remove.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> getSkinnable().getActiveFilters().remove(filter));
		HBox.setMargin(remove, InsetsFactory.top(2));

		HBox container = new HBox(15, filterLabel, functionLabel, queryLabel, remove);
		container.getStyleClass().add("active-filter");
		container.setAlignment(Pos.CENTER);
		return container;
	}

	/**
	 * Builds an icon that represents how two {@link FilterBean}s should be chained.
	 * Clicking on the icon switches the chain mode.
	 */
	protected Node buildAndOrIcon(FilterBean<T, ?> filter) {
		Text modeText = new Text(filter.getMode().text());
		modeText.getStyleClass().add("and-or-text");
		modeText.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (filter.getMode() == ChainMode.AND) {
				filter.setMode(ChainMode.OR);
				modeText.setText(ChainMode.OR.text());
			} else {
				filter.setMode(ChainMode.AND);
				modeText.setText(ChainMode.AND.text());
			}
		});
		return modeText;
	}

	/**
	 * Builds the header.
	 */
	protected Region buildHeader() {
		MFXFilterPane<T> filterPane = getSkinnable();

		Label headerLabel = new Label();
		headerLabel.getStyleClass().add("header-label");
		headerLabel.textProperty().bind(filterPane.headerTextProperty());
		headerLabel.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(headerLabel, Priority.ALWAYS);

		MFXIconWrapper filter = new MFXIconWrapper("mfx-variant7-mark", 16, 28).defaultRippleGeneratorBehavior();
		MFXIconWrapper reset = new MFXIconWrapper("mfx-undo", 16, 28).defaultRippleGeneratorBehavior();

		filter.setId("filterIcon");
		reset.setId("resetIcon");

		NodeUtils.makeRegionCircular(filter);
		NodeUtils.makeRegionCircular(reset);

		filter.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> filterPane.getOnFilter().handle(event));
		reset.addEventHandler(MouseEvent.MOUSE_CLICKED, this::reset);

		HBox box = new HBox(5, headerLabel, filter, reset);
		box.setAlignment(Pos.CENTER_LEFT);
		box.getStyleClass().add("header");
		return box;
	}

	/**
	 * Builds the filter builder region.
	 */
	protected Region buildFilterBuilder() {
		MFXFilterPane<T> filterPane = getSkinnable();

		ListProperty<BiPredicateBean<?, ?>> predicates = new SimpleListProperty<>(FXCollections.observableArrayList());

		MFXTextField searchField = new MFXTextField() {
			@Override
			public String getUserAgentStylesheet() {
				return filterPane.getUserAgentStylesheet();
			}
		};
		searchField.setFloatMode(FloatMode.DISABLED);
		searchField.setPromptText(I18N.getOrDefault("filterPane.searchField"));
		searchField.textProperty().bindBidirectional(query);

		MFXComboBox<Object> enumsCombo = new MFXComboBox<>() {
			@Override
			public String getUserAgentStylesheet() {
				return filterPane.getUserAgentStylesheet();
			}
		};
		enumsCombo.setFloatMode(FloatMode.DISABLED);
		enumsCombo.valueProperty().addListener((observable, oldValue, newValue) -> setQuery(newValue.toString()));
		enumsCombo.setManaged(false);
		enumsCombo.setVisible(false);

		MFXComboBox<Boolean> booleansCombo = new MFXComboBox<>(FXCollections.observableArrayList(true, false)) {
			@Override
			public String getUserAgentStylesheet() {
				return filterPane.getUserAgentStylesheet();
			}
		};
		booleansCombo.setFloatMode(FloatMode.DISABLED);
		booleansCombo.valueProperty().addListener((observable, oldValue, newValue) -> setQuery(newValue.toString()));
		booleansCombo.setManaged(false);
		booleansCombo.setVisible(false);

		MFXComboBox<BiPredicateBean<?, ?>> predicatesCombo = new MFXComboBox<>(predicates) {
			@Override
			public String getUserAgentStylesheet() {
				return filterPane.getUserAgentStylesheet();
			}
		};
		predicatesCombo.setFloatMode(FloatMode.DISABLED);
		predicatesCombo.getStyleClass().add("predicates-combo");

		MFXComboBox<AbstractFilter<T, ?>> filterCombo = new MFXComboBox<>(filterPane.getFilters()) {
			@Override
			public String getUserAgentStylesheet() {
				return filterPane.getUserAgentStylesheet();
			}
		};
		filterCombo.setFloatMode(FloatMode.DISABLED);
		filterCombo.getStyleClass().add("filter-combo");
		filterCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
			setQuery("");
			predicatesCombo.selectFirst();
			predicates.setAll(newValue.getPredicates());

			if (newValue instanceof EnumFilter) {
				avoidQueryReset = true;
				EnumFilter enumFilter = (EnumFilter) filterCombo.getValue();
				enumsCombo.setItems(FXCollections.observableArrayList(enumFilter.getEnumType().getEnumConstants()));
				enumsCombo.setVisible(true);
				booleansCombo.setVisible(false);
				searchField.setVisible(false);
			} else if (newValue instanceof BooleanFilter) {
				avoidQueryReset = true;
				booleansCombo.setVisible(true);
				enumsCombo.setVisible(false);
				searchField.setVisible(false);
			} else {
				avoidQueryReset = false;
				enumsCombo.setVisible(false);
				booleansCombo.setVisible(false);
				searchField.setVisible(true);
			}
		});
		filterCombo.selectFirst();

		MFXButton addButton = new MFXButton(I18N.getOrDefault("filterPane.addFilter")) {
			@Override
			public String getUserAgentStylesheet() {
				return filterPane.getUserAgentStylesheet();
			}
		};
		addButton.setOnAction(event -> {
			if (filterCombo.getSelectedItem() != null
					&& predicatesCombo.getSelectedItem() != null
					&& !searchField.getText().isEmpty()
			) {
				AbstractFilter<T, ?> selected = filterCombo.getValue();
				selected.setSelectedPredicateIndex(predicatesCombo.getSelectedIndex());
				FilterBean<T, ?> predicate = selected.toFilterBean(getQuery());

				if (queryValidation(selected)) {
					filterPane.getActiveFilters().add(predicate);
				}
			}
		});
		addButton.getRippleGenerator().setClipSupplier(() -> new RippleClipTypeFactory(RippleClipType.ROUNDED_RECTANGLE).setArcs(30).build(addButton));

		HBox container = new HBox(10, filterCombo, predicatesCombo, searchField, enumsCombo, booleansCombo, addButton) {
			@Override
			protected void layoutChildren() {
				super.layoutChildren();

				double w = searchField.getBoundsInParent().getWidth();
				double h = searchField.getBoundsInParent().getHeight();
				double x = searchField.getBoundsInParent().getMinX();
				double y = searchField.getBoundsInParent().getMinY();
				enumsCombo.resizeRelocate(x, y, w, h);
				booleansCombo.resizeRelocate(x, y, w, h);
			}
		};
		container.setAlignment(Pos.CENTER_LEFT);
		return container;
	}

	/**
	 * Builds tha active filters flow pane.
	 */
	protected FlowPane buildActiveFilters() {
		MFXFilterPane<T> filterPane = getSkinnable();
		FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL, 10, 10);
		flowPane.setAlignment(Pos.TOP_LEFT);
		flowPane.prefWrapLengthProperty().bind(filterPane.widthProperty());
		flowPane.setPadding(InsetsFactory.bottom(7));
		return flowPane;
	}

	/**
	 * Validates the given query. This is needed for Numbers since the input is a
	 * String and there's no guarantee that the typed text represents a number.
	 * <p></p>
	 * Returns true if the input is valid otherwise shows an {@link MFXGenericDialog} and returns false.
	 */
	@SuppressWarnings("unused")
	protected boolean queryValidation(AbstractFilter<T, ?> filter) {
		String name = "";

		try {
			if (filter instanceof NumberFilter) {
				NumberFilter<T, ?> numberFilter = (NumberFilter<T, ?>) filter;
				name = numberFilter.name();
				Number parsed = numberFilter.getValue(getQuery());
			}
			return true;
		} catch (Exception ex) {
			String title = "Failed to parse: " + name;
			dialogContent.setHeaderText(title);
			dialogContent.setContentText(ex.getMessage());
			dialog.showDialog();
			return false;
		}
	}

	/**
	 * Resets the filter pane.
	 */
	protected void reset(MouseEvent event) {
		MFXFilterPane<T> filterPane = getSkinnable();
		if (!avoidQueryReset) setQuery("");
		filterPane.getActiveFilters().clear();
		filterPane.getOnReset().handle(event);
	}

	public String getQuery() {
		return query.get();
	}

	public void setQuery(String query) {
		this.query.set(query);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return leftInset + filterBuilder.prefWidth(-1) + rightInset;
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(-1);
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefHeight(-1);
	}
}
