package cleargl;

import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL3;
import javax.media.opengl.GLException;

import org.apache.commons.io.IOUtils;

public class GLShader implements GLInterface, GLCloseable
{
	private GL3 mGL3;
	private int mShaderId;
	private GLShaderType mShaderType;
	private String mShaderSource;

	public GLShader(GL3 pGL3,
	                Class<?> pRootClass,
									String pRessourceName,
									GLShaderType pShaderType) throws IOException
	{
		super();
		mGL3 = pGL3;
		InputStream lResourceAsStream = pRootClass.getResourceAsStream(pRessourceName);
		mShaderSource = IOUtils.toString(lResourceAsStream, "UTF-8");
		mShaderType = pShaderType;

		int lShaderTypeInt = mShaderType == GLShaderType.VertexShader	? GL3.GL_VERTEX_SHADER
																																	: GL3.GL_FRAGMENT_SHADER;

		mShaderId = pGL3.glCreateShader(lShaderTypeInt);
		mGL3.glShaderSource(mShaderId, 1, new String[]
		{ mShaderSource }, null);
		mGL3.glCompileShader(mShaderId);

	}

	@Override
	public void close() throws GLException
	{
		mGL3.glDeleteShader(mShaderId);
	}

	public String getShaderInfoLog()
	{
		final int logLen = getShaderParameter(GL3.GL_INFO_LOG_LENGTH);
		if (logLen <= 0)
			return "";

		final int[] lLength = new int[1];
		final byte[] lBytes = new byte[logLen + 1];
		mGL3.glGetShaderInfoLog(mShaderId, logLen, lLength, 0, lBytes, 0);
		final String logMessage = new String(lBytes);
		return logMessage;
	}

	public int getShaderParameter(int pParamName)
	{
		final int lParameter[] = new int[1];
		mGL3.glGetShaderiv(mShaderId, pParamName, lParameter, 0);
		return lParameter[0];
	}


	@Override
	public int getId()
	{
		return mShaderId;
	}

	@Override
	public GL3 getGL()
	{
		return mGL3;
	}

	@Override
	public String toString()
	{
		return "GLShader [mGL3=" + mGL3
						+ ", mShaderId="
						+ mShaderId
						+ ", mShaderType="
						+ mShaderType
						+ ", mShaderSource="
						+ mShaderSource
						+ "]";
	}

}
