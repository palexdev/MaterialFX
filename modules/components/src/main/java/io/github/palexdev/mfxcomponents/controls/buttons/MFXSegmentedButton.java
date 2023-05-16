package io.github.palexdev.mfxcomponents.controls.buttons;

import io.github.palexdev.mfxcomponents.behaviors.MFXSegmentedButtonBehavior;
import io.github.palexdev.mfxcomponents.controls.base.MFXControl;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.layout.LayoutStrategy;
import io.github.palexdev.mfxcomponents.skins.MFXSegmentedButtonSkin;
import io.github.palexdev.mfxcomponents.skins.MFXSegmentedButtonSkin.MFXSegment;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableIntegerProperty;
import io.github.palexdev.mfxcore.collections.ObservableCircularQueue;
import io.github.palexdev.mfxcore.enums.SelectionMode;
import io.github.palexdev.mfxcore.selection.Selectable;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import io.github.palexdev.mfxcore.utils.NumberUtils;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Implementation of the segmented button shown in the MD3 guidelines. Extends {@link MFXControl} since this is a rather
 * special component. A segmented button is basically a container for a bunch of segments, buttons that can be selected.
 * So, it's not the segmented button itself to be selectable, but rather its segments.
 * <p>
 * As stated in the guidelines, a segmented buttons should have at least two segments and at max 5. This implementation
 * only follows the max constraint. After all, having a segmented button with just one segment would not make any sense.
 * <b>Beware</b>, when more than five segments are added, the first in the list will be automatically removed!
 * The structure responsible for holding the segments is an {@link ObservableCircularQueue}.
 * <p></p>
 * In MaterialFX a segmented button could also be seen as the visual implementation of a {@link SelectionGroup}.
 * And it's a quite fitting description since this uses a group to handle the selection of its segments.
 * Segmented buttons are designed for choices, for this reason, such buttons can operate in single or multiple selection
 * mode, but there must always be at least one choice selected. So, by default {@link SelectionGroup#atLeastOneSelectedProperty()}
 * is set to true.
 * <p></p>
 * About CSS: the default selector is '.mfx-segmented-button', and there's an extra styleable property, {@link #densityProperty()}.
 */
public class MFXSegmentedButton extends MFXControl<MFXSegmentedButtonBehavior> {
    //================================================================================
    // Properties
    //================================================================================
    private final ObservableCircularQueue<MFXSegment> segments = new ObservableCircularQueue<>(5);
    private final SelectionGroup selectionGroup = new SelectionGroup();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXSegmentedButton() {
        initialize();
    }

    public MFXSegmentedButton(MFXSegment... segments) {
        initialize();
        this.segments.addAll(segments);
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll(defaultStyleClasses());
        setDefaultBehaviorProvider();
        selectionGroup.setAtLeastOneSelected(true);
        segments.addListener((ListChangeListener<? super MFXSegment>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    List<? extends MFXSegment> removed = c.getRemoved();
                    removed.forEach(selectionGroup::remove);
                }
                if (c.wasAdded()) {
                    List<? extends MFXSegment> added = c.getAddedSubList();
                    added.forEach(selectionGroup::add);
                }
            }
        });
    }

    //================================================================================
    // Delegate Methods
    //================================================================================

    /**
     * Delegate for {@link SelectionGroup#getSelectionList()}.
     */
    public List<Selectable> getSelection() {
        return selectionGroup.getSelectionList();
    }

    /**
     * Delegate for {@link SelectionGroup#getFirstSelected()}.
     */
    public Optional<Selectable> getFirstSelected() {
        return selectionGroup.getFirstSelected();
    }

    public SelectionMode getSelectionMode() {
        return selectionGroup.getSelectionMode();
    }

    /**
     * Delegate for {@link SelectionGroup#selectionModeProperty()}.
     */
    public ObjectProperty<SelectionMode> selectionModeProperty() {
        return selectionGroup.selectionModeProperty();
    }

    public void setSelectionMode(SelectionMode mode) {
        selectionGroup.setSelectionMode(mode);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected MFXSkinBase<?, ?> buildSkin() {
        return new MFXSegmentedButtonSkin(this);
    }

    @Override
    public LayoutStrategy defaultLayoutStrategy() {
        return LayoutStrategy.defaultStrategy()
            .setPrefWidthFunction(LayoutStrategy.Defaults.DEF_PREF_WIDTH_FUNCTION.andThen(r -> Math.max(r, getInitWidth())))
            .setPrefHeightFunction(LayoutStrategy.Defaults.DEF_PREF_HEIGHT_FUNCTION.andThen(r -> {
                double target = getInitHeight() - getDensity() * 4;
                return Math.max(r, target);
            }));
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-segmented-button");
    }

    @Override
    public Supplier<MFXSegmentedButtonBehavior> defaultBehaviorProvider() {
        return () -> new MFXSegmentedButtonBehavior(this);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableIntegerProperty density = new StyleableIntegerProperty(
        StyleableProperties.DENSITY,
        this,
        "density",
        0
    ) {
        @Override
        public void set(int v) {
            super.set(NumberUtils.clamp(v, 0, 5));
        }
    };

    public int getDensity() {
        return density.get();
    }

    /**
     * This property can be used to 'compress' the segmented button's height. Valid values are from 0 to 5, and each step
     * will remove 4px from the prefHeight (handled by the default layout strategy).
     * <p></p>
     * Can be set in CSS via the property: '-mfx-density'.
     */
    public StyleableIntegerProperty densityProperty() {
        return density;
    }

    public void setDensity(int density) {
        this.density.set(density);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXSegmentedButton> FACTORY = new StyleablePropertyFactory<>(MFXControl.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXSegmentedButton, Number> DENSITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-density",
                MFXSegmentedButton::densityProperty,
                0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                MFXControl.getClassCssMetaData(),
                DENSITY
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /**
     * @return the observable list containing the button' segments
     */
    public ObservableList<MFXSegment> getSegments() {
        return segments;
    }

    /**
     * Adds the given segment to the button.
     */
    public void addSegment(MFXSegment segment) {
        segments.add(segment);
    }

    /**
     * Builds a new {@link MFXSegment} with the given parameters, then adds it to the button.
     */
    public void addSegment(MFXFontIcon icon, String text) {
        addSegment(new MFXSegment(text, icon));
    }

    /**
     * Builds a new {@link MFXSegment} from the given supplier, them adds it to the button.
     */
    public void addSegment(Supplier<MFXSegment> supplier) {
        addSegment(supplier.get());
    }
}
