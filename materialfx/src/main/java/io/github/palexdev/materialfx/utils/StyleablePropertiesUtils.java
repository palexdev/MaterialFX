package io.github.palexdev.materialfx.utils;

import javafx.css.CssMetaData;
import javafx.css.Styleable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StyleablePropertiesUtils {

    private StyleablePropertiesUtils() {
    }

    // TODO replace everywhere
    @SafeVarargs
    public static List<CssMetaData<? extends Styleable, ?>> cssMetaDataList(List<CssMetaData<? extends Styleable, ?>> styleable, CssMetaData<? extends Styleable, ?>... cssMetaData) {
        CssMetaDataList styleableMetaData = new CssMetaDataList(styleable);
        styleableMetaData.addAll(cssMetaData);
        return styleableMetaData.toUnmodifiable();
    }

    public static class CssMetaDataList extends ArrayList<CssMetaData<? extends Styleable, ?>> {
        public CssMetaDataList() {
        }

        public CssMetaDataList(Collection<? extends CssMetaData<? extends Styleable, ?>> c) {
            super(c);
        }

        @SafeVarargs
        public final boolean addAll(CssMetaData<? extends Styleable, ?>... cssMetaData) {
            return Collections.addAll(this, cssMetaData);
        }

        public List<CssMetaData<? extends Styleable, ?>> toUnmodifiable() {
            return Collections.unmodifiableList(this);
        }
    }
}
