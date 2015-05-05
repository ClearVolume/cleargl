package cleargl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public class GLError
{

	private static final GLU sGLU = new GLU();

	public static final String printGLErrors(	final GL lGL,
																						String lDescription)
	{
		final int lGLErrorCode = lGL.glGetError();
		final String lGLErrorStr = sGLU.gluErrorString(lGLErrorCode);
		if (lGLErrorCode != 0)
			System.err.format("OPENGL ERROR %s #%d : %s \n",
												lDescription,
												lGLErrorCode,
												lGLErrorStr);
		return lGLErrorStr;
	}
}
