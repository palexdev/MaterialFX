package io.github.palexdev.materialfx.beans;

import javafx.geometry.HPos;
import javafx.geometry.VPos;

/**
 * This bean's purpose is to allow specifying position based on
 * {@link HPos} and {@link VPos} enumerations.
 */
public class Alignment {
	//================================================================================
	// Properties
	//================================================================================
	private final HPos hPos;
	private final VPos vPos;

	//================================================================================
	// Constructors
	//================================================================================
	public Alignment(HPos hPos, VPos vPos) {
		this.hPos = hPos;
		this.vPos = vPos;
	}

	//================================================================================
	// Static Methods
	//================================================================================
	public static Alignment of(HPos hPos, VPos vPos) {
		return new Alignment(hPos, vPos);
	}

	//================================================================================
	// Getters
	//================================================================================
	public HPos getHPos() {
		return hPos;
	}

	public VPos getVPos() {
		return vPos;
	}
}
