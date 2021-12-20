package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;
import io.github.palexdev.materialfx.controls.MFXPagination;
import io.github.palexdev.materialfx.controls.MFXTableRow;
import io.github.palexdev.materialfx.utils.AnimationUtils.PauseBuilder;
import io.github.palexdev.virtualizedfx.flow.simple.SimpleVirtualFlow;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

/**
 * This is the default skin implementation for {@link MFXPaginatedTableView}.
 * <p><
 * Extends {@link MFXTableViewSkin} and just modifies the footer node to add a
 * {@link MFXPagination} control to it, responsible for changing the current page.
 * <p></p>
 * Little side note as a reminder too:
 * <p>
 * The layoutChildren(...) method has been overridden because it's the best place to properly
 * initialize the virtual flow minHeight property, since it's needed to wait until the cellHeight
 * is retrieved. It's also needed to move the viewport to the current page (if not 1), and for
 * some reason it's needed to delay the update with a {@link PauseTransition} otherwise an exception
 * is thrown.
 */
public class MFXPaginatedTableViewSkin<T> extends MFXTableViewSkin<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final MFXPagination pagination;
	private boolean init = false;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXPaginatedTableViewSkin(MFXPaginatedTableView<T> tableView, SimpleVirtualFlow<T, MFXTableRow<T>> rowsFlow) {
		super(tableView, rowsFlow);

		pagination = new MFXPagination();
		pagination.pagesToShowProperty().bind(tableView.pagesToShowProperty());
		pagination.maxPageProperty().bind(tableView.maxPageProperty());
		pagination.setCurrentPage(tableView.getCurrentPage());
		tableView.currentPageProperty().bindBidirectional(pagination.currentPageProperty());

		container.getChildren().remove(footer);
		container.getChildren().add(buildFooter());
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected StackPane buildFooter() {
		StackPane footer = super.buildFooter();
		if (pagination == null) return footer;
		footer.getChildren().add(pagination);
		StackPane.setAlignment(pagination, Pos.CENTER);
		return footer;
	}

	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		super.layoutChildren(contentX, contentY, contentWidth, contentHeight);

		if (!init && rowsFlow.getCellHeight() != 0) {
			MFXPaginatedTableView<T> tableView = (MFXPaginatedTableView<T>) getSkinnable();
			rowsFlow.minHeightProperty().bind(Bindings.createDoubleBinding(
					() -> tableView.getRowsPerPage() * rowsFlow.getCellHeight(),
					tableView.rowsPerPageProperty()
			));

			int current = tableView.getCurrentPage();
			if (current != 1) {
				PauseBuilder.build()
						.setDuration(20)
						.setOnFinished(event -> tableView.goToPage(current))
						.getAnimation().play();
			}

			init = true;
		}
	}
}
