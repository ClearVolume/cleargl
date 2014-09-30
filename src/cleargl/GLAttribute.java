package cleargl;

import javax.media.opengl.GL3;

public class GLAttribute implements GLInterface
{
	private GLProgram mGlProgram;
	private int mAttributeIndex;


	public GLAttribute(GLProgram pGlProgram, int pAttributeId)
	{
		mGlProgram = pGlProgram;
		mAttributeIndex = pAttributeId;
	}

	@Override
	public GL3 getGL()
	{
		return mGlProgram.getGL();
	}

	@Override
	public int getId()
	{
		return mAttributeIndex;
	}

	public int getIndex()
	{
		return mAttributeIndex;
	}

	@Override
	public String toString()
	{
		return "GLAttribute [mGlProgram=" + mGlProgram
						+ ", mAttributeIndex="
						+ mAttributeIndex
						+ "]";
	}

}
