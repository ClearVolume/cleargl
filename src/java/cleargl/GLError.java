package cleargl;

import javax.media.opengl.GL4;
import javax.media.opengl.glu.GLU;

public class GLError
{

	private static final GLU sGLU = new GLU();

	public static final String printGLErrors(	final GL4 lGL4,
																						String lDescription)
	{
		int lGLErrorCode = lGL4.glGetError();
		String lGLErrorStr = sGLU.gluErrorString(lGLErrorCode);
		if (lGLErrorCode != 0)
			System.err.format("OPENGL ERROR %s #%d : %s \n",
												lDescription,
												lGLErrorCode,
												lGLErrorStr);
		return lGLErrorStr;
	}
}
