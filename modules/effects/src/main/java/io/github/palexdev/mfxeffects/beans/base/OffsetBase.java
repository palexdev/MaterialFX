package io.github.palexdev.mfxeffects.beans.base;

import java.util.Objects;

public abstract class OffsetBase {
	protected final double dx;
	protected final double dy;

	public OffsetBase(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public boolean lesser(OffsetBase other) {
		return dx < other.dx && dy < other.dy;
	}

	public boolean lesserEq(OffsetBase other) {
		return dx <= other.dx && dy <= other.dy;
	}

	public boolean greater(OffsetBase other) {
		return dx > other.dx && dy > other.dy;
	}

	public boolean greaterEq(OffsetBase other) {
		return dx >= other.dx && dy >= other.dy;
	}

	public boolean isInfinite() {
		return Double.isInfinite(dx) || Double.isInfinite(dy);
	}

	public boolean isFinite() {
		return !isInfinite();
	}

	public double getDx() {
		return dx;
	}

	public double getDy() {
		return dy;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OffsetBase that = (OffsetBase) o;
		return Double.compare(that.dx, dx) == 0 && Double.compare(that.dy, dy) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dx, dy);
	}

	@Override
	public String toString() {
		return String.format("%s(%f, %f)", getClass().getSimpleName(), dx, dy);
	}
}
