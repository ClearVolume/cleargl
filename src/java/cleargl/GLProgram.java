package cleargl;

import javax.media.opengl.GL4;
import javax.media.opengl.GLException;
import java.io.IOException;

public class GLProgram implements GLInterface, GLCloseable
{
	private GL4 mGL4;
	private int mProgramId;
	private GLShader mVerteShader;
	private GLShader mFragmentShader;

	public static GLProgram buildProgram(	GL4 pGL4,
																				Class<?> pClass,
																				String pVertexShaderRessourcePath,
																				String pFragmentShaderRessourcePath) throws IOException
	{
		GLShader lVertexShader = new GLShader(pGL4,
																					pClass,
																					pVertexShaderRessourcePath,
																					GLShaderType.VertexShader);
		System.out.println(lVertexShader.getShaderInfoLog());

		GLShader lFragmentShader = new GLShader(pGL4,
																						pClass,
																						pFragmentShaderRessourcePath,
																						GLShaderType.FragmentShader);
		System.out.println(lFragmentShader.getShaderInfoLog());
		GLProgram lGLProgram = new GLProgram(	lVertexShader,
																					lFragmentShader);
		return lGLProgram;
	}

	public static GLProgram buildProgram(	GL4 pGL4,
																				String pVertexShaderSourceAsString,
																				String pFragmentShaderSourceAsString) throws IOException
	{
		GLShader lVertexShader = new GLShader(pGL4,
																					pVertexShaderSourceAsString,
																					GLShaderType.VertexShader);
		System.out.println(lVertexShader.getShaderInfoLog());

		GLShader lFragmentShader = new GLShader(pGL4,
																						pFragmentShaderSourceAsString,
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

		mGL4 = pVerteShader.getGL();

		final int lVertexShaderId = mVerteShader.getId();
		final int lFragmentShaderId = mFragmentShader.getId();

		mProgramId = mGL4.glCreateProgram();
		mGL4.glAttachShader(mProgramId, lVertexShaderId);
		mGL4.glAttachShader(mProgramId, lFragmentShaderId);
		mGL4.glLinkProgram(mProgramId);

		mGL4.glBindFragDataLocation(mProgramId, 0, "outColor");
	}

	@Override
	public void close() throws GLException
	{
		mGL4.glDeleteProgram(mProgramId);
	}

	public GLAttribute getAtribute(String pAttributeName)
	{
		int lAttributeId = mGL4.glGetAttribLocation(mProgramId,
																								pAttributeName);
		GLAttribute lGLAttribute = new GLAttribute(this, lAttributeId);
		return lGLAttribute;
	}

	public GLUniform getUniform(String pUniformName)
	{
		int lUniformId = mGL4.glGetUniformLocation(	mProgramId,
																								pUniformName);
		GLUniform lGLUniform = new GLUniform(this, lUniformId);
		return lGLUniform;
	}

	public void bind()
	{
		mGL4.glUseProgram(mProgramId);
	}

	public void unbind()
	{
		mGL4.glUseProgram(0);
	}

	public void use(GL4 pGL4)
	{
		mGL4 = pGL4;
		bind();
	}

	public String getProgramInfoLog()
	{
		final int lLogLength = getProgramParameter(GL4.GL_INFO_LOG_LENGTH);
		if (lLogLength <= 0)
			return "";

		final int[] lLength = new int[1];
		final byte[] lBytes = new byte[lLogLength + 1];
		mGL4.glGetProgramInfoLog(	mProgramId,
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
		mGL4.glGetProgramiv(mProgramId, pParameterName, lParameter, 0);
		return lParameter[0];
	}

	public void setGL(GL4 pGL4)
	{
		mGL4 = pGL4;
	}

	@Override
	public GL4 getGL()
	{
		return mGL4;
	}

	@Override
	public int getId()
	{
		return mProgramId;
	}

	@Override
	public String toString()
	{
		return "GLProgram [mGL4=" + mGL4
						+ ", mProgramId="
						+ mProgramId
						+ ", mVerteShader="
						+ mVerteShader
						+ ", mFragmentShader="
						+ mFragmentShader
						+ "]";
	}

}
