package cleargl;

import java.awt.Graphics;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;

public class ClearGLJPanel extends GLJPanel implements GLCloseable
{
	private static final long serialVersionUID = 1L;

	private GLMatrix mProjectionMatrix;
	private GLMatrix mViewMatrix;

	public ClearGLJPanel(ClearGLEventListener pClearGLEventListener)
	{
		super(new GLCapabilities(GLProfile.get(GLProfile.GL4)));

		pClearGLEventListener.setClearGLWindow(null);
		addGLEventListener(pClearGLEventListener);
		setAutoSwapBufferMode(true);

		mProjectionMatrix = new GLMatrix();
		mViewMatrix = new GLMatrix();
	}

	@Override
	public void close() throws GLException
	{
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

	@Override
	public void print(Graphics pGraphics)
	{

	}

	@Override
	public String toString()
	{
		return "ClearGLJPanel [mProjectionMatrix=" + mProjectionMatrix
						+ ", mViewMatrix="
						+ mViewMatrix
						+ "]";
	}




}
