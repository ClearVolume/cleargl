/*-
 * #%L
 * ClearGL facade API on top of JOGL.
 * %%
 * Copyright (C) 2014 - 2025 ClearVolume developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package cleargl;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;

public enum GLTypeEnum {
	Byte(GL.GL_BYTE), UnsignedByte(GL.GL_UNSIGNED_BYTE), Short(GL.GL_SHORT), UnsignedShort(GL.GL_UNSIGNED_SHORT), Int(
			GL2ES2.GL_INT), UnsignedInt(GL.GL_UNSIGNED_INT),
	// Long(-1),
	// UnsignedLong(-1),
	// HalfFloat(-1),
	Float(GL.GL_FLOAT), Double(-1);

	private final int type;

	private GLTypeEnum(final int glType) {
		this.type = glType;
	}

	public int glType() {
		return type;
	}
}
