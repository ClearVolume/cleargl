package cleargl;

import java.nio.FloatBuffer;

import javax.media.opengl.GL4;

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

	public void set(int pInt)
	{
		mGlProgram.bind();
		mGlProgram.getGL().glUniform1i(mUniformId, pInt);
	}

	public void set(float pFloat)
	{
		mGlProgram.bind();
		mGlProgram.getGL().glUniform1f(mUniformId, pFloat);
	}

	public void set(double pDouble)
	{
		mGlProgram.bind();
		mGlProgram.getGL().glUniform1d(mUniformId, pDouble);
	}

	@Override
	public GL4 getGL()
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
