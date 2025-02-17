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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLFloatArray {
	private final int mElementSize;
	private FloatBuffer mFloatBuffer;
	private volatile int mLastDataPosition;

	public GLFloatArray(final int pNumberOfElements, final int pElementSize) {
		super();
		mElementSize = pElementSize;
		mFloatBuffer = allocate(pNumberOfElements);
	}

	private FloatBuffer allocate(final int pNewCapacity) {
		return ByteBuffer.allocateDirect(pNewCapacity * mElementSize
				* (Float.SIZE / Byte.SIZE))
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
	}

	private FloatBuffer allocateN(final int pNewCapacity) {
		return ByteBuffer.allocateDirect(pNewCapacity * (Float.SIZE / Byte.SIZE))
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
	}

	public void copyFromBuffer(final FloatBuffer buffer) {
		mFloatBuffer.put(buffer);
	}

	public void add(final float... pElementFloats) {
		// System.out.println("cap: " + mFloatBuffer.capacity() + " rem: " +
		// mFloatBuffer.remaining() + " lim: " + mFloatBuffer.limit() + " newe:
		// " +
		// pElementFloats.length + " pos " + mFloatBuffer.position() + " ldp " +
		// mLastDataPosition);

		if (mLastDataPosition + pElementFloats.length > mFloatBuffer.capacity()) {
			final int lNewCapacity = mFloatBuffer.capacity() + pElementFloats.length;
			final FloatBuffer lNewBuffer = allocateN(lNewCapacity);
			lNewBuffer.put(mFloatBuffer);
			mFloatBuffer = lNewBuffer;
		}

		mFloatBuffer.put(pElementFloats);
		// System.out.println("new cap: " + mFloatBuffer.capacity());
		mLastDataPosition = mFloatBuffer.position();
	}

	public void clear() {
		mLastDataPosition = 0;
		mFloatBuffer.clear();
	}

	public void fillZeros() {
		mFloatBuffer.rewind();
		padZeros();
	}

	public void padZeros() {
		while (mFloatBuffer.hasRemaining())
			mFloatBuffer.put(0);
	}

	public void rewind() {
		mFloatBuffer.rewind();
	}

	public void flip() {
		mFloatBuffer.flip();
	}

	public FloatBuffer getFloatBuffer() {
		rewind();
		return mFloatBuffer;
	}

	public int getNumberOfElements() {
		return mFloatBuffer.limit();
	}

	public int getElementSize() {
		return mElementSize;
	}

}
