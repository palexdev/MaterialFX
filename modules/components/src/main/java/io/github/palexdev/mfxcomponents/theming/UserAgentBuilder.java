/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcomponents.theming;

import io.github.palexdev.mfxcomponents.theming.base.Theme;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The best way to style a JavaFX application is to use {@link Application#setUserAgentStylesheet(String)} because the
 * styling will be applied everywhere, even in separate windows/scenes. And this is huge for developers that create their
 * own themes. First, it's not needed to track every single window/scene (which means no more worries about
 * dialogs/popups and such not being styled); and second the performance will be much better since the theme will be parsed
 * only once.
 * <p>
 * However, ideally the author of a theme should still use one of the JavaFX's default themes as a base, to make sure that
 * every control is covered, or maybe because his themes are just for his custom controls (coff coff MaterialFX coff).
 * For whatever reason, it's always a good idea to have a base.
 * <p>
 * Unfortunately as of now, JavaFX doesn't have a Theming API which would easily allow to create a single theme from a group
 * of stylesheets. Which means that there is no way to set a user agent stylesheet that can cover all the controls.
 * There IS a proposal for such API <a href=https://github.com/openjdk/jfx/pull/511>#511</a>, but at the time of writing
 * this, it's been closed and I don't think we'll see such API in the near future, as always JavaFX development is so SLOW and
 * clumsy when it comes to new features, it's a shame really.
 * <p></p>
 * This builder is an attempt at supporting such use cases, and while it's true that the goal can be achieved, it doesn't come
 * without some caveats which I will discuss later on.
 * <p>
 * The mechanism is rather simple, you can specify a set of themes with {@link #themes(Theme...)}, these will be merged into
 * a single stylesheet by the {@link #build()} method. The result is a {@link CSSFragment} object that,once converted to
 * a Data URI through {@link CSSFragment#toDataUri()}, can be set as the {@link Application}'s user agent, can be added
 * on a {@link Scene} or on a {@link Parent}. The explicit conversion can be avoided by using one of the convenience
 * method offered by {@link CSSFragment}.
 * <p></p>
 * Now, let's talk about the <b>caveats</b>.
 * <p>
 * Before the final stylesheet can be feed to {@link CSSFragment}, it needs to be processed by
 * {@link Processor#process(String)}. In my tests, I found out that Data URIs would fail to work if any of the themes
 * contains {@code at-statements}, such as 'import', 'font-face' and such. For this reason, all those rules must be removed,
 * and unfortunately this is very bad for two reasons:
 * <p> 1) This can work only with 'full themes', stylesheets that do not import/depend on other stylesheets
 * <p> 2) Since fonts, added through imports or directly through font-faces, are removed, there's the risk of text not
 * having the desired font. On this matter though, I found out another interesting fact about JavaFX. It seems that for
 * font-faces it doesn't matter where stylesheets are added. If you add the stylesheet containing the font-faces declarations
 * on the root scene for example, the fonts will be picked in other windows as well, and this is a good news
 */
public class UserAgentBuilder {
    //================================================================================
    // Properties
    //================================================================================
    private final Set<Theme> themes = new LinkedHashSet<>();

    //================================================================================
    // Constructors
    //================================================================================
    public UserAgentBuilder() {
    }

    public static UserAgentBuilder builder() {
        return new UserAgentBuilder();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Allows to specify all the themes that will be merged by {@link #build()}.
     * <p>
     * The themes are stored in a {@link LinkedHashSet}, ensuring insertion order and avoiding duplicates.
     */
    public UserAgentBuilder themes(Theme... themes) {
        Collections.addAll(this.themes, themes);
        return this;
    }

    /**
     * Iterates over all the themes added through {@link #themes(Theme...)}, loading them with {@link #load(Theme)},
     * and merging them by using a {@link StringBuilder}.
     * <p></p>
     * Before returning the {@link CSSFragment}, the merged stylesheet must be processed to remove all the
     * {@code at-rules}, as well as removing all the comments.
     */
    public CSSFragment build() {
        StringBuilder sb = new StringBuilder();
        for (Theme theme : themes) {
            String loaded = load(theme);
            sb.append(loaded).append("\n\n");
        }
        String postProcessed = Processor.process(sb.toString());
        return new CSSFragment(postProcessed);
    }

    /**
     * Loads a stylesheet specified by the given {@link Theme}. To achieve this, two streams are used, an {@link InputStream}
     * which derives from the theme's URL, {@link URL#openStream()}, and a {@link ByteArrayOutputStream}.
     * <p>
     * The stylesheet's contents are read from the input stream and wrote to the output stream. At the end, the latter
     * is converter to a string by using {@link ByteArrayOutputStream#toString(Charset)} and {@link StandardCharsets#UTF_8}.
     * <p></p>
     * In case of errors, an empty string is returned, and the exception printed to the stdout.
     */
    protected String load(Theme theme) {
        try (InputStream is = theme.get().openStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            for (int length; (length = is.read(buffer)) != -1; ) {
                baos.write(buffer, 0, length);
            }
            return baos.toString(StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    private static class Processor {
        enum Type {
            START_COMMENT,
            END_COMMENT,
            IMPORT,
            OTHER
        }

        /**
         * Processes the stylesheet merged by {@link UserAgentBuilder#build()} to remove any comment but most importantly
         * to remove any CSS {@code at-rule} that would cause the stylesheet to not work.
         * <p></p>
         * This is a naive and simple implementation, it's expected that the origin stylesheets are valid and well formatted.
         */
        public static String process(String data) {
            String[] lines = data.split("\n");

            StringBuilder sb = new StringBuilder();
            boolean insideComment = false;
            for (String line : lines) {
                if (line.isBlank()) continue;
                Type type = typeOf(line);

                // Ignore comments
                if (type == Type.START_COMMENT) {
                    if (line.trim().endsWith("*/")) continue; // One line comment
                    insideComment = true;
                    continue;
                }
                if (type == Type.END_COMMENT) {
                    insideComment = false;
                    continue;
                }
                if (insideComment) continue;

                // Ignore imports
                if (type == Type.IMPORT) {
                    System.err.printf("Unsupported import statement: %s was found, skipping!%n", line);
                    continue;
                }

                if (sb.toString().endsWith("}\n")) sb.append("\n");
                sb.append(line).append("\n");
            }
            return sb.toString();
        }

        private static Type typeOf(String line) {
            if (line.trim().startsWith("/*")) return Type.START_COMMENT;
            if (line.trim().endsWith("*/")) return Type.END_COMMENT;
            if (line.trim().startsWith("@")) return Type.IMPORT;
            return Type.OTHER;
        }
    }
}
