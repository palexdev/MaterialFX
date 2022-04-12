#!/bin/sh

#
# Copyright (C) 2022 Parisi Alessandro
# This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
#
# MaterialFX is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# MaterialFX is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
#

# This only works on Linux and specifically my system since tar2zip is a npm module I installed to convert the tar archive
# to zip easily.
# I was tired of Gradle shit and also tired of learning about boring Linux commands stuff (the tar command is quantum physic
# and the zip command on the other hand is very limited) so this is the quickest solution I came up with

BASE_DIR=$1
IMG_DIR="${BASE_DIR}/image"
OUT_DIR="${BASE_DIR}/distributions"

tar -C "${IMG_DIR}/MaterialFX Demo-linux-x64" --transform 'flags=r;s|.|MFXDemo_Linux|' -c . | xz -4 > "${OUT_DIR}/materialfx_demo_linux64.tar.xz"
tar -C "${IMG_DIR}/MaterialFX Demo-mac" --transform 'flags=r;s|.|MFXDemo_Mac|' -c . | xz -4 > "${OUT_DIR}/materialfx_demo_mac64.tar.xz"
tar -C "${IMG_DIR}/MaterialFX Demo-win" --transform 'flags=r;s|.|MFXDemo_Win|' -c . | tar2zip > "${OUT_DIR}/materialfx_demo_win64.zip"