package cleargl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLFloatArray
{
	private int mElementSize;
	private FloatBuffer mFloatBuffer;

	public GLFloatArray(int pNumberOfElements, int pElementSize)
	{
		super();
		mElementSize = pElementSize;
		mFloatBuffer = allocate(pNumberOfElements);
	}

	private FloatBuffer allocate(int pNewCapacity)
	{
		return ByteBuffer.allocateDirect(pNewCapacity * mElementSize
																			* (Float.SIZE / Byte.SIZE))
											.order(ByteOrder.nativeOrder())
											.asFloatBuffer();
	}

  public void copyFromBuffer(FloatBuffer buffer) {
    mFloatBuffer.put(buffer);
  }

	public void add(float... pElementFloats)
	{
		if (mFloatBuffer.remaining() < pElementFloats.length)
		{
			final int lNewCapacity = (int) 1.5 * mFloatBuffer.capacity();
			FloatBuffer lNewBuffer = allocate(lNewCapacity);
			lNewBuffer.put(mFloatBuffer);
			mFloatBuffer = lNewBuffer;
		}

		mFloatBuffer.put(pElementFloats);
	}

	public void fillZeros()
	{
		mFloatBuffer.rewind();
		padZeros();
	}

	public void padZeros()
	{
		while (mFloatBuffer.hasRemaining())
			mFloatBuffer.put(0);
	}

	public void rewind()
	{
		mFloatBuffer.rewind();
	}

	public void flip()
	{
		mFloatBuffer.flip();
	}

	public FloatBuffer getFloatBuffer()
	{
		rewind();
		return mFloatBuffer;
	}

	public int getNumberOfElements()
	{
		return mFloatBuffer.limit() - 1;
	}

	public int getElementSize()
	{
		return mElementSize;
	}

}
