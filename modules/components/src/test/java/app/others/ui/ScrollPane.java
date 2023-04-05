package app.others.ui;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public class ScrollPane extends javafx.scene.control.ScrollPane {

	public ScrollPane(Node content) {
		super(content);
	}

	@Override
	public ObservableList<Node> getChildren() {
		return super.getChildren();
	}
}
