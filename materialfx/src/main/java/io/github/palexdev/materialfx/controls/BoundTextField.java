package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.bindings.BidirectionalBindingHelper;
import io.github.palexdev.materialfx.bindings.BindingManager;
import javafx.beans.property.Property;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;

// TODO documentation
public class BoundTextField extends TextField {

	public BoundTextField(TextField textField) {
		// Initialization
		setPromptText(textField.getPromptText());
		setText(textField.getText());
		setFont(textField.getFont());
		setEditable(textField.isEditable());
		setAlignment(textField.getAlignment());
		setTextFormatter(textField.getTextFormatter());

		// Binding
		promptTextProperty().bind(textField.promptTextProperty());
		textProperty().bindBidirectional(textField.textProperty());
		fontProperty().bind(textField.fontProperty());
		editableProperty().bind(textField.editableProperty());
		alignmentProperty().bind(textField.alignmentProperty());
		textFormatterProperty().bind(textField.textFormatterProperty());

		// TODO can it be done in a functional/fluent way?
/*        BindingManager<IndexRange> bindingManager = new BindingManager<>();
        bindingManager.provideHelperFactory(observable -> new BindingHelper<>() {
            @Override
            protected void updateBound(IndexRange value) {
                textField.selectRange(value.getStart(), value.getEnd());
            }
        });
        bindingManager.getBindingHelper(textField.selectionProperty()).bind(selectionProperty());*/

		BindingManager<IndexRange> bindingManager = new BindingManager<>();
		bindingManager.provideBidirectionalHelperFactory(observable -> new BidirectionalBindingHelper<>(observable) {
			@Override
			protected void updateThis(IndexRange newValue) {
				selectRange(newValue.getStart(), newValue.getEnd());
			}

			@Override
			protected void updateOther(Property<IndexRange> other, IndexRange newValue) {
				textField.selectRange(newValue.getStart(), newValue.getEnd());
			}
		});
	}
}
