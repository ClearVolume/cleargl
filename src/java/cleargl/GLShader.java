package cleargl;

import org.apache.commons.io.IOUtils;

import javax.media.opengl.GL4;
import javax.media.opengl.GLException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class GLShader implements GLInterface, GLCloseable
{
	private GL4 mGL4;
	private int mShaderId;
	private GLShaderType mShaderType;
	private String mShaderSource;

	public GLShader(GL4 pGL4,
	                Class<?> pRootClass,
									String pRessourceName,
									GLShaderType pShaderType) throws IOException
	{
		super();
		mGL4 = pGL4;
		InputStream lResourceAsStream = pRootClass.getResourceAsStream(pRessourceName);
		mShaderSource = IOUtils.toString(lResourceAsStream, "UTF-8");
		mShaderType = pShaderType;

    HashMap<GLShaderType, Integer> glShaderTypeMapping = new HashMap<>();
    glShaderTypeMapping.put(GLShaderType.VertexShader, GL4.GL_VERTEX_SHADER);
    glShaderTypeMapping.put(GLShaderType.GeometryShader, GL4.GL_GEOMETRY_SHADER);
    glShaderTypeMapping.put(GLShaderType.TesselationControlShader, GL4.GL_TESS_CONTROL_SHADER);
    glShaderTypeMapping.put(GLShaderType.TesselationEvaluationShader, GL4.GL_TESS_EVALUATION_SHADER);
    glShaderTypeMapping.put(GLShaderType.FragmentShader, GL4.GL_FRAGMENT_SHADER);

    int lShaderTypeInt = glShaderTypeMapping.get(pShaderType);

		mShaderId = pGL4.glCreateShader(lShaderTypeInt);
		mGL4.glShaderSource(mShaderId, 1, new String[]
		{ mShaderSource }, null);
		mGL4.glCompileShader(mShaderId);

	}

	public GLShader(GL4 pGL4,
									String pShaderSourceAsString,
									GLShaderType pShaderType) throws IOException
	{
		super();
		mGL4 = pGL4;
		mShaderSource = pShaderSourceAsString;
		mShaderType = pShaderType;

    HashMap<GLShaderType, Integer> glShaderTypeMapping = new HashMap<>();
    glShaderTypeMapping.put(GLShaderType.VertexShader, GL4.GL_VERTEX_SHADER);
    glShaderTypeMapping.put(GLShaderType.GeometryShader, GL4.GL_GEOMETRY_SHADER);
    glShaderTypeMapping.put(GLShaderType.TesselationControlShader, GL4.GL_TESS_CONTROL_SHADER);
    glShaderTypeMapping.put(GLShaderType.TesselationEvaluationShader, GL4.GL_TESS_EVALUATION_SHADER);
    glShaderTypeMapping.put(GLShaderType.FragmentShader, GL4.GL_FRAGMENT_SHADER);

		int lShaderTypeInt = glShaderTypeMapping.get(pShaderType);

		mShaderId = pGL4.glCreateShader(lShaderTypeInt);
		mGL4.glShaderSource(mShaderId, 1, new String[]
		{ mShaderSource }, null);
		mGL4.glCompileShader(mShaderId);

	}

	@Override
	public void close() throws GLException
	{
		mGL4.glDeleteShader(mShaderId);
	}

	public String getShaderInfoLog()
	{
		final int logLen = getShaderParameter(GL4.GL_INFO_LOG_LENGTH);
		if (logLen <= 0)
			return "";

		final int[] lLength = new int[1];
		final byte[] lBytes = new byte[logLen + 1];
		mGL4.glGetShaderInfoLog(mShaderId, logLen, lLength, 0, lBytes, 0);
		final String logMessage = new String(lBytes);
		return logMessage;
	}

	public int getShaderParameter(int pParamName)
	{
		final int lParameter[] = new int[1];
		mGL4.glGetShaderiv(mShaderId, pParamName, lParameter, 0);
		return lParameter[0];
	}


	@Override
	public int getId()
	{
		return mShaderId;
	}

	@Override
	public GL4 getGL()
	{
		return mGL4;
	}

	@Override
	public String toString()
	{
		return "GLShader [mGL4=" + mGL4
						+ ", mShaderId="
						+ mShaderId
						+ ", mShaderType="
						+ mShaderType
						+ ", mShaderSource="
						+ mShaderSource
						+ "]";
	}

}
