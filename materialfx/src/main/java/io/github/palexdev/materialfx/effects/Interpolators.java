/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.effects;

import javafx.animation.Interpolator;

import java.util.function.Function;

/**
 * Enumerator that offers some new {@link Interpolator}s for JavaFX's animations.
 */
public enum Interpolators {
	INTERPOLATOR_V1(null) {
		public Interpolator toInterpolator() {
			return Interpolator.SPLINE(0.25D, 0.1D, 0.25D, 1.0D);
		}
	},
	INTERPOLATOR_V2(null) {
		public Interpolator toInterpolator() {
			return Interpolator.SPLINE(0.0825D, 0.3025D, 0.0875D, 0.9975D);
		}
	},
	LINEAR((t) -> t) {
		public Interpolator toInterpolator() {
			return new Interpolator() {
				protected double curve(double t) {
					return getCurve().apply(t);
				}
			};
		}
	},
	EASE_IN((t) -> t * t * t) {
		public Interpolator toInterpolator() {
			return new Interpolator() {
				protected double curve(double t) {
					return getCurve().apply(t);
				}
			};
		}
	},
	EASE_IN_SINE((t) -> 1.0D - Math.cos(t * 3.141592653589793D / 2.0D)) {
		public Interpolator toInterpolator() {
			return new Interpolator() {
				protected double curve(double t) {
					return getCurve().apply(t);
				}
			};
		}
	},
	EASE_OUT((t) -> 1.0D - (1.0D - t) * (1.0D - t) * (1.0D - t)) {
		public Interpolator toInterpolator() {
			return new Interpolator() {
				protected double curve(double t) {
					return getCurve().apply(t);
				}
			};
		}
	},
	EASE_OUT_SINE((t) -> Math.sin(t * 3.141592653589793D / 2.0D)) {
		public Interpolator toInterpolator() {
			return new Interpolator() {
				protected double curve(double t) {
					return getCurve().apply(t);
				}
			};
		}
	},
	EASE_IN_OUT((t) -> t < 0.5D ? 4.0D * t * t * t : 1.0D - Math.pow(-2.0D * t + 2.0D, 3.0D) / 2.0D) {
		public Interpolator toInterpolator() {
			return new Interpolator() {
				protected double curve(double t) {
					return getCurve().apply(t);
				}
			};
		}
	},
	EASE_IN_OUT_SINE((t) -> -(Math.cos(3.141592653589793D * t) - 1.0D) / 2.0D) {
		public Interpolator toInterpolator() {
			return new Interpolator() {
				protected double curve(double t) {
					return getCurve().apply(t);
				}
			};
		}
	};

	private final Function<Double, Double> curve;

	Interpolators(Function<Double, Double> curve) {
		this.curve = curve;
	}

	/**
	 * Converts a {@code Function<Double, Double>} to a JavaFX's {@link Interpolator}.
	 */
	public abstract Interpolator toInterpolator();

	public Function<Double, Double> getCurve() {
		return this.curve;
	}
}
