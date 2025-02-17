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
import java.nio.IntBuffer;

public class GLIntArray {

	private final int mElementSize;
	private IntBuffer mIntBuffer;

	public GLIntArray(final int pNumberOfElements, final int pElementSize) {
		super();
		mElementSize = pElementSize;
		mIntBuffer = allocate(pNumberOfElements);
	}

	private IntBuffer allocate(final int pNewCapacity) {
		return ByteBuffer.allocateDirect(pNewCapacity * mElementSize
				* (Integer.SIZE / Byte.SIZE))
				.order(ByteOrder.nativeOrder())
				.asIntBuffer();
	}

	public void add(final int... pElementInts) {
		if (mIntBuffer.remaining() < pElementInts.length) {
			final int lNewCapacity = (int) 1.5 * mIntBuffer.capacity();
			final IntBuffer lNewBuffer = allocate(lNewCapacity);
			lNewBuffer.put(mIntBuffer);
			mIntBuffer = lNewBuffer;
		}

		mIntBuffer.put(pElementInts);
	}

	public void clear() {
		mIntBuffer.clear();
	}

	public void fillZeros() {
		mIntBuffer.rewind();
		padZeros();
	}

	public void padZeros() {
		while (mIntBuffer.hasRemaining())
			mIntBuffer.put(0);
	}

	public void rewind() {
		mIntBuffer.rewind();
	}

	public void flip() {
		mIntBuffer.flip();
	}

	public IntBuffer getIntBuffer() {
		rewind();
		return mIntBuffer;
	}

	public int getNumberOfElements() {
		return mIntBuffer.limit();
	}

	public int getElementSize() {
		return mElementSize;
	}

}
