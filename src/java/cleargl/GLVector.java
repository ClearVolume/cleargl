package cleargl;

import java.nio.FloatBuffer;
import java.util.Arrays;

/*
@author Ulrik Günther, Loïc Royer
 */

public class GLVector
{
	protected float[] mElements;
	protected int mDimension;

	public GLVector(float... pElements)
	{
		super();
		mElements = Arrays.copyOf(pElements, pElements.length);
		mDimension = pElements.length;
	}

	public GLVector(float element, int dimension) {
		super();
		mElements = new float[dimension];
		for(int i = 0; i < dimension; i++) {
			mElements[i] = element;
		}

		mDimension = dimension;
	}

	public GLVector(GLVector pGLVector)
	{
		super();
		mElements = Arrays.copyOf(pGLVector.mElements,
															pGLVector.mElements.length);
		mDimension = pGLVector.mElements.length;
	}

	public GLVector clone()
	{
		return new GLVector(this);
	}

	public float x()
	{
		return mElements[0];
	}

	public float y()
	{
		return mElements[1];
	}

	public float z()
	{
		return mElements[2];
	}

	public float get(int pIndex)
	{
		return mElements[pIndex];
	}

	public void set(int pIndex, float pValue)
	{
		mElements[pIndex] = pValue;
	}
	
	public void plusAssign(GLVector pGLVector)
	{
		float[] lElements = pGLVector.mElements;
		for (int i = 0; i < mDimension; i++)
			mElements[i] += lElements[i];
	}

	public GLVector minus(GLVector pGLVector)
	{
		GLVector lMinus = this.clone();
		lMinus.minusAssign(pGLVector);
		return lMinus;
	}

	public GLVector plus(GLVector pGLVector)
	{
		GLVector lPlus = this.clone();
		lPlus.plusAssign(pGLVector);
		return lPlus;
	}

	public void minusAssign(GLVector pGLVector)
	{
		float[] lElements = pGLVector.mElements;
		for (int i = 0; i < mDimension; i++)
			mElements[i] -= lElements[i];
	}

	public void timesAssign(GLVector pGLVector)
	{
		float[] lElements = pGLVector.mElements;
		for (int i = 0; i < mDimension; i++)
			mElements[i] *= lElements[i];
	}

	public void divAssign(GLVector pGLVector)
	{
		float[] lElements = pGLVector.mElements;
		for (int i = 0; i < mDimension; i++)
			mElements[i] /= lElements[i];
	}

	public float times(GLVector pGLVector)
	{
		float lResult = 0;
		float[] lElements = pGLVector.mElements;
		for (int i = 0; i < mDimension; i++)
			lResult += mElements[i] * lElements[i];
		return lResult;
	}

	public float magnitude()
	{
		float lResult = 0;
		for (int i = 0; i < mDimension; i++)
		{
			float lValue = mElements[i];
			lResult += lValue * lValue;
		}
		return lResult;
	}

	public GLVector normalize()
	{
		float lFactor = 1f / magnitude();
		for (int i = 0; i < mDimension; i++)
			mElements[i] *= lFactor;
		return this;
	}

	public GLVector getNormalized()
	{
		return this.clone().normalize();
	}

	public FloatBuffer toFloatBuffer()
	{
		return FloatBuffer.wrap(mElements);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + mDimension;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GLVector other = (GLVector) obj;
		if (mDimension != other.mDimension)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "GLVector [mElements=" + Arrays.toString(mElements)
						+ ", mDimension="
						+ mDimension
						+ "]";
	}

	public static GLVector getOneVector(int dimension) {
		return new GLVector(1.0f, dimension);
	}

	public static GLVector getNullVector(int dimension) {
	  return new GLVector(0.0f, dimension);
	}

}
