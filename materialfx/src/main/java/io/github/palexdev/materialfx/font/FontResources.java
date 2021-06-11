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

package io.github.palexdev.materialfx.font;

/**
 * Enumerator class for MaterialFX font resources. (Count: 81)
 */
public enum FontResources {
    ANGLE_DOWN("mfx-angle-down", '\uE900'),
    ANGLE_LEFT("mfx-angle-left", '\uE901'),
    ANGLE_RIGHT("mfx-angle-right", '\uE902'),
    ANGLE_UP("mfx-angle-up", '\uE903'),
    ARROW_BACK("mfx-arrow-back", '\uE904'),
    ARROW_FORWARD("mfx-arrow-forward", '\uE905'),
    BACK("mfx-back", '\uE906'),
    CALENDAR_BLACK("mfx-calendar-black", '\uE907'),
    CALENDAR_SEMI_BLACK("mfx-calendar-semi-black", '\uE908'),
    CALENDAR_WHITE("mfx-calendar-white", '\uE909'),
    CARET_DOWN("mfx-caret-down", '\uE90A'),
    CARET_LEFT("mfx-caret-left", '\uE90B'),
    CARET_RIGHT("mfx-caret-right", '\uE90C'),
    CARET_UP("mfx-caret-up", '\uE90D'),
    CASPIAN_MARK("mfx-caspian-mark", '\uE90E'),
    CHART_PIE("mfx-chart-pie", '\uE90F'),
    CHECK_CIRCLE("mfx-check-circle", '\uE910'),
    CHEVRON_DOWN("mfx-chevron-down", '\uE911'),
    CHEVRON_LEFT("mfx-chevron-left", '\uE912'),
    CHEVRON_RIGHT("mfx-chevron-right", '\uE913'),
    CHEVRON_UP("mfx-chevron-up", '\uE914'),
    CIRCLE("mfx-circle", '\uE915'),
    CONTENT_COPY("mfx-content-copy", '\uE916'),
    CONTENT_CUT("mfx-content-cut", '\uE917'),
    CONTENT_PASTE("mfx-content-paste", '\uE918'),
    DASHBOARD("mfx-dashboard", '\uE919'),
    DEBUG("mfx-debug", '\uE91A'),
    DELETE("mfx-delete", '\uE91B'),
    DELETE_ALT("mfx-delete-alt", '\uE91C'),
    EXCLAMATION_CIRCLE("mfx-exclamation-circle", '\uE91D'),
    EXCLAMATION_TRIANGLE("mfx-exclamation-triangle", '\uE91E'),
    EXPAND("mfx-expand", '\uE91F'),
    EYE("mfx-eye", '\uE920'),
    EYE_SLASH("mfx-eye-slash", '\uE921'),
    FILTER("mfx-filter", '\uE922'),
    FILTER_ALT("mfx-filter-alt", '\uE923'),
    FILTER_ALT_CLEAR("mfx-filter-alt-clear", '\uE924'),
    FIRST_PAGE("mfx-first-page", '\uE925'),
    FIT("mfx-fit", '\uE926'),
    GEAR("mfx-gear", '\uE927'),
    GOOGLE("mfx-google", '\uE928'),
    GOOGLE_DRIVE("mfx-google-drive", '\uE929'),
    HOME("mfx-home", '\uE92A'),
    INFO("mfx-info", '\uE92B'),
    INFO_CIRCLE("mfx-info-circle", '\uE92C'),
    LAST_PAGE("mfx-last-page", '\uE92D'),
    LEVEL_UP("mfx-level-up", '\uE92E'),
    LOCK("mfx-lock", '\uE92F'),
    LOCK_OPEN("mfx-lock-open", '\uE930'),
    MINUS("mfx-minus", '\uE931'),
    MINUS_CIRCLE("mfx-minus-circle", '\uE932'),
    MODENA_MARK("mfx-modena-mark", '\uE933'),
    NEXT("mfx-next", '\uE934'),
    REDO("mfx-redo", '\uE935'),
    RESTORE("mfx-restore", '\uE936'),
    SEARCH("mfx-search", '\uE937'),
    SEARCH_PLUS("mfx-search-plus", '\uE938'),
    SELECT_ALL("mfx-select-all", '\uE939'),
    SLIDERS("mfx-sliders", '\uE93A'),
    STEP_BACKWARD("mfx-step-backward", '\uE93B'),
    STEP_FORWARD("mfx-step-forward", '\uE93C'),
    SYNC("mfx-sync", '\uE93D'),
    SYNC_LIGHT("mfx-sync-light", '\uE93E'),
    UNDO("mfx-undo", '\uE93F'),
    USER("mfx-user", '\uE940'),
    USERS("mfx-users", '\uE941'),
    VARIANT10_MARK("mfx-variant10-mark", '\uE942'),
    VARIANT11_MARK("mfx-variant11-mark", '\uE943'),
    VARIANT12_MARK("mfx-variant12-mark", '\uE944'),
    VARIANT3_MARK("mfx-variant3-mark", '\uE945'),
    VARIANT4_MARK("mfx-variant4-mark", '\uE946'),
    VARIANT5_MARK("mfx-variant5-mark", '\uE947'),
    VARIANT6_MARK("mfx-variant6-mark", '\uE948'),
    VARIANT7_MARK("mfx-variant7-mark", '\uE949'),
    VARIANT8_MARK("mfx-variant8-mark", '\uE94A'),
    VARIANT9_MARK("mfx-variant9-mark", '\uE94B'),
    X("mfx-x", '\uE94C'),
    X_ALT("mfx-x-alt", '\uE94D'),
    X_CIRCLE("mfx-x-circle", '\uE94E'),
    X_CIRCLE_LIGHT("mfx-x-circle-light", '\uE94F'),
    X_LIGHT("mfx-x-light", '\uE950'),
    ;

    public static FontResources findByDescription(String description) {
        for (FontResources font : values()) {
            if (font.getDescription().equals(description)) {
                return font;
            }
        }
        throw new IllegalArgumentException("Icon description '" + description + "' is invalid!");
    }

    private final String description;
    private final char code;

    FontResources(String description, char code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public char getCode() {
        return code;
    }

}
