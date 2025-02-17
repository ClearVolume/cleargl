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
import com.jogamp.opengl.GL;

public class GLUniform implements GLInterface {
	private final GLProgram mGlProgram;
	private final int mUniformId;

	public GLUniform(final GLProgram pGlProgram, final int pUniformId) {
		mGlProgram = pGlProgram;
		mUniformId = pUniformId;
	}

	public void setFloatMatrix(final float[] pProjectionMatrix,
			final boolean pTranspose) {
		mGlProgram.bind();
		mGlProgram.getGL()
				.getGL3()
				.glUniformMatrix4fv(mUniformId,
						1,
						pTranspose,
						pProjectionMatrix,
						0);
	}

	public void setFloatMatrix(final FloatBuffer pProjectionMatrix,
			final boolean pTranspose) {
		mGlProgram.bind();
		mGlProgram.getGL()
				.getGL3()
				.glUniformMatrix4fv(mUniformId,
						1,
						pTranspose,
						pProjectionMatrix);
	}

	public void setFloatMatrix(final GLMatrix matrix, final boolean pTranspose) {
		mGlProgram.bind();
		mGlProgram.getGL().getGL3().glUniformMatrix4fv(
				mUniformId,
				1,
				pTranspose,
				FloatBuffer.wrap(matrix.getFloatArray()));
	}

	public void setFloatVector2(final float... pVector2) {
		setFloatVector2(FloatBuffer.wrap(pVector2));
	}

	public void setFloatVector2(final FloatBuffer pVector) {
		mGlProgram.bind();
		mGlProgram.getGL().getGL3().glUniform2fv(mUniformId, 1, pVector);
	}

	public void setFloatVector3(final float... pVector3) {
		setFloatVector3(FloatBuffer.wrap(pVector3));
	}

	public void setFloatVector3(final FloatBuffer pVector) {
		mGlProgram.bind();
		mGlProgram.getGL().getGL3().glUniform3fv(mUniformId, 1, pVector);
	}

	public void setFloatVector(final GLVector pVector) {
		mGlProgram.bind();
		switch (pVector.mDimension) {
			case 2:
				mGlProgram.getGL().getGL3().glUniform2fv(mUniformId, 1, FloatBuffer.wrap(pVector.mElements));
				break;
			case 3:
				mGlProgram.getGL().getGL3().glUniform3fv(mUniformId, 1, FloatBuffer.wrap(pVector.mElements));
				break;
			case 4:
				mGlProgram.getGL().getGL3().glUniform4fv(mUniformId, 1, FloatBuffer.wrap(pVector.mElements));
				break;
			default:
				System.err.println("Unsupported vector dimension " + pVector.mDimension + " for uniform assignment!");
				break;
		}
	}

	public void setFloatVector4(final float... pVector4) {
		setFloatVector4(FloatBuffer.wrap(pVector4));
	}

	public void setFloatVector4(final FloatBuffer pVector) {
		mGlProgram.bind();
		mGlProgram.getGL().getGL3().glUniform4fv(mUniformId, 1, pVector);
	}

	public void setInt(final int pInt) {
		mGlProgram.bind();
		mGlProgram.getGL().getGL3().glUniform1i(mUniformId, pInt);
	}

	public void setFloat(final float pFloat) {
		mGlProgram.bind();
		mGlProgram.getGL().getGL3().glUniform1f(mUniformId, pFloat);
	}

	/*
	 * public void set(double pDouble) { mGlProgram.bind();
	 * mGlProgram.getGL().getGL4().glUniform1d(mUniformId, pDouble); }/
	 **/

	@Override
	public GL getGL() {
		return mGlProgram.getGL();
	}

	@Override
	public int getId() {
		return mUniformId;
	}

	@Override
	public String toString() {
		return "GLUniform [mGlProgram=" + mGlProgram
				+ ", mUniformId="
				+ mUniformId
				+ "]";
	}

}
