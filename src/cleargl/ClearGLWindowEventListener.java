package cleargl;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

public abstract class ClearGLWindowEventListener implements
																								GLEventListener
{

	private ClearGLWindow mClearGLWindow;
	private GLMatrix mProjectionMatrix;
	private GLMatrix mViewMatrix;

	public ClearGLWindowEventListener()
	{
		super();
		mProjectionMatrix = new GLMatrix();
		mViewMatrix = new GLMatrix();

	}

	@Override
	public void dispose(GLAutoDrawable pDrawable)
	{

	}

	@Override
	public void reshape(GLAutoDrawable pDrawable,
											int pX,
											int pY,
											int pWidth,
											int pHeight)
	{
		try
		{
			float ratio;
			// Prevent a divide by zero, when window is too short
			// (you can't make a window of zero width).
			if (pHeight == 0)
				pHeight = 1;

			ratio = (1.0f * pWidth) / pHeight;

			setPerspectiveProjectionMatrix(53.13f, ratio, 1.0f, 30.0f);
		}
		catch (Throwable e)
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
		getViewMatrix().setCamera(pPosX,
															pPosY,
															pPosZ,
															pLookAtX,
															pLookAtY,
															pLookAtZ,
															pUpX,
															pUpY,
															pUpZ);
	}

	public ClearGLWindow getClearGLWindow()
	{
		return mClearGLWindow;
	}

	public void setClearGLWindow(ClearGLWindow pClearGLWindow)
	{
		mClearGLWindow = pClearGLWindow;
	}

	public GLMatrix getProjectionMatrix()
	{
		return mProjectionMatrix;
	}

	public GLMatrix getViewMatrix()
	{
		return mViewMatrix;
	}

	@Override
	public String toString()
	{
		return "ClearGLWindowEventListener [mClearGLWindow=" + mClearGLWindow
						+ ", mProjectionMatrix="
						+ mProjectionMatrix
						+ ", mViewMatrix="
						+ mViewMatrix
						+ "]";
	}



}
