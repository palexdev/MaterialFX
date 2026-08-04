/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.utils.fx.resize;

import io.github.palexdev.mfxcore.utils.fx.resize.targets.ResizeTarget;

/// The `ResizeHandler` is a callback that can be used to override the default resize behavior of a [Resizer].
/// When a handler is set through [Resizer#resizeHandler(ResizeHandler)], it will skip [ResizeTarget#apply(double, double, double, double)]
/// and call [#resize(Object, double, double, double, double)] instead.
@FunctionalInterface
public interface ResizeHandler<T> {
    void resize(T target, double x, double y, double w, double h);
}
