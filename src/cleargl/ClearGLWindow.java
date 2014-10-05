package cleargl;

import javax.media.nativewindow.WindowClosingProtocol.WindowClosingMode;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;

import com.jogamp.common.util.IOUtil.ClassResources;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Window;
import com.jogamp.newt.opengl.GLWindow;


public class ClearGLWindow implements GLCloseable
{
	static
	{
		try
		{
			System.setProperty("sun.awt.noerasebackground", "true");

			ClassResources lClassResources = new ClassResources(ClearGLWindow.class,
																													new String[]
																													{ "icon/ClearGLIcon16.png",
																														"icon/ClearGLIcon32.png" });
			// NewtFactory.setWindowIcons(lClassResources);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	private GLWindow mGlWindow;
	private Window mWindow;
	private int mWindowDefaultWidth;
	private int mWindowDefaultHeight;

	private ClearGLWindow mClearGLWindow;
	private GLMatrix mProjectionMatrix;
	private GLMatrix mViewMatrix;

	public ClearGLWindow(	String pWindowTitle,
												int pDefaultWidth,
												int pDefaultHeight,
												ClearGLEventListener pClearGLWindowEventListener)
	{
		super();
		mWindowDefaultWidth = pDefaultWidth;
		mWindowDefaultHeight = pDefaultHeight;
		final GLProfile lProfile = GLProfile.get(GLProfile.GL4);
		final GLCapabilities lCapabilities = new GLCapabilities(lProfile);
		mWindow = NewtFactory.createWindow(lCapabilities);
		mGlWindow = GLWindow.create(mWindow);
		mGlWindow.setTitle(pWindowTitle);

		pClearGLWindowEventListener.setClearGLWindow(this);
		mGlWindow.addGLEventListener(pClearGLWindowEventListener);
		mGlWindow.setSize(pDefaultWidth, pDefaultHeight);
		mGlWindow.setAutoSwapBufferMode(true);

		mProjectionMatrix = new GLMatrix();
		mViewMatrix = new GLMatrix();

		// gl = mGlWindow.setGL(new DebugGL(mGlWindow.getGL()));

		/*..setProperty(	"newt.window.icons",
													"cleargl/icon/ClearGLIcon16.png cleargl/icon/ClearGLIcon32.png");/**/

	}

	@Override
	public void close() throws GLException
	{
		try
		{
			mGlWindow.setVisible(false);
			if (mGlWindow.isRealized())
				mGlWindow.destroy();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	public GLWindow getGLWindow()
	{
		return mGlWindow;
	}

	/**
	 * @param pTitleString
	 */
	public void setWindowTitle(final String pTitleString)
	{
		mGlWindow.setTitle(pTitleString);
	}

	public void setVisible(final boolean pIsVisible)
	{
		mGlWindow.setVisible(pIsVisible);
	}

	public void toggleFullScreen()
	{
		try
		{
			if (mGlWindow.isFullscreen())
			{
				mGlWindow.setFullscreen(false);
			}
			else
			{
				mGlWindow.setSize(mWindowDefaultWidth, mWindowDefaultHeight);
				mGlWindow.setFullscreen(true);
			}
			mGlWindow.display();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

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

	public GLMatrix getProjectionMatrix()
	{
		return mProjectionMatrix;
	}

	public GLMatrix getViewMatrix()
	{
		return mViewMatrix;
	}

	public void disableClose()
	{
		mGlWindow.setDefaultCloseOperation(WindowClosingMode.DO_NOTHING_ON_CLOSE);
	}

	@Override
	public String toString()
	{
		return "ClearGLWindow [mGlWindow=" + mGlWindow
						+ ", mWindow="
						+ mWindow
						+ ", mWindowDefaultWidth="
						+ mWindowDefaultWidth
						+ ", mWindowDefaultHeight="
						+ mWindowDefaultHeight
						+ "]";
	}

}
