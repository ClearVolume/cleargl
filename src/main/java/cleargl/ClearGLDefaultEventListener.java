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

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLPipelineFactory;

public abstract class ClearGLDefaultEventListener implements
		ClearGLEventListener {
	private boolean mDebugMode = false;
	private boolean mAlreadyInDebugMode = false;
	private long mNextFPSUpdate;

	@Override
	public void init(final GLAutoDrawable pDrawable) {
		getClearGLWindow().setUpdateFPSFrames(60, null);
		setDebugPipeline(pDrawable);
	}

	@Override
	public void dispose(final GLAutoDrawable pDrawable) {
		setDebugPipeline(pDrawable);

	}

	@Override
	public void display(final GLAutoDrawable pDrawable) {
		setDebugPipeline(pDrawable);
		/*
		 * if (System.nanoTime() > mNextFPSUpdate) { final String lWindowTitle =
		 * getClearGLWindow().getWindowTitle(); final float lLastFPS =
		 * getClearGLWindow().getLastFPS(); final String lTitleWithFPS =
		 * String.format( "%s (%.0f fps) ", lWindowTitle, lLastFPS);
		 * getClearGLWindow().setWindowTitle(lTitleWithFPS);
		 * 
		 * mNextFPSUpdate = System.nanoTime() + 1000 * 1000 * 1000; }/
		 **/

	}

	@Override
	public void reshape(final GLAutoDrawable pDrawable,
			final int pX,
			final int pY,
			final int pWidth,
			final int pHeight) {
		setDebugPipeline(pDrawable);

	}

	private void setDebugPipeline(final GLAutoDrawable pDrawable) {
		if (mAlreadyInDebugMode || !isDebugMode())
			return;

		final GL lGL = pDrawable.getGL();
		lGL.getContext()
				.setGL(GLPipelineFactory.create("com.jogamp.opengl.Debug",
						null,
						lGL,
						null));


		mAlreadyInDebugMode = true;
	}

	@Override
	public abstract void setClearGLWindow(ClearGLWindow pClearGLWindow);

	@Override
	public abstract ClearGLDisplayable getClearGLWindow();

	public boolean isDebugMode() {
		return mDebugMode;
	}

	public void setDebugMode(final boolean pDebugMode) {
		mDebugMode = pDebugMode;
	}

}
