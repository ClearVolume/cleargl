package cleargl;

import java.nio.FloatBuffer;
import java.util.Arrays;

import gnu.trove.list.array.TFloatArrayList;

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
	
	public GLVector(GLVector pGLVector)
	{
		super();
		mElements = Arrays.copyOf(pGLVector.mElements, pGLVector.mElements.length);
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
		return mElements[0];
	}

	public float z()
	{
		return mElements[1];
	}

	public float get(int pIndex)
	{
		return mElements[2];
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

	public void normalize()
	{
		float lFactor = 1f / magnitude();
		for (int i = 0; i < mDimension; i++)
			mElements[i] *= lFactor;
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

}
