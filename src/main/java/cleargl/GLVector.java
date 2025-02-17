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

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import com.jogamp.opengl.math.Quaternion;
import com.jogamp.opengl.math.VectorUtil;

/*
@author Ulrik Günther, Loïc Royer
 */

public class GLVector implements Serializable {
	protected float[] mElements;
	protected int mDimension;

	public GLVector(final float... pElements) {
		super();
		mElements = Arrays.copyOf(pElements, pElements.length);
		mDimension = pElements.length;
	}

	public GLVector(final float element, final int dimension) {
		super();
		mElements = new float[dimension];
		for (int i = 0; i < dimension; i++) {
			mElements[i] = element;
		}

		mDimension = dimension;
	}

	public GLVector(final GLVector pGLVector) {
		super();
		mElements = Arrays.copyOf(pGLVector.mElements,
				pGLVector.mElements.length);
		mDimension = pGLVector.mElements.length;
	}

	@Override
	public GLVector clone() {
		return new GLVector(this);
	}

	public float x() {
		return mElements[0];
	}

	public float y() {
		return mElements[1];
	}

	public float z() {
		return mElements[2];
	}

	public float w() {
		return mElements[3];
	}

	public GLVector xyz() {
		return new GLVector(mElements[0], mElements[1], mElements[2]);
	}

	public GLVector xyzw() {
		if (mElements.length == 2) {
			return new GLVector(mElements[0], mElements[1], 0.0f, 0.0f);
		} else if (mElements.length == 3) {
			return new GLVector(mElements[0], mElements[1], mElements[2], 1.0f);
		} else {
			return new GLVector(mElements[0], mElements[1], mElements[2], mElements[3]);
		}
	}

	public float get(final int pIndex) {
		return mElements[pIndex];
	}

	public void set(final int pIndex, final float pValue) {
		mElements[pIndex] = pValue;
	}

	public int getDimension() {
		return mDimension;
	}

	public void plusAssign(final GLVector pGLVector) {
		final float[] lElements = pGLVector.mElements;
		for (int i = 0; i < mDimension; i++)
			mElements[i] += lElements[i];
	}

	public GLVector minus(final GLVector pGLVector) {
		final GLVector lMinus = this.clone();
		lMinus.minusAssign(pGLVector);
		return lMinus;
	}

	public GLVector plus(final GLVector pGLVector) {
		final GLVector lPlus = this.clone();
		lPlus.plusAssign(pGLVector);
		return lPlus;
	}

	public void minusAssign(final GLVector pGLVector) {
		final float[] lElements = pGLVector.mElements;
		for (int i = 0; i < mDimension; i++)
			mElements[i] -= lElements[i];
	}

	public void timesAssign(final GLVector pGLVector) {
		final float[] lElements = pGLVector.mElements;
		for (int i = 0; i < mDimension; i++)
			mElements[i] *= lElements[i];
	}

	public void divAssign(final GLVector pGLVector) {
		final float[] lElements = pGLVector.mElements;
		for (int i = 0; i < mDimension; i++)
			mElements[i] /= lElements[i];
	}

	public float times(final GLVector pGLVector) {
		float lResult = 0;
		final float[] lElements = pGLVector.mElements;
		for (int i = 0; i < mDimension; i++)
			lResult += mElements[i] * lElements[i];
		return lResult;
	}

	public GLVector times(final Quaternion q) {
		final float[] in = this.mElements.clone();
		final float[] out = new float[mDimension];

		q.rotateVector(out, 0, in, 0);

		return new GLVector(out);
	}

	public GLVector times(final float num) {
		final GLVector n = this.clone();
		for (int i = 0; i < mDimension; i++) {
			n.mElements[i] = num * n.mElements[i];
		}

		return n;
	}

	public GLVector hadamard(final GLVector pGLVector) {
		if(this.getDimension() != pGLVector.getDimension()) {
			throw new UnsupportedOperationException("Vectors do not have same dimension: " + this + " vs " + pGLVector);
		}

		GLVector result = this.clone();
		for (int i = 0; i < mDimension; i++) {
			result.mElements[i] *= pGLVector.mElements[i];
		}

		return result;
	}

	public GLVector inverse() {
		GLVector result = this.clone();
		for (int i = 0; i < mDimension; i++) {
			result.mElements[i] = 1.0f/result.mElements[i];
		}

		return result;
	}

	public float magnitude() {
		float lResult = 0;
		for (int i = 0; i < mDimension; i++) {
			final float lValue = mElements[i];
			lResult += lValue * lValue;
		}
		return (float) Math.sqrt(lResult);
	}

	public float length2() {
		float lResult = 0;
		for (int i = 0; i < mDimension; i++) {
			final float lValue = mElements[i];
			lResult += lValue * lValue;
		}
		return lResult;
	}

	public GLVector normalize() {
		final float lFactor = 1f / magnitude();
		for (int i = 0; i < mDimension; i++)
			mElements[i] *= lFactor;
		return this;
	}

	public GLVector cross(final GLVector v) {
		final float result[] = new float[3];
		VectorUtil.crossVec3(result, this.toFloatBuffer().array(), v.toFloatBuffer().array());

		return new GLVector(result);
	}

	public GLVector getNormalized() {
		return this.clone().normalize();
	}

	public FloatBuffer toFloatBuffer() {
		return FloatBuffer.wrap(mElements);
	}

	public float[] toFloatArray() {
		return mElements;
	}

	@Override
	public int hashCode() {
		int hash = Arrays.hashCode(mElements);
		long value = mDimension;
		hash = 31 * hash + (int) (value ^ (value >>> 32));

		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final GLVector other = (GLVector) obj;
		if (mDimension != other.mDimension)
			return false;
		return true;
	}

	public ByteBuffer push(ByteBuffer buffer) {
		int pos = buffer.position();
		buffer.asFloatBuffer().put(mElements);
		buffer.position(pos);

		return buffer;
	}

	public ByteBuffer put(ByteBuffer buffer) {
		int position = buffer.position();
		buffer.asFloatBuffer().put(mElements);
		buffer.position(position + mElements.length * 4);

		return buffer;
	}

	@Override
	public String toString() {
		return "[" + Arrays.toString(mElements) + "]";
	}

	public static GLVector getOneVector(final int dimension) {
		return new GLVector(1.0f, dimension);
	}

	public static GLVector getNullVector(final int dimension) {
		return new GLVector(0.0f, dimension);
	}

}
