package io.github.palexdev.materialfx.beans.binding;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class BooleanListBinding extends BooleanBinding {
    private final ObservableList<BooleanProperty> boundList;
    private final ListChangeListener<BooleanProperty> changeListener;
    private BooleanProperty[] observedProperties;

    public BooleanListBinding(ObservableList<BooleanProperty> boundList) {
        this.boundList = boundList;
        this.changeListener = c -> refreshBinding();
        this.boundList.addListener(changeListener);
        refreshBinding();
    }

    @Override
    protected boolean computeValue() {
        for (BooleanProperty bp : observedProperties) {
            if (!bp.get()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void dispose() {
        boundList.removeListener(changeListener);
        super.dispose();
    }

    private void refreshBinding() {
        super.unbind(observedProperties);
        observedProperties = boundList.toArray(new BooleanProperty[0]);
        super.bind(observedProperties);
        this.invalidate();
    }
}
