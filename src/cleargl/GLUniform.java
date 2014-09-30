package cleargl;

import java.nio.FloatBuffer;

import javax.media.opengl.GL3;

public class GLUniform implements GLInterface
{
	private GLProgram mGlProgram;
	private int mUniformId;

	public GLUniform(GLProgram pGlProgram, int pUniformId)
	{
		mGlProgram = pGlProgram;
		mUniformId = pUniformId;
	}

	public void setFloatMatrix(	float[] pProjectionMatrix,
															boolean pTranspose)
	{
		mGlProgram.bind();
		mGlProgram.getGL().glUniformMatrix4fv(mUniformId,
																					1,
																					pTranspose,
																					pProjectionMatrix,
																					0);
	}

	public void setFloatMatrix(	FloatBuffer pProjectionMatrix,
															boolean pTranspose)
	{
		mGlProgram.bind();
		mGlProgram.getGL().glUniformMatrix4fv(mUniformId,
																					1,
																					pTranspose,
																					pProjectionMatrix);
	}

	public void setInt(int pInt)
	{
		mGlProgram.bind();
		mGlProgram.getGL().glUniform1i(mUniformId, pInt);
	}

	@Override
	public GL3 getGL()
	{
		return mGlProgram.getGL();
	}

	@Override
	public int getId()
	{
		return mUniformId;
	}

	@Override
	public String toString()
	{
		return "GLUniform [mGlProgram=" + mGlProgram
						+ ", mUniformId="
						+ mUniformId
						+ "]";
	}

}
