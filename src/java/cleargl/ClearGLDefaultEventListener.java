package cleargl;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLPipelineFactory;

import com.jogamp.newt.opengl.GLWindow;

public abstract class ClearGLDefaultEventListener	implements
																									ClearGLEventListener
{
	private boolean mDebugMode = false;
	private boolean mAlreadyInDebugMode = false;
	private long mNextFPSUpdate;

	@Override
	public void init(GLAutoDrawable pDrawable)
	{
		getClearGLWindow().getGLWindow().setUpdateFPSFrames(60, null);
		setDebugPipeline(pDrawable);
	}

	@Override
	public void dispose(GLAutoDrawable pDrawable)
	{
		setDebugPipeline(pDrawable);

	}

	@Override
	public void display(GLAutoDrawable pDrawable)
	{
		setDebugPipeline(pDrawable);
		GLWindow lGlWindow = getClearGLWindow().getGLWindow();
		if (System.nanoTime() > mNextFPSUpdate)
		{
			String lWindowTitle = getClearGLWindow().getWindowTitle();
			float lLastFPS = lGlWindow.getLastFPS();
			String lTitleWithFPS = String.format(	"%s (%.0f fps) ",
																						lWindowTitle,
																						lLastFPS);
			lGlWindow.setTitle(lTitleWithFPS);

			mNextFPSUpdate = System.nanoTime() + 1000 * 1000 * 1000;
		}

	}

	@Override
	public void reshape(GLAutoDrawable pDrawable,
											int pX,
											int pY,
											int pWidth,
											int pHeight)
	{
		setDebugPipeline(pDrawable);

	}

	private void setDebugPipeline(GLAutoDrawable pDrawable)
	{
		if (mAlreadyInDebugMode || !isDebugMode())
			return;

		GL4 gl = pDrawable.getGL().getGL4();
		gl.getContext()
			.setGL(GLPipelineFactory.create("javax.media.opengl.Debug",
																			null,
																			gl,
																			null));

		mAlreadyInDebugMode = true;
	}

	@Override
	public abstract void setClearGLWindow(ClearGLWindow pClearGLWindow);

	@Override
	public abstract ClearGLWindow getClearGLWindow();

	public boolean isDebugMode()
	{
		return mDebugMode;
	}

	public void setDebugMode(boolean pDebugMode)
	{
		mDebugMode = pDebugMode;
	}

}
