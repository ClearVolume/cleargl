package cleargl;

import java.awt.Component;
import java.io.PrintStream;
import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.awt.AWTKeyAdapter;
import com.jogamp.newt.event.awt.AWTMouseAdapter;
import com.jogamp.newt.event.awt.AWTWindowAdapter;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;

public class ClearGLJPanel implements ClearGLDisplayable

{
	private static final long serialVersionUID = 1L;

	private final GLJPanel mGLJPanel;

	private final GLMatrix mProjectionMatrix;
	private final GLMatrix mViewMatrix;

	public ClearGLJPanel(final ClearGLEventListener pClearGLEventListener) {
		final GLProfile lProfile = GLProfile.getMaxProgrammable(true);
		final GLCapabilities lGlCapabilities = new GLCapabilities(lProfile);
		mGLJPanel = new GLJPanel(lGlCapabilities);

		pClearGLEventListener.setClearGLWindow(null);
		getGLJPanel().addGLEventListener(pClearGLEventListener);
		getGLJPanel().setAutoSwapBufferMode(true);

		mProjectionMatrix = new GLMatrix();
		mViewMatrix = new GLMatrix();
	}

	@Override
	public void close() throws GLException {
	}

	@Override
	public void setPerspectiveProjectionMatrix(final float fov,
			final float ratio,
			final float nearP,
			final float farP) {
		mProjectionMatrix.setPerspectiveProjectionMatrix(fov,
				ratio,
				nearP,
				farP);
	}

	@Override
	public void setOrthoProjectionMatrix(final float left,
			final float right,
			final float bottom,
			final float top,
			final float zNear,
			final float zFar) {
		mProjectionMatrix.setOrthoProjectionMatrix(left,
				right,
				bottom,
				top,
				zNear,
				zFar);
	}

	@Override
	public void lookAt(final float pPosX,
			final float pPosY,
			final float pPosZ,
			final float pLookAtX,
			final float pLookAtY,
			final float pLookAtZ,
			final float pUpX,
			final float pUpY,
			final float pUpZ) {
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
	public GLMatrix getProjectionMatrix() {
		return mProjectionMatrix;
	}

	@Override
	public GLMatrix getViewMatrix() {
		return mViewMatrix;
	}

	@Override
	public String toString() {
		return "ClearGLJPanel [mProjectionMatrix=" + mProjectionMatrix
				+ ", mViewMatrix="
				+ mViewMatrix
				+ "]";
	}

	// @Override
	public WindowClosingMode setDdefaultCloseOperation(final WindowClosingMode pWindowClosingMode) {
		return getGLJPanel().setDefaultCloseOperation(pWindowClosingMode);
	}

	@Override
	public void setWindowTitle(final String pTitleString) {
		// no title
	}

	@Override
	public void toggleFullScreen() {
		// no full screen
	}

	@Override
	public String getWindowTitle() {
		return "";
	}

	@Override
	public void disableClose() {
		// no disable close
	}

	@Override
	public boolean isFullscreen() {
		return false;
	}

	@Override
	public void setFullscreen(final boolean pFullScreen) {
		// no full screen
	}

	@Override
	public void display() {
		getGLJPanel().repaint();
	}

	@Override
	public void addMouseListener(final MouseListener pMouseListener) {
		new AWTMouseAdapter(pMouseListener,
				getGLJPanel().getDelegatedDrawable()).addTo(getGLJPanel());
	}

	@Override
	public void addKeyListener(final KeyListener pKeyListener) {
		new AWTKeyAdapter(pKeyListener,
				getGLJPanel().getDelegatedDrawable()).addTo(getGLJPanel());
	}

	@Override
	public void addWindowListener(final WindowAdapter pWindowAdapter) {
		new AWTWindowAdapter(pWindowAdapter,
				getGLJPanel().getDelegatedDrawable()).addTo(getGLJPanel());
	}

	@Override
	public void setUpdateFPSFrames(final int pFramesPerSecond,
			final PrintStream pPrintStream) {
		// no fps
	}

	@Override
	public float getLastFPS() {
		// no fps
		return 0;
	}

	@Override
	public float getAspectRatio() {
		return getWidth() / getHeight();
	}

	@Override
	public WindowClosingMode setDefaultCloseOperation(final WindowClosingMode pWindowClosingMode) {
		return null;
	}

	@Override
	public int getHeight() {
		return getGLJPanel().getHeight();
	}

	@Override
	public int getWidth() {
		return getGLJPanel().getWidth();
	}

	@Override
	public void setSize(final int pWindowWidth, final int pWindowHeight) {
		getGLJPanel().setSize(pWindowWidth, pWindowHeight);
	}

	@Override
	public void setVisible(final boolean pIsVisible) {
		getGLJPanel().setVisible(pIsVisible);
	}

	@Override
	public boolean isVisible() {
		return getGLJPanel().isVisible();
	}

	public GLJPanel getGLJPanel() {
		return mGLJPanel;
	}

	@Override
	public Component getComponent() {
		return mGLJPanel;
	}

	@Override
	public float[] getBounds() {
		return new float[]{0.0f, 0.0f, getWidth(), getHeight()};
	}

}
