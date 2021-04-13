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
 * Enumerator class for MaterialFX font resources.
 */
public enum FontResources {
    ANGLE_DOWN("mfx-angle-down", '\uE91b'),
    ANGLE_LEFT("mfx-angle-left", '\uE91c'),
    ANGLE_RIGHT("mfx-angle-right", '\uE91d'),
    ANGLE_UP("mfx-angle-up", '\uE91e'),
    ARROW_BACK("mfx-arrow-back", '\uE925'),
    ARROW_FORWARD("mfx-arrow-forward", '\uE926'),
    CALENDAR_BLACK("mfx-calendar-black", '\uE904'),
    CALENDAR_SEMI_BLACK("mfx-calendar-semi-black", '\uE905'),
    CALENDAR_WHITE("mfx-calendar-white", '\uE906'),
    CARET_DOWN("mfx-caret-down", '\uE91f'),
    CARET_LEFT("mfx-caret-left", '\uE920'),
    CARET_RIGHT("mfx-caret-right", '\uE921'),
    CARET_UP("mfx-caret-up", '\uE922'),
    CASPIAN_MARK("mfx-caspian-mark", '\uE90b'),
    CHART_PIE("mfx-chart-pie", '\uE934'),
    CHECK_CIRCLE("mfx-check-circle", '\uE92f'),
    CHEVRON_DOWN("mfx-chevron-down", '\uE902'),
    CHEVRON_LEFT("mfx-chevron-left", '\uE903'),
    CHEVRON_RIGHT("mfx-chevron-right", '\uE907'),
    CHEVRON_UP("mfx-chevron-up", '\uE908'),
    CIRCLE("mfx-circle", '\uE909'),
    CONTENT_COPY("mfx-content-copy", '\uE935'),
    DASHBOARD("mfx-dashboard", '\uE930'),
    DEBUG("mfx-debug", '\uE93e'),
    EXCLAMATION_CIRCLE("mfx-exclamation-circle", '\uE917'),
    EXCLAMATION_TRIANGLE("mfx-exclamation-triangle", '\uE918'),
    EXPAND("mfx-expand", '\uE919'),
    FILTER("mfx-filter", '\uE929'),
    FILTER_CLEAR("mfx-filter-clear", '\uE92b'),
    FIRST_PAGE("mfx-first-page", '\uE927'),
    GEAR("mfx-gear", '\uE900'),
    GOOGLE("mfx-google", '\uE90a'),
    GOOGLE_DRIVE("mfx-google-drive", '\uE931'),
    HOME("mfx-home", '\uE932'),
    INFO("mfx-info", '\uE933'),
    INFO_CIRCLE("mfx-info-circle", '\uE91a'),
    LAST_PAGE("mfx-last-page", '\uE928'),
    LEVEL_UP("mfx-level-up", '\uE936'),
    MINUS("mfx-minus", '\uE901'),
    MINUS_CIRCLE("mfx-minus-circle", '\uE90c'),
    MODENA_MARK("mfx-modena-mark", '\uE90d'),
    SEARCH("mfx-search", '\uE92e'),
    SEARCH_PLUS("mfx-search-plus", '\uE92a'),
    SLIDERS("mfx-sliders", '\uE93f'),
    STEP_BACKWARD("mfx-step-backward", '\uE923'),
    STEP_FORWARD("mfx-step-forward", '\uE924'),
    SYNC("mfx-sync", '\uE937'),
    SYNC_LIGHT("mfx-sync-light", '\uE938'),
    USER("mfx-user", '\uE92c'),
    USERS("mfx-users", '\uE92d'),
    VARIANT3_MARK("mfx-variant3-mark", '\uE90f'),
    VARIANT4_MARK("mfx-variant4-mark", '\uE90e'),
    VARIANT5_MARK("mfx-variant5-mark", '\uE911'),
    VARIANT6_MARK("mfx-variant6-mark", '\uE910'),
    VARIANT7_MARK("mfx-variant7-mark", '\uE912'),
    VARIANT8_MARK("mfx-variant8-mark", '\uE93a'),
    VARIANT9_MARK("mfx-variant9-mark", '\uE913'),
    VARIANT10_MARK("mfx-variant10-mark", '\uE93b'),
    VARIANT11_MARK("mfx-variant11-mark", '\uE93c'),
    VARIANT12_MARK("mfx-variant12-mark", '\uE914'),
    X("mfx-x", '\uE916'),
    X_ALT("mfx-x-alt", '\uE93d'),
    X_CIRCLE("mfx-x-circle", '\uE915'),
    X_CIRCLE_LIGHT("mfx-x-circle-light", '\uE939'),
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
