package io.github.palexdev.materialfx.controls.enums;

public enum SortState {
    ASCENDING,
    DESCENDING,
    UNSORTED;

    private static final SortState[] valuesArr = values();

    /**
     * @return the next sort state
     */
    public SortState next() {
        return valuesArr[(this.ordinal() + 1) % valuesArr.length];
    }
}

