package io.github.palexdev.mfxcomponents.skins;

import io.github.palexdev.mfxcomponents.behaviors.MFXSegmentedButtonBehavior;
import io.github.palexdev.mfxcomponents.behaviors.MFXSelectableBehaviorBase;
import io.github.palexdev.mfxcomponents.controls.base.MFXSelectable;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXSegmentedButton;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxresources.base.properties.IconProperty;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.ContentDisplay;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Default skin used by {@link MFXSegmentedButton}. Extends {@link MFXSkinBase} as this is nothing more than a simple
 * container for the {@link MFXSegmentedButton#getSegments()}.
 * <p></p>
 * There are a few peculiarities worth explaining though. As shown by the MD3 guidelines, a segmented button is a
 * rounded container for a bunch of segments that are the actual buttons. When components are organized like this though,
 * there are issues with the styling. The guidelines show that the container is delimited by a border, and each segment
 * separated by a vertical line too. There are several ways to implement this with borders. However, the first and
 * last segments will need different values. The first will have the border radius only on the left, while the last will
 * have the border radius applied only on the right. No matter the implementation/theming strategy, we need a way to
 * distinguish between the first segment, the last segment and the others. For this reason we make use of two new
 * pseudo classes ':first' and ':last', these are automatically applied by {@link #updateFirstLast()}.
 * <p></p>
 * Another important detail is the width. Following the guidelines, each segment of the button must have the same width.
 * Once the segment with the max width is found, the total width will be computed as the found value multiplied by the
 * number of segments. And during layout, every segment will be resized to have that max found width.
 */
public class MFXSegmentedButtonSkin extends MFXSkinBase<MFXSegmentedButton, MFXSegmentedButtonBehavior> {
    //================================================================================
    // Properties
    //================================================================================
    private InvalidationListener segmentsChanged = i -> onSegmentsChanged();
    protected WeakReference<MFXSegment> first;
    protected WeakReference<MFXSegment> last;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXSegmentedButtonSkin(MFXSegmentedButton button) {
        super(button);

        ObservableList<MFXSegment> segments = button.getSegments();
        updateFirstLast();
        addListeners();
        getChildren().setAll(segments);
    }

    //================================================================================
    // Methods
    //================================================================================
    private void addListeners() {
        MFXSegmentedButton btn = getSkinnable();
        btn.getSegments().addListener(segmentsChanged);
    }

    /**
     * Updates the children list when {@link MFXSegmentedButton#getSegments()} change. Before doing so, this also calls
     * {@link #updateFirstLast()}.
     */
    protected void onSegmentsChanged() {
        ObservableList<MFXSegment> segments = getSkinnable().getSegments();
        updateFirstLast();
        getChildren().setAll(segments);
    }

    /**
     * This is responsible for retrieving the first and last segments from {@link MFXSegmentedButton#getSegments()} and
     * apply the ':first' and ':last' pseudo classes to them respectively.
     * It's enough to call this only when the segments list change. Also, if the list becomes empty the old segments
     * will have the relative pseudo class disabled, and if the list contains only one segment then only ':first' will be
     * applied.
     */
    protected void updateFirstLast() {
        ObservableList<MFXSegment> segments = getSkinnable().getSegments();
        if (segments.isEmpty()) {
            Optional.ofNullable(first)
                .map(Reference::get)
                .ifPresent(s -> PseudoClasses.FIRST.setOn(s, false));
            Optional.ofNullable(last)
                .map(Reference::get)
                .ifPresent(s -> PseudoClasses.LAST.setOn(s, false));
            return;
        }

        MFXSegment first = segments.get(0);
        if (!first.equals(Optional.ofNullable(this.first).map(Reference::get).orElse(null))) {
            PseudoClasses.FIRST.setOn(first, true);
            this.first = new WeakReference<>(first);
        }

        int lastIndex = segments.size() - 1;
        if (lastIndex == 0) return;

        MFXSegment last = segments.get(lastIndex);
        if (!last.equals(Optional.ofNullable(this.last).map(Reference::get).orElse(null))) {
            PseudoClasses.LAST.setOn(last, true);
            this.last = new WeakReference<>(last);
        }
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void initBehavior(MFXSegmentedButtonBehavior behavior) {
    }

    @Override
    public double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        ObservableList<MFXSegment> segments = getSkinnable().getSegments();
        double max = segments.stream()
            .mapToDouble(LayoutUtils::boundWidth)
            .max()
            .orElse(0);
        return leftInset + max * segments.size() + rightInset;
    }

    @Override
    public double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    public double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        ObservableList<MFXSegment> segments = getSkinnable().getSegments();
        double max = segments.stream()
            .mapToDouble(LayoutUtils::boundWidth)
            .max()
            .orElse(0);
        double sx = x;
        for (MFXSegment segment : segments) {
            segment.resizeRelocate(sx, 0, max, h);
            sx += max;
        }
    }

    @Override
    public void dispose() {
        MFXSegmentedButton btn = getSkinnable();
        btn.getSegments().removeListener(segmentsChanged);
        segmentsChanged = null;
        super.dispose();
    }

    //================================================================================
    // Internal Classes
    //================================================================================

    /**
     * This component is the buttons that fill a {@link MFXSegmentedButton}. It's a simple extension of {@link MFXSelectable}
     * which enforces the usage of {@link MFXFontIcon}s as the button's graphic. As the Material Design 3 guidelines
     * show, any segment in a segmented button can have an arbitrary icon, however, the moment the segment is selected
     * the icon should be set to a 'check mark'. This however is not enforced via code, but depends on the theme.
     * <p></p>
     * Its default style class is '.segment' and uses skins of type {@link MFXSegmentSkin}.
     */
    public static class MFXSegment extends MFXSelectable<MFXSelectableBehaviorBase<MFXSegment>> {
        private final IconProperty icon = new IconProperty(new MFXFontIcon()) {
            @Override
            public void set(MFXFontIcon newValue) {
                MFXFontIcon oldValue = get();
                if (newValue == null) {
                    if (oldValue != null) {
                        oldValue.setDescription("");
                        return;
                    }
                    newValue = new MFXFontIcon();
                }
                super.set(newValue);
            }
        };

        public MFXSegment() {
            initialize();
        }

        public MFXSegment(String text) {
            super(text);
            setIcon(new MFXFontIcon());
            initialize();
        }

        public MFXSegment(String text, MFXFontIcon icon) {
            super(text);
            setIcon(icon);
            initialize();
        }

        private void initialize() {
            graphicProperty().bind(iconProperty());
        }

        @Override
        protected MFXSkinBase<?, ?> buildSkin() {
            return new MFXSegmentSkin(this);
        }

        @Override
        public List<String> defaultStyleClasses() {
            return List.of("segment");
        }

        @Override
        public Supplier<MFXSelectableBehaviorBase<MFXSegment>> defaultBehaviorProvider() {
            return () -> new MFXSelectableBehaviorBase<>(this);
        }

        public MFXFontIcon getIcon() {
            return icon.get();
        }

        public IconProperty iconProperty() {
            return icon;
        }

        public void setIcon(MFXFontIcon icon) {
            this.icon.set(icon);
        }
    }

    /**
     * Default skin used by {@link MFXSegment} and simple extension of {@link MFXButtonSkin}.
     * <p>
     * What changes is the layout strategy. According to MD3 guidelines a segment's label (text + icon) is always centered.
     */
    public static class MFXSegmentSkin extends MFXButtonSkin<MFXSegment, MFXSelectableBehaviorBase<MFXSegment>> {

        public MFXSegmentSkin(MFXSegment button) {
            super(button);
            initTextMeasurementCache();
        }

        @Override
        protected void addListeners() {
        }

        @Override
        public double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            MFXSegment segment = getSkinnable();
            MFXFontIcon icon = segment.getIcon();
            double insets = leftInset + rightInset;
            double tW = tmCache.getSnappedWidth();
            if (segment.getContentDisplay() == ContentDisplay.GRAPHIC_ONLY) tW = 0;
            double iW = Math.max(LayoutUtils.boundWidth(icon), icon.getSize()) + segment.getGraphicTextGap();
            return insets + tW + iW;
        }

        @Override
        public double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            MFXSegment segment = getSkinnable();
            MFXFontIcon icon = segment.getIcon();
            double insets = topInset + bottomInset;
            double iH = Math.max(LayoutUtils.boundHeight(icon), icon.getSize());
            double tH = tmCache.getSnappedHeight();
            return insets + Math.max(iH, tH);
        }

        @Override
        public double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return getSkinnable().prefWidth(height);
        }

        @Override
        public double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return getSkinnable().prefHeight(width);
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            MFXSegment segment = getSkinnable();
            MFXFontIcon icon = segment.getIcon();
            double gap = segment.getGraphicTextGap();
            if (icon.getDescription() == null || icon.getDescription().isBlank()) {
                double lW = tmCache.getSnappedWidth() + gap;
                double lH = LayoutUtils.boundHeight(label);
                label.resize(lW, lH);
                Position lPos = LayoutUtils.computePosition(
                    segment, label,
                    x, y, w, h, 0,
                    Insets.EMPTY, HPos.CENTER, VPos.CENTER,
                    true, false
                );
                label.relocate(snapPositionX(lPos.getX() - gap / 2), lPos.getY());
            } else {
                layoutInArea(label, x, y, w, h, 0, HPos.CENTER, VPos.CENTER);
            }
            surface.resizeRelocate(0, 0, segment.getWidth(), segment.getHeight());
        }

        @Override
        public void dispose() {
            tmCache.dispose();
            tmCache = null;
            surface.dispose();
            label.getTextNode().ifPresent(n -> n.opacityProperty().unbind());
            super.dispose();
        }
    }
}
