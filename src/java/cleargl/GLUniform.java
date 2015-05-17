package cleargl;

import java.nio.FloatBuffer;

import com.jogamp.opengl.GL;

public class GLUniform implements GLInterface
{
	private final GLProgram mGlProgram;
	private final int mUniformId;

	public GLUniform(GLProgram pGlProgram, int pUniformId)
	{
		mGlProgram = pGlProgram;
		mUniformId = pUniformId;
	}

	public void setFloatMatrix(	float[] pProjectionMatrix,
															boolean pTranspose)
	{
		mGlProgram.bind();
		mGlProgram.getGL()
							.getGL3()
							.glUniformMatrix4fv(mUniformId,
																	1,
																	pTranspose,
																	pProjectionMatrix,
																	0);
	}

	public void setFloatMatrix(	FloatBuffer pProjectionMatrix,
															boolean pTranspose)
	{
		mGlProgram.bind();
		mGlProgram.getGL()
							.getGL3()
							.glUniformMatrix4fv(mUniformId,
																	1,
																	pTranspose,
																	pProjectionMatrix);
	}

	public void setFloatVector2(float... pVector2)
	{
		setFloatVector2(FloatBuffer.wrap(pVector2));
	}

	public void setFloatVector2(FloatBuffer pVector)
	{
		mGlProgram.bind();
		mGlProgram.getGL().getGL3().glUniform2fv(mUniformId, 1, pVector);
	}

	public void setFloatVector3(float... pVector3)
	{
		setFloatVector3(FloatBuffer.wrap(pVector3));
	}

	public void setFloatVector3(FloatBuffer pVector)
	{
		mGlProgram.bind();
		mGlProgram.getGL().getGL3().glUniform3fv(mUniformId, 1, pVector);
	}

	public void setFloatVector4(float... pVector4)
	{
		setFloatVector4(FloatBuffer.wrap(pVector4));
	}

	public void setFloatVector4(FloatBuffer pVector)
	{
		mGlProgram.bind();
		mGlProgram.getGL().getGL3().glUniform4fv(mUniformId, 1, pVector);
	}

	public void setInt(int pInt)
	{
		mGlProgram.bind();
		mGlProgram.getGL().getGL3().glUniform1i(mUniformId, pInt);
	}

	public void setFloat(float pFloat)
	{
		mGlProgram.bind();
		mGlProgram.getGL().getGL3().glUniform1f(mUniformId, pFloat);
	}

	/*public void set(double pDouble)
	{
		mGlProgram.bind();
		mGlProgram.getGL().getGL4().glUniform1d(mUniformId, pDouble);
	}/**/

	@Override
	public GL getGL()
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
