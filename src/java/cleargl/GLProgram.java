package cleargl;

import javax.media.opengl.GL4;
import javax.media.opengl.GLException;
import java.io.IOException;
import java.util.HashMap;

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

    System.out.println(lGLProgram.getProgramInfoLog());
		return lGLProgram;
	}

  public static GLProgram buildProgram(	GL4 pGL4,
                                         Class<?> pClass,
                                         String[] shaders) throws IOException
  {
    GLProgram lGLProgram = new GLProgram(pGL4, shaderPipelineFromFilenames(pGL4, pClass, shaders));

    System.out.println(lGLProgram.getProgramInfoLog());

    return lGLProgram;
  }

  private static String shaderFileForType(GLShaderType type, String[] shaders) {
    HashMap<GLShaderType, String> glslFilenameMapping = new HashMap<>();

    glslFilenameMapping.put(GLShaderType.VertexShader, "_vert.glsl");
    glslFilenameMapping.put(GLShaderType.GeometryShader, "_geom.glsl");
    glslFilenameMapping.put(GLShaderType.TesselationControlShader, "_tess_ctrl.glsl");
    glslFilenameMapping.put(GLShaderType.TesselationEvaluationShader, "_tess_eval.glsl");
    glslFilenameMapping.put(GLShaderType.FragmentShader, "_frag.glsl");

    for (int i = 0; i < shaders.length; i++) {
      if(shaders[i].endsWith(glslFilenameMapping.get(type))) {
        return shaders[i];
      }
    }

    return null;
  }

  private static HashMap<GLShaderType, GLShader> shaderPipelineFromFilenames(GL4 pGL4, Class<?> rootClass, String[] shaders) throws IOException {
    HashMap<GLShaderType, GLShader> pipeline  = new HashMap<>();

    for(GLShaderType type: GLShaderType.values()) {
      String filename = shaderFileForType(type, shaders);
      if(filename != null) {
        GLShader shader = new GLShader(pGL4, rootClass, filename, type);
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

		mGL4 = pVerteShader.getGL();

		final int lVertexShaderId = mVerteShader.getId();
		final int lFragmentShaderId = mFragmentShader.getId();

		mProgramId = mGL4.glCreateProgram();
		mGL4.glAttachShader(mProgramId, lVertexShaderId);
		mGL4.glAttachShader(mProgramId, lFragmentShaderId);
		mGL4.glLinkProgram(mProgramId);

		mGL4.glBindFragDataLocation(mProgramId, 0, "outColor");
	}

  public GLProgram(GL4 pGL4, HashMap<GLShaderType, GLShader> pipeline) {
    super();

    mGL4 = pGL4;

    mProgramId = mGL4.glCreateProgram();

    for(GLShader shader: pipeline.values()) {
      mGL4.glAttachShader(mProgramId, shader.getId());
    }

    mGL4.glLinkProgram(mProgramId);
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
