package cleargl;

import java.io.IOException;

import javax.media.opengl.GL3;
import javax.media.opengl.GLException;

public class GLProgram implements GLInterface, GLCloseable
{
	private GL3 mGL3;
	private int mProgramId;
	private GLShader mVerteShader;
	private GLShader mFragmentShader;

	public static GLProgram buildProgram(	GL3 pGL3,
																				Class<?> pClass,
																				String pVertexShader,
																				String pFragmentShader) throws IOException
	{
		GLShader lVertexShader = new GLShader(pGL3,
																					pClass,
																					pVertexShader,
																					GLShaderType.VertexShader);
		System.out.println(lVertexShader.getShaderInfoLog());

		GLShader lFragmentShader = new GLShader(pGL3,
																						pClass,
																						pFragmentShader,
																						GLShaderType.FragmentShader);
		System.out.println(lFragmentShader.getShaderInfoLog());
		GLProgram lGLProgram = new GLProgram(	lVertexShader,
																					lFragmentShader);
		return lGLProgram;
	}

	public GLProgram(GLShader pVerteShader, GLShader pFragmentShader)
	{
		super();
		mVerteShader = pVerteShader;
		mFragmentShader = pFragmentShader;

		mGL3 = pVerteShader.getGL();

		final int lVertexShaderId = mVerteShader.getId();
		final int lFragmentShaderId = mFragmentShader.getId();

		mProgramId = mGL3.glCreateProgram();
		mGL3.glAttachShader(mProgramId, lVertexShaderId);
		mGL3.glAttachShader(mProgramId, lFragmentShaderId);
		mGL3.glLinkProgram(mProgramId);

		mGL3.glBindFragDataLocation(mProgramId, 0, "outColor");
	}

	@Override
	public void close() throws GLException
	{
		mGL3.glDeleteProgram(mProgramId);
	}

	public GLAttribute getAtribute(String pAttributeName)
	{
		int lAttributeId = mGL3.glGetAttribLocation(mProgramId,
																								pAttributeName);
		GLAttribute lGLAttribute = new GLAttribute(this, lAttributeId);
		return lGLAttribute;
	}

	public GLUniform getUniform(String pUniformName)
	{
		int lUniformId = mGL3.glGetUniformLocation(	mProgramId,
																								pUniformName);
		GLUniform lGLUniform = new GLUniform(this, lUniformId);
		return lGLUniform;
	}

	public void bind()
	{
		mGL3.glUseProgram(mProgramId);
	}

	public void unbind()
	{
		mGL3.glUseProgram(0);
	}

	public void use(GL3 pGL3)
	{
		mGL3 = pGL3;
		bind();
	}

	public String getProgramInfoLog()
	{
		final int lLogLength = getProgramParameter(GL3.GL_INFO_LOG_LENGTH);
		if (lLogLength <= 0)
			return "";

		final int[] lLength = new int[1];
		final byte[] lBytes = new byte[lLogLength + 1];
		mGL3.glGetProgramInfoLog(	mProgramId,
															lLogLength,
															lLength,
															0,
															lBytes,
															0);
		final String logMessage = new String(lBytes);

		return logMessage;
	}

	public int getProgramParameter(int pParameterName)
	{
		final int lParameter[] = new int[1];
		mGL3.glGetProgramiv(mProgramId, pParameterName, lParameter, 0);
		return lParameter[0];
	}

	public void setGL(GL3 pGL3)
	{
		mGL3 = pGL3;
	}

	@Override
	public GL3 getGL()
	{
		return mGL3;
	}

	@Override
	public int getId()
	{
		return mProgramId;
	}

	@Override
	public String toString()
	{
		return "GLProgram [mGL3=" + mGL3
						+ ", mProgramId="
						+ mProgramId
						+ ", mVerteShader="
						+ mVerteShader
						+ ", mFragmentShader="
						+ mFragmentShader
						+ "]";
	}

}
