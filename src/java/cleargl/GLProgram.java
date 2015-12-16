package cleargl;

import java.io.IOException;
import java.util.HashMap;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLException;

public class GLProgram implements GLInterface, GLCloseable
{
	private GL mGL;
	private final int mProgramId;
	private GLShader mVerteShader;
	private GLShader mFragmentShader;

	public static GLProgram buildProgram(	GL pGL,
																				Class<?> pClass,
																				String pVertexShaderRessourcePath,
																				String pFragmentShaderRessourcePath) throws IOException
	{
		final GLShader lVertexShader = new GLShader(pGL,
																								pClass,
																								pVertexShaderRessourcePath,
																								GLShaderType.VertexShader);
		System.out.println(lVertexShader.getShaderInfoLog());

		final GLShader lFragmentShader = new GLShader(pGL,
																									pClass,
																									pFragmentShaderRessourcePath,
																									GLShaderType.FragmentShader);
		System.out.println(lFragmentShader.getShaderInfoLog());
		final GLProgram lGLProgram = new GLProgram(	lVertexShader,
																								lFragmentShader);
		return lGLProgram;
	}

	public static GLProgram buildProgram(	GL pGL,
																				String pVertexShaderSourceAsString,
																				String pFragmentShaderSourceAsString) throws IOException
	{
		final GLShader lVertexShader = new GLShader(pGL,
																								pVertexShaderSourceAsString,
																								GLShaderType.VertexShader);
		System.out.println(lVertexShader.getShaderInfoLog());

		final GLShader lFragmentShader = new GLShader(pGL,
																									pFragmentShaderSourceAsString,
																									GLShaderType.FragmentShader);
		System.out.println(lFragmentShader.getShaderInfoLog());
		final GLProgram lGLProgram = new GLProgram(	lVertexShader,
																								lFragmentShader);

		System.out.println(lGLProgram.getProgramInfoLog());
		return lGLProgram;
	}

	public static GLProgram buildProgram(	GL pGL,
																				Class<?> pClass,
																				String[] shaders) throws IOException
	{
		final GLProgram lGLProgram = new GLProgram(	pGL,
																								shaderPipelineFromFilenames(pGL,
																																						pClass,
																																						shaders));

		System.out.println(lGLProgram.getProgramInfoLog());

		return lGLProgram;
	}

	private static String shaderFileForType(GLShaderType type,
																					String[] shaders)
	{
		final HashMap<GLShaderType, String> glslFilenameMapping = new HashMap<>();

		glslFilenameMapping.put(GLShaderType.VertexShader, ".vs");
		glslFilenameMapping.put(GLShaderType.GeometryShader, ".gs");
		glslFilenameMapping.put(GLShaderType.TesselationControlShader,
														".tcs");
		glslFilenameMapping.put(GLShaderType.TesselationEvaluationShader,
														".tes");
		glslFilenameMapping.put(GLShaderType.FragmentShader, ".fs");

		for (int i = 0; i < shaders.length; i++)
		{
			if (shaders[i].endsWith(glslFilenameMapping.get(type)))
			{
				return shaders[i];
			}
		}

		return null;
	}

	private static HashMap<GLShaderType, GLShader> shaderPipelineFromFilenames(	GL pGL,
																																							Class<?> rootClass,
																																							String[] shaders) throws IOException
	{
		final HashMap<GLShaderType, GLShader> pipeline = new HashMap<>();

		for (final GLShaderType type : GLShaderType.values())
		{
			final String filename = shaderFileForType(type, shaders);
			if (filename != null)
			{
				final GLShader shader = new GLShader(	pGL,
																							rootClass,
																							filename,
																							type);
				System.out.println(shader.getShaderInfoLog());
				pipeline.put(type, shader);
			}
		}

		return pipeline;
	}

	public GLProgram(GLShader pVerteShader, GLShader pFragmentShader)
	{
		super();
		mVerteShader = pVerteShader;
		mFragmentShader = pFragmentShader;

		mGL = pVerteShader.getGL();

		final int lVertexShaderId = mVerteShader.getId();
		final int lFragmentShaderId = mFragmentShader.getId();

		mProgramId = mGL.getGL3().glCreateProgram();
		mGL.getGL3().glAttachShader(mProgramId, lVertexShaderId);
		mGL.getGL3().glAttachShader(mProgramId, lFragmentShaderId);
		mGL.getGL3().glLinkProgram(mProgramId);

		mGL.getGL3().glBindFragDataLocation(mProgramId, 0, "outColor");
	}

	public GLProgram(GL pGL, HashMap<GLShaderType, GLShader> pipeline)
	{
		super();

		mGL = pGL;

		mProgramId = mGL.getGL3().glCreateProgram();

		for (final GLShader shader : pipeline.values())
		{
			mGL.getGL3().glAttachShader(mProgramId, shader.getId());
		}

		mGL.getGL3().glLinkProgram(mProgramId);
	}

	@Override
	public void close() throws GLException
	{
		mGL.getGL3().glDeleteProgram(mProgramId);
	}

	public GLAttribute getAtribute(String pAttributeName)
	{
		final int lAttributeId = mGL.getGL3()
																.glGetAttribLocation(	mProgramId,
																											pAttributeName);
		final GLAttribute lGLAttribute = new GLAttribute(	this,
																											lAttributeId);
		return lGLAttribute;
	}

	public GLUniform getUniform(String pUniformName)
	{
		final int lUniformId = mGL.getGL3()
															.glGetUniformLocation(mProgramId,
																										pUniformName);
		final GLUniform lGLUniform = new GLUniform(this, lUniformId);
		return lGLUniform;
	}

	public void bind()
	{
		mGL.getGL3().glUseProgram(mProgramId);
	}

	public void unbind()
	{
		mGL.getGL3().glUseProgram(0);
	}

	public void use(GL pGL)
	{
		mGL = pGL;
		bind();
	}

	public String getProgramInfoLog()
	{
		final int lLogLength = getProgramParameter(GL2ES2.GL_INFO_LOG_LENGTH);
		if (lLogLength <= 0)
			return "";

		final int[] lLength = new int[1];
		final byte[] lBytes = new byte[lLogLength + 1];
		mGL.getGL3().glGetProgramInfoLog(	mProgramId,
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
		mGL.getGL3().glGetProgramiv(mProgramId,
																pParameterName,
																lParameter,
																0);
		return lParameter[0];
	}

	public void setGL(GL pGL)
	{
		mGL = pGL;
	}

	@Override
	public GL getGL()
	{
		return mGL;
	}

	@Override
	public int getId()
	{
		return mProgramId;
	}

	@Override
	public String toString()
	{
		return "GLProgram [mGL=" + mGL
						+ ", mProgramId="
						+ mProgramId
						+ ", mVertexShader="
						+ mVerteShader
						+ ", mFragmentShader="
						+ mFragmentShader
						+ "]";
	}

}
