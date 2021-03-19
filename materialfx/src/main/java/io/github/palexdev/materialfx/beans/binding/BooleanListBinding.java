/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

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
