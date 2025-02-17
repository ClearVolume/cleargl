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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class GLMatrixtest {

	@Test
	public void testSetMultGet() {
		final GLMatrix lGLMatrix = new GLMatrix();
		lGLMatrix.set(0, 1, 1);
		assertEquals(1, lGLMatrix.get(0, 1), 0);
		lGLMatrix.mult(0, 1, 2);
		assertEquals(2, lGLMatrix.get(0, 1), 0);
		lGLMatrix.mult(1, 0, 0.5f);
		assertEquals(2, lGLMatrix.get(0, 1), 0);
	}

}
