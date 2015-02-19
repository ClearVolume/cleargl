package cleargl;

import javax.media.opengl.GLEventListener;

public interface ClearGLEventListener extends GLEventListener
{

	void setClearGLWindow(ClearGLWindow pClearGLWindow);

	ClearGLDisplayable getClearGLWindow();

}
