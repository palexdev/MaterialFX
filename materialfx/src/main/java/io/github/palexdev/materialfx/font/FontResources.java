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
 * Enumerator class for MaterialFX font resources. (Count: 69)
 */
public enum FontResources {
    ANGLE_DOWN("mfx-angle-down", '\uE900'),
    ANGLE_LEFT("mfx-angle-left", '\uE901'),
    ANGLE_RIGHT("mfx-angle-right", '\uE902'),
    ANGLE_UP("mfx-angle-up", '\uE903'),
    ARROW_BACK("mfx-arrow-back", '\uE904'),
    ARROW_FORWARD("mfx-arrow-forward", '\uE905'),
    CALENDAR_BLACK("mfx-calendar-black", '\uE906'),
    CALENDAR_SEMI_BLACK("mfx-calendar-semi-black", '\uE907'),
    CALENDAR_WHITE("mfx-calendar-white", '\uE908'),
    CARET_DOWN("mfx-caret-down", '\uE909'),
    CARET_LEFT("mfx-caret-left", '\uE90A'),
    CARET_RIGHT("mfx-caret-right", '\uE90B'),
    CARET_UP("mfx-caret-up", '\uE90C'),
    CASPIAN_MARK("mfx-caspian-mark", '\uE90D'),
    CHART_PIE("mfx-chart-pie", '\uE90E'),
    CHECK_CIRCLE("mfx-check-circle", '\uE90F'),
    CHEVRON_DOWN("mfx-chevron-down", '\uE910'),
    CHEVRON_LEFT("mfx-chevron-left", '\uE911'),
    CHEVRON_RIGHT("mfx-chevron-right", '\uE912'),
    CHEVRON_UP("mfx-chevron-up", '\uE913'),
    CIRCLE("mfx-circle", '\uE914'),
    CONTENT_COPY("mfx-content-copy", '\uE915'),
    DASHBOARD("mfx-dashboard", '\uE916'),
    DEBUG("mfx-debug", '\uE917'),
    EXCLAMATION_CIRCLE("mfx-exclamation-circle", '\uE918'),
    EXCLAMATION_TRIANGLE("mfx-exclamation-triangle", '\uE919'),
    EXPAND("mfx-expand", '\uE91A'),
    EYE("mfx-eye", '\uE91B'),
    EYE_SLASH("mfx-eye-slash", '\uE91C'),
    FILTER("mfx-filter", '\uE91D'),
    FILTER_ALT("mfx-filter-alt", '\uE91E'),
    FILTER_ALT_CLEAR("mfx-filter-alt-clear", '\uE91F'),
    FIRST_PAGE("mfx-first-page", '\uE920'),
    GEAR("mfx-gear", '\uE921'),
    GOOGLE("mfx-google", '\uE922'),
    GOOGLE_DRIVE("mfx-google-drive", '\uE923'),
    HOME("mfx-home", '\uE924'),
    INFO("mfx-info", '\uE925'),
    INFO_CIRCLE("mfx-info-circle", '\uE926'),
    LAST_PAGE("mfx-last-page", '\uE927'),
    LEVEL_UP("mfx-level-up", '\uE928'),
    LOCK("mfx-lock", '\uE929'),
    LOCK_OPEN("mfx-lock-open", '\uE92A'),
    MINUS("mfx-minus", '\uE92B'),
    MINUS_CIRCLE("mfx-minus-circle", '\uE92C'),
    MODENA_MARK("mfx-modena-mark", '\uE92D'),
    SEARCH("mfx-search", '\uE92E'),
    SEARCH_PLUS("mfx-search-plus", '\uE92F'),
    SLIDERS("mfx-sliders", '\uE930'),
    STEP_BACKWARD("mfx-step-backward", '\uE931'),
    STEP_FORWARD("mfx-step-forward", '\uE932'),
    SYNC("mfx-sync", '\uE933'),
    SYNC_LIGHT("mfx-sync-light", '\uE934'),
    USER("mfx-user", '\uE935'),
    USERS("mfx-users", '\uE936'),
    VARIANT10_MARK("mfx-variant10-mark", '\uE937'),
    VARIANT11_MARK("mfx-variant11-mark", '\uE938'),
    VARIANT12_MARK("mfx-variant12-mark", '\uE939'),
    VARIANT3_MARK("mfx-variant3-mark", '\uE93A'),
    VARIANT4_MARK("mfx-variant4-mark", '\uE93B'),
    VARIANT5_MARK("mfx-variant5-mark", '\uE93C'),
    VARIANT6_MARK("mfx-variant6-mark", '\uE93D'),
    VARIANT7_MARK("mfx-variant7-mark", '\uE93E'),
    VARIANT8_MARK("mfx-variant8-mark", '\uE93F'),
    VARIANT9_MARK("mfx-variant9-mark", '\uE940'),
    X("mfx-x", '\uE941'),
    X_ALT("mfx-x-alt", '\uE942'),
    X_CIRCLE("mfx-x-circle", '\uE943'),
    X_CIRCLE_LIGHT("mfx-x-circle-light", '\uE944'),
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
