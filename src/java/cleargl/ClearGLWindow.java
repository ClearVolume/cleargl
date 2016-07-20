package cleargl;

import java.awt.Component;
import java.io.PrintStream;
import java.util.List;

import com.jogamp.nativewindow.CapabilitiesImmutable;
import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Window;
import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.DefaultGLCapabilitiesChooser;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLCapabilitiesChooser;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

public class ClearGLWindow implements ClearGLDisplayable
{

	private final GLWindow mGlWindow;
	private final Window mWindow;
	private final String mWindowTitle;
	private final int mWindowDefaultWidth;
	private final int mWindowDefaultHeight;

	private ClearGLDisplayable mClearGLWindow;
	private final GLMatrix mProjectionMatrix;
	private final GLMatrix mViewMatrix;
	private NewtCanvasAWT mNewtCanvasAWT;

	private FPSAnimator mAnimator;
	private int mFramesPerSecond = 60;
	private ClearGLEventListener mClearGLWindowEventListener;

	static
	{
		System.setProperty("sun.awt.noerasebackground", "true");
	}

	static class MultisampleChooser	extends
																	DefaultGLCapabilitiesChooser
	{
		public int chooseCapabilities(GLCapabilities desired,
																	List<? extends CapabilitiesImmutable> available,
																	int windowSystemRecommendedChoice)
		{
			boolean anyHaveSampleBuffers = false;
			for (int i = 0; i < available.size(); i++)
			{
				final GLCapabilitiesImmutable caps = (GLCapabilitiesImmutable) available.get(i);
				if (caps != null && caps.getSampleBuffers())
				{
					anyHaveSampleBuffers = true;
					break;
				}
			}
			final int selection = super.chooseCapabilities(	desired,
																											available,
																											windowSystemRecommendedChoice);
			if (!anyHaveSampleBuffers)
			{
				System.err.println("WARNING: antialiasing will be disabled because none of the available pixel formats had it to offer");
			}
			else if (selection >= 0)
			{
				final GLCapabilitiesImmutable caps = (GLCapabilitiesImmutable) available.get(selection);
				if (!caps.getSampleBuffers())
				{
					System.err.println("WARNING: antialiasing will be disabled because the DefaultGLCapabilitiesChooser didn't supply it");
				}
			}
			return selection;
		}
	}

	public static final void setWindowIconsDefault()
	{
		setWindowIcons(	"cleargl/icon/ClearGLIcon16.png",
										"cleargl/icon/ClearGLIcon32.png");
	}

	public static final void setWindowIcons(String... pIconsLowToHighRessourcePaths)
	{
		try
		{

			final StringBuilder lStringBuilder = new StringBuilder();

			for (final String lIconRessourcePath : pIconsLowToHighRessourcePaths)
			{
				lStringBuilder.append(lIconRessourcePath);
				lStringBuilder.append(' ');
			}

			System.setProperty(	"newt.window.icons",
													lStringBuilder.toString());
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

	public ClearGLWindow(	String pWindowTitle,
												int pDefaultWidth,
												int pDefaultHeight,
												ClearGLEventListener pClearGLWindowEventListener)
	{
		this(	pWindowTitle,
					pDefaultWidth,
					pDefaultHeight,
					16,
					pClearGLWindowEventListener);
	}

	public ClearGLWindow(	String pWindowTitle,
												int pDefaultWidth,
												int pDefaultHeight,
												int pNumberOfSamples,
												ClearGLEventListener pClearGLWindowEventListener)
	{
		super();
		mWindowTitle = pWindowTitle;
		mWindowDefaultWidth = pDefaultWidth;
		mWindowDefaultHeight = pDefaultHeight;
		mClearGLWindowEventListener = pClearGLWindowEventListener;

		mProjectionMatrix = new GLMatrix();
		mViewMatrix = new GLMatrix();

		final GLProfile lProfile = GLProfile.getMaxProgrammable(true);
		System.out.println(this.getClass().getSimpleName() + ": "
												+ lProfile);
		final GLCapabilities lCapabilities = new GLCapabilities(lProfile);

		lCapabilities.setSampleBuffers(pNumberOfSamples > 1);
		lCapabilities.setNumSamples(pNumberOfSamples);

		final GLCapabilitiesChooser lMultisampleChooser = new MultisampleChooser();

		mWindow = NewtFactory.createWindow(lCapabilities);
		mGlWindow = GLWindow.create(mWindow);
		mGlWindow.setCapabilitiesChooser(lMultisampleChooser);
		mGlWindow.setTitle(pWindowTitle);

		mClearGLWindowEventListener.setClearGLWindow(this);
		mGlWindow.addGLEventListener(mClearGLWindowEventListener);
		mGlWindow.setSize(pDefaultWidth, pDefaultHeight);
		mGlWindow.setAutoSwapBufferMode(true);

		// lAnimator.add(mClearGLWindow.getGLAutoDrawable());
	}

	public void setFPS(int pFramesPerSecond)
	{
		mFramesPerSecond = pFramesPerSecond;
	}

	public void start()
	{
		mAnimator = new FPSAnimator(this.getGLAutoDrawable(),
																mFramesPerSecond);
		mAnimator.start();
		while (!mAnimator.isAnimating())
			Thread.yield();
	}

	public void stop()
	{
		mAnimator.setIgnoreExceptions(true);
		mAnimator.pause();
		mAnimator.stop();
		while (mAnimator.isAnimating())
			Thread.yield();
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#close()
	 */
	@Override
	public void close() throws GLException
	{
		runOnEDT(	true,
							() -> {
								try
								{
									try
									{
										mGlWindow.setVisible(false);
										mGlWindow.removeGLEventListener(mClearGLWindowEventListener);
									}
									catch (final Throwable e)
									{
										System.err.println(e.getLocalizedMessage());
									}
									if (mGlWindow.isRealized())
										mGlWindow.destroy();
								}
								catch (final Throwable e)
								{
									System.err.println(e.getLocalizedMessage());
								}
							});

	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#setWindowTitle(java.lang.String)
	 */
	@Override
	public void setWindowTitle(final String pTitleString)
	{
		runOnEDT(false, () -> {
			mGlWindow.setTitle(pTitleString);
		});
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean pIsVisible)
	{
		runOnEDT(false, () -> {
			mGlWindow.setVisible(pIsVisible);
		});
	}

	public void requestFocus()
	{
		runOnEDT(false, () -> {
			mGlWindow.requestFocus();
		});
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#toggleFullScreen()
	 */
	@Override
	public void toggleFullScreen()
	{
		runOnEDT(	false,
							() -> {
								try
								{
									if (mGlWindow.isFullscreen())
									{
										mGlWindow.setFullscreen(false);
									}
									else
									{
										mGlWindow.setSize(mWindowDefaultWidth,
																			mWindowDefaultHeight);
										mGlWindow.setFullscreen(true);
									}
									mGlWindow.display();
								}
								catch (final Exception e)
								{
									e.printStackTrace();
								}
							});

	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#setPerspectiveProjectionMatrix(float, float, float, float)
	 */
	@Override
	public void setPerspectiveProjectionMatrix(	float fov,
																							float ratio,
																							float nearP,
																							float farP)
	{
		if (mProjectionMatrix != null)
			mProjectionMatrix.setPerspectiveProjectionMatrix(	fov,
																												ratio,
																												nearP,
																												farP);
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#setOrthoProjectionMatrix(float, float, float, float, float, float)
	 */
	@Override
	public void setOrthoProjectionMatrix(	final float left,
																				final float right,
																				final float bottom,
																				final float top,
																				final float zNear,
																				final float zFar)
	{
		if (mProjectionMatrix != null)
			mProjectionMatrix.setOrthoProjectionMatrix(	left,
																									right,
																									bottom,
																									top,
																									zNear,
																									zFar);
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#lookAt(float, float, float, float, float, float, float, float, float)
	 */
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

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#getProjectionMatrix()
	 */
	@Override
	public GLMatrix getProjectionMatrix()
	{
		return mProjectionMatrix;
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#getViewMatrix()
	 */
	@Override
	public GLMatrix getViewMatrix()
	{
		return mViewMatrix;
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#getWindowTitle()
	 */
	@Override
	public String getWindowTitle()
	{
		return mWindowTitle;
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#disableClose()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#isFullscreen()
	 */
	@Override
	public boolean isFullscreen()
	{
		return mGlWindow.isFullscreen();
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#setFullscreen(boolean)
	 */
	@Override
	public void setFullscreen(boolean pFullScreen)
	{
		mGlWindow.setFullscreen(pFullScreen);
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#requestDisplay()
	 */
	@Override
	public void display()
	{
		mGlWindow.display();
	}

	public static boolean isRetina(GL pGL)
	{
		int[] trialSizes = new int[2];

		trialSizes[0] = 512;
		trialSizes[1] = 512;

		if (pGL != null && pGL.getContext() != null
				&& pGL.getContext().getGLDrawable() != null
				&& pGL.getContext().getGLDrawable().getNativeSurface() != null)
			pGL.getContext()
					.getGLDrawable()
					.getNativeSurface()
					.convertToPixelUnits(trialSizes);

		if (trialSizes[0] == 512 && trialSizes[1] == 512)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#setDefaultCloseOperation(javax.media.nativewindow.WindowClosingProtocol.WindowClosingMode)
	 */
	@Override
	public WindowClosingMode setDefaultCloseOperation(WindowClosingMode pWindowClosingMode)
	{
		return mGlWindow.setDefaultCloseOperation(pWindowClosingMode);
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#getHeight()
	 */
	@Override
	public int getHeight()
	{
		int factor = isRetina(this.mGlWindow.getGL()) ? 2 : 1;
		return mGlWindow.getHeight() * factor;
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#getWidth()
	 */
	@Override
	public int getWidth()
	{
		int factor = isRetina(this.mGlWindow.getGL()) ? 2 : 1;
		return mGlWindow.getWidth() * factor;
	}

	@Override
	public void setSize(int pWidth, int pHeight)
	{
		mGlWindow.setSize(pWidth, pHeight);
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#isVisible()
	 */
	@Override
	public boolean isVisible()
	{
		return mGlWindow.isVisible();
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#addMouseListener(com.jogamp.newt.event.MouseListener)
	 */
	@Override
	public void addMouseListener(MouseListener pMouseListener)
	{
		mGlWindow.addMouseListener(pMouseListener);
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#addKeyListener(com.jogamp.newt.event.KeyListener)
	 */
	@Override
	public void addKeyListener(KeyListener pKeyListener)
	{
		mGlWindow.addKeyListener(pKeyListener);
	}

	/* (non-Javadoc)
	 * @see cleargl.ClearGLDisplayable#addWindowListener(com.jogamp.newt.event.WindowAdapter)
	 */
	@Override
	public void addWindowListener(WindowAdapter pWindowAdapter)
	{
		mGlWindow.addWindowListener(pWindowAdapter);
	}

	@Override
	public void setUpdateFPSFrames(	int pFramesPerSecond,
																	PrintStream pPrintStream)
	{
		mGlWindow.setUpdateFPSFrames(pFramesPerSecond, pPrintStream);
	}

	@Override
	public float getLastFPS()
	{
		return mGlWindow.getLastFPS();
	}

	@Override
	public Component getComponent()
	{
		return getNewtCanvasAWT();
	}

	public NewtCanvasAWT getNewtCanvasAWT()
	{
		if (mNewtCanvasAWT == null)
		{
			mNewtCanvasAWT = new NewtCanvasAWT(mGlWindow);
			mNewtCanvasAWT.setShallUseOffscreenLayer(false);
		}

		return mNewtCanvasAWT;
	}

	public GLAutoDrawable getGLAutoDrawable()
	{
		return mGlWindow;
	}

	public void runOnEDT(boolean pWait, Runnable pRunnable)
	{
		mGlWindow.runOnEDTIfAvail(pWait, pRunnable);
	}

}
