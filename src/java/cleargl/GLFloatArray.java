package cleargl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLFloatArray
{
	private final int mNumberOfElements;
	private final int mElementSize;
	private final FloatBuffer mFloatBuffer;

	public GLFloatArray(int pNumberOfElements, int pElementSize)
	{
		super();
		mNumberOfElements = pNumberOfElements;
		mElementSize = pElementSize;
		mFloatBuffer = ByteBuffer.allocateDirect(pNumberOfElements * pElementSize
																							* (Float.SIZE / Byte.SIZE))
															.order(ByteOrder.nativeOrder())
															.asFloatBuffer();
	}

  public void copyFromBuffer(FloatBuffer buffer) {
    mFloatBuffer.put(buffer);
  }

	public void add(float... pElementFloats)
	{
		mFloatBuffer.put(pElementFloats);
	}

	public void fillZeros()
	{
		while (mFloatBuffer.hasRemaining())
			mFloatBuffer.put(0);
	}

	public void rewind()
	{
		mFloatBuffer.rewind();
	}

	public FloatBuffer getFloatBuffer()
	{
		rewind();
		return mFloatBuffer;
	}

	public int getNumberOfElements()
	{
		return mNumberOfElements;
	}

	public int getElementSize()
	{
		return mElementSize;
	}

}
