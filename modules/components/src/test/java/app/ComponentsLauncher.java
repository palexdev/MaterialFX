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

package app;

import app.popups.PopupTest;
import javafx.application.Application;

public class ComponentsLauncher {

	public static void main(String[] args) {
		//System.setProperty("prism.order", "sw");
		//System.setProperty("prism.text", "t2k");
		//System.setProperty("prism.lcdtext", "false");
		//System.setProperty("prism.vsync", "false");
		//System.setProperty("prism.showdirty", "true");
		//System.setProperty("prism.forceGPU","true");
		//System.setProperty("prism.verbose", "true");
		//System.setProperty("javafx.animation.fullspeed", "true");
		System.setProperty("glass.disableGrab", "true");
		Application.launch(PopupTest.class, args);
	}

	public static String load(String name) {
		return ComponentsLauncher.class.getResource(name).toExternalForm();
	}
}
