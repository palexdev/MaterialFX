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

package io.github.palexdev.materialfx.controls.flowless;

import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

/**
 * Acts as an intermediate class between {@link VirtualizedScrollPane} and
 * its {@link Virtualized} content in that it scales the content without
 * also scaling the ScrollPane's scroll bars.
 * <pre>
 *     {@code
 *     Virtualized actualContent = // creation code
 *     ScaledVirtualized<Virtualized> wrapper = new ScaledVirtualized(actualContent);
 *     VirtualizedScrollPane<ScaledVirtualized> vsPane = new VirtualizedScrollPane(wrapper);
 *
 *     // To scale actualContent without also scaling vsPane's scrollbars:
 *     wrapper.scaleProperty().setY(3);
 *     wrapper.scaleProperty().setX(2);
 *     }
 * </pre>
 *
 * @param <V> the {@link Virtualized} content to be scaled when inside a {@link VirtualizedScrollPane}
 */
public class ScaledVirtualized<V extends Node & Virtualized> extends Region implements Virtualized {
    private final V content;
    private final Scale zoom = new Scale();

    private final Val<Double> estHeight;
    private final Val<Double> estWidth;
    private final Var<Double> estScrollX;
    private final Var<Double> estScrollY;

    public ScaledVirtualized(V content) {
        super();
        this.content = content;
        getChildren().add(content);
        getTransforms().add(zoom);

        estHeight = Val.combine(
                content.totalHeightEstimateProperty(),
                zoom.yProperty(),
                (estHeight, scaleFactor) -> estHeight * scaleFactor.doubleValue()
        );
        estWidth = Val.combine(
                content.totalWidthEstimateProperty(),
                zoom.xProperty(),
                (estWidth, scaleFactor) -> estWidth * scaleFactor.doubleValue()
        );
        estScrollX = Var.mapBidirectional(
                content.estimatedScrollXProperty(),
                scrollX -> scrollX * zoom.getX(),
                scrollX -> scrollX / zoom.getX()
        );
        estScrollY = Var.mapBidirectional(
                content.estimatedScrollYProperty(),
                scrollY -> scrollY * zoom.getY(),
                scrollY -> scrollY / zoom.getY()
        );

        zoom.xProperty().addListener((obs, ov, nv) -> requestLayout());
        zoom.yProperty().addListener((obs, ov, nv) -> requestLayout());
        zoom.zProperty().addListener((obs, ov, nv) -> requestLayout());
        zoom.pivotXProperty().addListener((obs, ov, nv) -> requestLayout());
        zoom.pivotYProperty().addListener((obs, ov, nv) -> requestLayout());
        zoom.pivotZProperty().addListener((obs, ov, nv) -> requestLayout());
    }

    @Override
    protected void layoutChildren() {
        double width = getLayoutBounds().getWidth();
        double height = getLayoutBounds().getHeight();
        content.resize(width / zoom.getX(), height / zoom.getY());
    }

    @Override
    public Var<Double> estimatedScrollXProperty() {
        return estScrollX;
    }

    @Override
    public Var<Double> estimatedScrollYProperty() {
        return estScrollY;
    }

    @Override
    public Val<Double> totalHeightEstimateProperty() {
        return estHeight;
    }

    @Override
    public Val<Double> totalWidthEstimateProperty() {
        return estWidth;
    }

    @Override
    public void scrollXBy(double deltaX) {
        content.scrollXBy(deltaX);
    }

    @Override
    public void scrollYBy(double deltaY) {
        content.scrollYBy(deltaY);
    }

    @Override
    public void scrollXToPixel(double pixel) {
        content.scrollXToPixel(pixel);
    }

    @Override
    public void scrollYToPixel(double pixel) {
        content.scrollYToPixel(pixel);
    }

    /**
     * The {@link Scale} object that scales the virtualized content: named "zoom"
     * to prevent confusion with {@link Node#getScaleX()}, etc. Not to be confused
     * with {@link Node#getOnZoom()} or similar methods/objects.
     */
    public Scale getZoom() {
        return zoom;
    }
}
