package io.github.palexdev.materialfx.controls;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class acts as a {@link ToggleGroup} but for {@link MFXTitledPane}s.
 */
public class ExpandGroup {
	private Set<Node> prevPanes = new HashSet<>();
	private final ReadOnlyObjectWrapper<MFXTitledPane> expandedPane = new ReadOnlyObjectWrapper<>() {
		@Override
		public void set(MFXTitledPane newValue) {
			if (isBound()) {
				throw new RuntimeException("A bound value cannot be set.");
			}
			MFXTitledPane old = get();
			if (old == newValue) return;

			if (setExpanded(newValue, true) ||
					(newValue != null && newValue.getExpandGroup() == ExpandGroup.this) ||
					(newValue == null)) {
				if (old == null || old.getExpandGroup() == ExpandGroup.this || !old.isExpanded())
					setExpanded(old, false);
				super.set(newValue);
			}
		}
	};
	private final ObservableList<MFXTitledPane> panes = FXCollections.observableArrayList();

	public ExpandGroup() {
		panes.addListener((ListChangeListener<? super MFXTitledPane>) c -> {
			while (c.next()) {
				List<? extends MFXTitledPane> addedList = c.getAddedSubList();

				for (MFXTitledPane removed : c.getRemoved()) {
					if (removed.isExpanded()) {
						setExpandedPane(null);
					}

					if (!addedList.contains(removed)) {
						removed.setExpandGroup(null);
					}
				}

				for (MFXTitledPane added : addedList) {
					if (prevPanes.contains(added))
						throw new IllegalArgumentException("Duplicate panes are not allowed!");
					if (!this.equals(added.getExpandGroup())) {
						if (added.getExpandGroup() != null) added.getExpandGroup().getPanes().remove(added);
						added.setExpandGroup(this);
					}
				}

				for (MFXTitledPane added : addedList) {
					if (added.isExpanded()) {
						setExpandedPane(added);
						break;
					}
				}
			}
			prevPanes = new HashSet<>(c.getList());
		});
	}

	private boolean setExpanded(MFXTitledPane pane, boolean expanded) {
		if (pane != null &&
				pane.getExpandGroup() == this &&
				!pane.expandedProperty().isBound()) {
			pane.setExpanded(expanded);
			return true;
		}
		return false;
	}

	public final void clearExpandedPane() {
		if (!getExpandedPane().isExpanded()) {
			for (MFXTitledPane pane : getPanes()) {
				if (pane.isExpanded()) return;
			}
		}
		setExpandedPane(null);
	}

	public MFXTitledPane getExpandedPane() {
		return expandedPane.get();
	}

	public ReadOnlyObjectProperty<MFXTitledPane> expandedPaneProperty() {
		return expandedPane.getReadOnlyProperty();
	}

	public final void setExpandedPane(MFXTitledPane expandedPane) {
		this.expandedPane.set(expandedPane);
	}

	public ObservableList<MFXTitledPane> getPanes() {
		return FXCollections.unmodifiableObservableList(panes);
	}

	public static void addToGroup(ExpandGroup group, MFXTitledPane... panes) {
		for (MFXTitledPane pane : panes) {
			pane.setExpandGroup(group);
		}
	}
}
