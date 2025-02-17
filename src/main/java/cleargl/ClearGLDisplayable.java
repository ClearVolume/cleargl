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

import java.awt.Component;
import java.io.PrintStream;
import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.opengl.GLException;

public interface ClearGLDisplayable extends GLCloseable {

	@Override
	public void close() throws GLException;

	public void setWindowTitle(String pTitleString);

	public void setVisible(boolean pIsVisible);

	public void toggleFullScreen();

	// TODO remove
	public void setPerspectiveProjectionMatrix(float fov,
			float ratio,
			float nearP,
			float farP);

	// TODO remove
	public void setOrthoProjectionMatrix(float left,
			float right,
			float bottom,
			float top,
			float zNear,
			float zFar);

	// TODO remove
	public void lookAt(float pPosX,
			float pPosY,
			float pPosZ,
			float pLookAtX,
			float pLookAtY,
			float pLookAtZ,
			float pUpX,
			float pUpY,
			float pUpZ);

	// TODO remove
	public GLMatrix getProjectionMatrix();

	// TODO remove
	public GLMatrix getViewMatrix();

	public String getWindowTitle();

	public void disableClose();

	public boolean isFullscreen();

	public void setFullscreen(boolean pFullScreen);

	// TODO remove
	public void display();

	public WindowClosingMode setDefaultCloseOperation(WindowClosingMode pWindowClosingMode);

	public void setWindowPosition(int pX, int pY);

	public void setSize(int pWindowWidth, int pWindowHeight);

	public int getWindowX();

	public int getWindowY();

	public int getWindowHeight();

	public int getWindowWidth();

	public int getSurfaceHeight();

	public int getSurfaceWidth();

	public boolean isVisible();

	public void addMouseListener(MouseListener pMouseListener);

	public void addKeyListener(KeyListener pKeyListener);

	public void addWindowListener(WindowAdapter pWindowAdapter);

	public void setUpdateFPSFrames(int pFramesPerSecond,
			PrintStream pPrintStream);

	public float getLastFPS();

	// TODO remove
	public float getAspectRatio();

	public Component getComponent();

	public float[] getBounds();



}
