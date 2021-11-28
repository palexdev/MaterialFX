package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.controls.MFXPopup;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.Region;

/**
 * JavaFX offers a special type of Popup, the {@link PopupControl}, to allow styling
 * its content with CSS. It would be a great control....if only it worked properly, but just like most of JavaFX's
 * controls it is borked too because those code monkeys at JavaFX are dumb.
 * <p></p>
 * Since I could not make it work no matter what I tried I was tired of this bullshit and designed my
 * own solution.
 * <p>
 * {@link MFXPopup} now implements this interface allowing CSS to really fucking work as it should!
 * <p></p>
 * The system is quite simple at the moment:
 * <p>
 * First when creating a {@code MFXStyleablePopup} you must set which is the parent that has the stylesheet,
 * use {@link #setPopupStyleableParent(Parent)}. Then the interface, thanks to a helper class (the {@link CSSBridge},
 * will parse the parent's user agent stylesheet and all its stylesheets automatically, even if changed at runtime (the list
 * will always be rebuilt for simplicity). Then it's up to the popup implementation to connect the bridge to the
 * node on which apply the stylesheets.
 * <p>
 * This approach is very simple yet effective (ffs even a baby could make it), but it has a limitation at the moment.
 * The stylesheets parsing is limited to the specified parent, it won't scan the entire scenegraph, I could also implement something like
 * that, it's simple, it's just needed to mimic the behavior of the JavaFX' StyleManager, but at the moment I don't think
 * it's really useful, so... we'll see in future if needed.
 */
public interface MFXStyleablePopup {

	/**
	 * @return the node that has the necessary stylesheets to customize the popup
	 */
	Parent getPopupStyleableParent();

	/**
	 * Sets the node that has the necessary stylesheets to customize the popup.
	 */
	void setPopupStyleableParent(Parent parent);

	/**
	 * @return the parsed stylesheets
	 */
	ObservableList<String> getStyleSheets();

	/**
	 * Helper class which is responsible for parsing the stylesheets for a given {@link MFXStyleablePopup}.
	 * <p>
	 * The list is automatically rebuilt if the parent's stylesheets change.
	 */
	class CSSBridge {
		//================================================================================
		// Properties
		//================================================================================
		private final MFXStyleablePopup popup;
		private final ObservableList<String> stylesheets = FXCollections.observableArrayList();
		private final InvalidationListener stylesheetsChanged = invalidated -> initializeStylesheets();

		//================================================================================
		// Constructors
		//================================================================================
		public CSSBridge(MFXStyleablePopup popup) {
			this.popup = popup;
			initializeStylesheets();
			addListeners();
		}

		//================================================================================
		// Methods
		//================================================================================

		/**
		 * Called by the constructor the first time.
		 * <p>
		 * Responsible for parsing and building the stylesheets list.
		 */
		public void initializeStylesheets() {
			stylesheets.clear();
			if (popup.getPopupStyleableParent() == null) return;
			Parent parent = popup.getPopupStyleableParent();

			if (parent instanceof Region) {
				Region region = (Region) parent;
				if (region.getUserAgentStylesheet() != null && !region.getUserAgentStylesheet().isEmpty()) {
					stylesheets.add(region.getUserAgentStylesheet());
				}
			}
			stylesheets.addAll(parent.getStylesheets());
		}

		/**
		 * Adds the listener responsible for updating the stylesheets list
		 * to the parent's stylesheets observable list.
		 */
		public void addListeners() {
			if (popup.getPopupStyleableParent() == null) return;
			popup.getPopupStyleableParent().getStylesheets().addListener(stylesheetsChanged);
		}

		/**
		 * Disposes the CSSBridge by removing the stylesheetsChanged listener.
		 */
		public void dispose() {
			if (popup.getPopupStyleableParent() != null) {
				popup.getPopupStyleableParent().getStylesheets().removeListener(stylesheetsChanged);
			}
		}

		/**
		 * @return the parsed stylesheets list
		 */
		public ObservableList<String> getStylesheets() {
			return stylesheets;
		}
	}
}
