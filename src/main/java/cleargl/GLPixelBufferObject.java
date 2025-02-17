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

import java.nio.Buffer;
import java.util.Arrays;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GLException;

public class GLPixelBufferObject implements GLInterface, GLCloseable {
	private final GLInterface mGLInterface;
	private int[] mPixelBufferObjectId = new int[1];
	private final int mTextureWidth;
	private final int mTextureHeight;

	public GLPixelBufferObject(final GLInterface pGLInterface,
			final int pWidth,
			final int pHeight) {
		super();
		mGLInterface = pGLInterface;
		mTextureWidth = pWidth;
		mTextureHeight = pHeight;

		mGLInterface.getGL().glGenBuffers(1, mPixelBufferObjectId, 0);

	}

	public void bind() {
		mGLInterface.getGL().glBindBuffer(GL2ES3.GL_PIXEL_UNPACK_BUFFER,
				getId());
	}

	public void unbind() {
		mGLInterface.getGL().glBindBuffer(GL2ES3.GL_PIXEL_UNPACK_BUFFER,
				0);
	}

	public void copyFrom(final Buffer pBuffer) {
		bind();
		mGLInterface.getGL().glBufferData(GL2ES3.GL_PIXEL_UNPACK_BUFFER,
				mTextureWidth * mTextureHeight
						* 1
						* 4,
				null,
				GL.GL_DYNAMIC_DRAW);
		unbind();
	}

	@Override
	public void close() throws GLException {
		mGLInterface.getGL().glDeleteBuffers(1, mPixelBufferObjectId, 0);
		mPixelBufferObjectId = null;
	}

	@Override
	public GL getGL() {
		return mGLInterface.getGL();
	}

	@Override
	public int getId() {
		return mPixelBufferObjectId[0];
	}

	@Override
	public String toString() {
		return "GLPixelBufferObject [mGLInterface=" + mGLInterface
				+ ", mPixelBufferObjectId="
				+ Arrays.toString(mPixelBufferObjectId)
				+ ", mTextureWidth="
				+ mTextureWidth
				+ ", mTextureHeight="
				+ mTextureHeight
				+ "]";
	}

}
