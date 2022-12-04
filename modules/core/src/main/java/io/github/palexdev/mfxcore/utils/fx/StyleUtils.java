/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.utils.fx;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StyleUtils {

	private StyleUtils() {
	}

	/**
	 * Changes the background color of a {@code Region} to the desired one.
	 *
	 * @param region The region to change the background color to
	 * @param fill   The desired color
	 */
	public static void updateBackground(Region region, Paint fill) {
		final Background background = region.getBackground();
		if (background == null || background.getFills().isEmpty()) {
			return;
		}

		final List<BackgroundFill> fills = new ArrayList<>();
		for (BackgroundFill bf : background.getFills()) {
			fills.add(new BackgroundFill(fill, bf.getRadii(), bf.getInsets()));
		}

		region.setBackground(new Background(fills.toArray(BackgroundFill[]::new)));
	}

	/**
	 * Changes the background color of a {@code Region} to the desired one and lets specify the background insets.
	 *
	 * @param region           The region to change the background color to
	 * @param fill             The desired color
	 * @param backgroundInsets The background insets to use
	 */
	public static void updateBackground(Region region, Paint fill, Insets backgroundInsets) {
		final Background background = region.getBackground();
		if (background == null || background.getFills().isEmpty()) {
			return;
		}

		final List<BackgroundFill> fills = new ArrayList<>();
		for (BackgroundFill bf : background.getFills()) {
			fills.add(new BackgroundFill(fill, bf.getRadii(), backgroundInsets));
		}

		region.setBackground(new Background(fills.toArray(BackgroundFill[]::new)));
	}

	public static void updateBackground(Region region, Paint fill, CornerRadii cornerRadii, Insets backgroundInsets) {
		final Background background = region.getBackground();
		if (background == null || background.getFills().isEmpty()) {
			return;
		}

		final List<BackgroundFill> fills = new ArrayList<>();
		for (BackgroundFill bf : background.getFills()) {
			fills.add(new BackgroundFill(fill, cornerRadii, backgroundInsets));
		}

		region.setBackground(new Background(fills.toArray(BackgroundFill[]::new)));
	}

	/**
	 * Sets the background of the given region to the given color.
	 */
	public static void setBackground(Region region, Paint fill) {
		setBackground(region, fill, CornerRadii.EMPTY, Insets.EMPTY);
	}

	/**
	 * Sets the background of the given region to the given color, with the given radius.
	 */
	public static void setBackground(Region region, Paint fill, CornerRadii radius) {
		setBackground(region, fill, radius, Insets.EMPTY);
	}

	/**
	 * Sets the background of the given region to the given color, with the given radius and insets.
	 */
	public static void setBackground(Region region, Paint fill, CornerRadii radius, Insets insets) {
		region.setBackground(new Background(new BackgroundFill(fill, radius, insets)));
	}

	/**
	 * Tries to parse tje given Region's corner radius.
	 * <p>
	 * To be more precise it tries to parse both the background and the
	 * border radius. The background radius is prioritized over the border one
	 * but in case the background is null or empty then the border one is used.
	 * <p>
	 * In case of both null or empty returns {@link  CornerRadii#EMPTY}.
	 */
	public static CornerRadii parseCornerRadius(Region region) {
		CornerRadii backRadius = CornerRadii.EMPTY;
		CornerRadii bordRadius = CornerRadii.EMPTY;

		Background background = region.getBackground();
		if (background != null && !background.isEmpty()) {
			backRadius = background.getFills().get(0).getRadii();
		}

		Border border = region.getBorder();
		if (border != null && !border.isEmpty()) {
			bordRadius = border.getStrokes().get(0).getRadii();
		}

		return !backRadius.equals(CornerRadii.EMPTY) ? backRadius : bordRadius;
	}

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
