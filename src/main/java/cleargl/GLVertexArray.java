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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLException;

public class GLVertexArray implements GLCloseable, GLInterface {

	private final GLInterface mGLInterface;
	private final int[] mVertexArrayId;
	private int mNumberOfIndices;
	private int mIndexCount = 0;

	public GLVertexArray(final GLInterface pGLInterface) {
		super();
		mGLInterface = pGLInterface;
		mVertexArrayId = new int[1];
		getGL().getGL3().glGenVertexArrays(1, mVertexArrayId, 0);
	}

	@Override
	public void close() throws GLException {
		mGLInterface.getGL()
				.getGL3()
				.glDeleteVertexArrays(1, mVertexArrayId, 0);
	}

	public void addVertexAttributeArray(final GLVertexAttributeArray pGLVertexAttributeArray,
			final FloatBuffer pFloatBuffer) {
		bind();

		final GLAttribute lAttribute = pGLVertexAttributeArray.getAttribute();

		pGLVertexAttributeArray.bind();

		final int lElementsPerIndex = pGLVertexAttributeArray.getElementsPerIndex();
		mNumberOfIndices = pFloatBuffer.remaining() / lElementsPerIndex;

		getGL().glBufferData(GL.GL_ARRAY_BUFFER,
				pFloatBuffer.remaining() * (Float.SIZE / 8),
				pFloatBuffer,
				GL.GL_STATIC_DRAW);
		getGL().getGL3().glEnableVertexAttribArray(lAttribute.getIndex());
		getGL().getGL3().glVertexAttribPointer(lAttribute.getIndex(),
				lElementsPerIndex,
				GL.GL_FLOAT,
				false,
				0,
				0);
	}

	public void addIndexArray(final GLVertexAttributeArray pGLVertexAttributeArray,
			final IntBuffer pIndexBuffer) {
		bind();

		getGL().glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER,
				pGLVertexAttributeArray.getId(1));
		getGL().glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER,
				pIndexBuffer.remaining() * (Integer.SIZE / 8)
						+ 1,
				pIndexBuffer,
				GL.GL_STATIC_DRAW);

		mIndexCount = pIndexBuffer.remaining();
	}

	public void bind() {
		getGL().getGL3().glBindVertexArray(getId());
	}

	public void unbind() {
		getGL().getGL3().glBindVertexArray(0);
	}

	public void draw(final int pType) {
		bind();
		if (mIndexCount > 0) {
			getGL().glDrawElements(pType,
					mIndexCount,
					GL.GL_UNSIGNED_INT,
					0);
		} else {
			getGL().glDrawArrays(pType, 0, mNumberOfIndices);
		}
	}

	@Override
	public GL getGL() {
		return mGLInterface.getGL();
	}

	@Override
	public int getId() {
		return mVertexArrayId[0];
	}

	@Override
	public String toString() {
		return "GLVertexArray [mGLInterface=" + mGLInterface
				+ ", mVertexArrayId="
				+ Arrays.toString(mVertexArrayId)
				+ ", mNumberOfIndices="
				+ mNumberOfIndices
				+ "]";
	}

}
