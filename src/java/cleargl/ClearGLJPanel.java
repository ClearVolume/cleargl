package cleargl;

import java.awt.Component;
import java.io.PrintStream;

import javax.media.nativewindow.WindowClosingProtocol.WindowClosingMode;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;

import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.awt.AWTKeyAdapter;
import com.jogamp.newt.event.awt.AWTMouseAdapter;
import com.jogamp.newt.event.awt.AWTWindowAdapter;

public class ClearGLJPanel implements ClearGLDisplayable

{
	private static final long serialVersionUID = 1L;

	private final GLJPanel mGLJPanel;

	private final GLMatrix mProjectionMatrix;
	private final GLMatrix mViewMatrix;

	public ClearGLJPanel(ClearGLEventListener pClearGLEventListener)
	{
		final GLProfile lProfile = GLProfile.get(GLProfile.GL4);
		final GLCapabilities lGlCapabilities = new GLCapabilities(lProfile);
		mGLJPanel = new GLJPanel(lGlCapabilities);

		pClearGLEventListener.setClearGLWindow(null);
		getGLJPanel().addGLEventListener(pClearGLEventListener);
		getGLJPanel().setAutoSwapBufferMode(true);

		mProjectionMatrix = new GLMatrix();
		mViewMatrix = new GLMatrix();
	}

	@Override
	public void close() throws GLException
	{
	}

	@Override
	public void setPerspectiveProjectionMatrix(	float fov,
																							float ratio,
																							float nearP,
																							float farP)
	{
		mProjectionMatrix.setPerspectiveProjectionMatrix(	fov,
																											ratio,
																											nearP,
																											farP);
	}

	@Override
	public void setOrthoProjectionMatrix(	final float left,
																				final float right,
																				final float bottom,
																				final float top,
																				final float zNear,
																				final float zFar)
	{
		mProjectionMatrix.setOrthoProjectionMatrix(	left,
																								right,
																								bottom,
																								top,
																								zNear,
																								zFar);
	}

	@Override
	public void lookAt(	float pPosX,
											float pPosY,
											float pPosZ,
											float pLookAtX,
											float pLookAtY,
											float pLookAtZ,
											float pUpX,
											float pUpY,
											float pUpZ)
	{
		mViewMatrix.setCamera(pPosX,
													pPosY,
													pPosZ,
													pLookAtX,
													pLookAtY,
													pLookAtZ,
													pUpX,
													pUpY,
													pUpZ);
	}

	@Override
	public GLMatrix getProjectionMatrix()
	{
		return mProjectionMatrix;
	}

	@Override
	public GLMatrix getViewMatrix()
	{
		return mViewMatrix;
	}

	@Override
	public String toString()
	{
		return "ClearGLJPanel [mProjectionMatrix=" + mProjectionMatrix
						+ ", mViewMatrix="
						+ mViewMatrix
						+ "]";
	}

	// @Override
	public WindowClosingMode setDdefaultCloseOperation(WindowClosingMode pWindowClosingMode)
	{
		return getGLJPanel().setDefaultCloseOperation(pWindowClosingMode);
	}

	@Override
	public void setWindowTitle(String pTitleString)
	{
		// no title
	}

	@Override
	public void toggleFullScreen()
	{
		// no full screen
	}

	@Override
	public String getWindowTitle()
	{
		return "";
	}

	@Override
	public void disableClose()
	{
		// no disable close
	}

	@Override
	public boolean isFullscreen()
	{
		return false;
	}

	@Override
	public void setFullscreen(boolean pFullScreen)
	{
		// no full screen
	}

	@Override
	public void display()
	{
		getGLJPanel().repaint();
	}

	@Override
	public void addMouseListener(MouseListener pMouseListener)
	{
		new AWTMouseAdapter(pMouseListener,
												getGLJPanel().getDelegatedDrawable()).addTo(getGLJPanel());
	}

	@Override
	public void addKeyListener(KeyListener pKeyListener)
	{
		new AWTKeyAdapter(pKeyListener,
											getGLJPanel().getDelegatedDrawable()).addTo(getGLJPanel());
	}

	@Override
	public void addWindowListener(WindowAdapter pWindowAdapter)
	{
		new AWTWindowAdapter(	pWindowAdapter,
													getGLJPanel().getDelegatedDrawable()).addTo(getGLJPanel());
	}

	@Override
	public void setUpdateFPSFrames(	int pFramesPerSecond,
																	PrintStream pPrintStream)
	{
		// no fps
	}

	@Override
	public float getLastFPS()
	{
		// no fps
		return 0;
	}

	@Override
	public WindowClosingMode setDefaultCloseOperation(WindowClosingMode pWindowClosingMode)
	{
		return null;
	}

	@Override
	public int getHeight()
	{
		return getGLJPanel().getHeight();
	}

	@Override
	public int getWidth()
	{
		return getGLJPanel().getWidth();
	}

	@Override
	public void setSize(int pWindowWidth, int pWindowHeight)
	{
		getGLJPanel().setSize(pWindowWidth, pWindowHeight);
	}

	@Override
	public void setVisible(boolean pIsVisible)
	{
		getGLJPanel().setVisible(pIsVisible);
	}

	@Override
	public boolean isVisible()
	{
		return getGLJPanel().isVisible();
	}

	public GLJPanel getGLJPanel()
	{
		return mGLJPanel;
	}

	@Override
	public Component getComponent()
	{
		return mGLJPanel;
	}

}
