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

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

public class GLEventListenerAdapter implements GLEventListener {

	public GLEventListenerAdapter() {
		super();
	}

	@Override
	public void init(final GLAutoDrawable pDrawable) {
	}

	@Override
	public void dispose(final GLAutoDrawable pDrawable) {
	}

	@Override
	public void display(final GLAutoDrawable pDrawable) {
	}

	@Override
	public void reshape(final GLAutoDrawable pDrawable,
			final int pX,
			final int pY,
			final int pWidth,
			final int pHeight) {
	}

}
