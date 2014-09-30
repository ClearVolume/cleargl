package cleargl;

import static javax.media.opengl.GL.GL_TEXTURE_2D;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.media.opengl.GL3;
import javax.media.opengl.GLException;

public class GLTexture implements GLInterface, GLCloseable
{

	private GLInterface mGLInterface;
	private int[] mTextureId = new int[1];
	private int mTextureWidth;
	private int mTextureHeight;

	public GLTexture(	GLInterface pGLInterface,
										int pTextureWidth,
										int pTextureHeight)
	{
		super();
		mGLInterface = pGLInterface;
		mTextureWidth = pTextureWidth;
		mTextureHeight = pTextureHeight;
		mGLInterface.getGL().glGenTextures(1, mTextureId, 0);
		bind();
		mGLInterface.getGL().glTexParameterf(	GL3.GL_TEXTURE_2D,
																					GL3.GL_TEXTURE_MAG_FILTER,
																					GL3.GL_LINEAR);
		mGLInterface.getGL().glTexParameterf(	GL3.GL_TEXTURE_2D,
																					GL3.GL_TEXTURE_MIN_FILTER,
																					GL3.GL_LINEAR);
		mGLInterface.getGL().glTexParameterf(	GL3.GL_TEXTURE_2D,
																					GL3.GL_TEXTURE_WRAP_S,
																					GL3.GL_CLAMP_TO_EDGE);
		mGLInterface.getGL().glTexParameterf(	GL3.GL_TEXTURE_2D,
																					GL3.GL_TEXTURE_WRAP_T,
																					GL3.GL_CLAMP_TO_EDGE);
		mGLInterface.getGL()
								.glTexImage2D(GL3.GL_TEXTURE_2D,
															0,
															GL3.GL_RGBA8,
															mTextureWidth,
															mTextureHeight,
															0,
															GL3.GL_RGBA,
															GL3.GL_UNSIGNED_BYTE,
															ByteBuffer.wrap(new byte[mTextureWidth * mTextureHeight
																												* 4]));
	}

	public void unbind()
	{
		mGLInterface.getGL().glBindTexture(GL3.GL_TEXTURE_2D, 0);
	}

	public void bind(GLProgram pGLProgram)
	{
		pGLProgram.bind();
		bind();
	}

	public void bind()
	{
		mGLInterface.getGL().glActiveTexture(GL3.GL_TEXTURE0);
		mGLInterface.getGL().glBindTexture(GL3.GL_TEXTURE_2D, getId());
	}

	public void copyFrom(GLPixelBufferObject pPixelBufferObject)
	{
		bind();
		pPixelBufferObject.bind();
		mGLInterface.getGL().glTexSubImage2D(	GL3.GL_TEXTURE_2D,
																					0,
																					0,
																					0,
																					mTextureWidth,
																					mTextureHeight,
																					GL3.GL_RGBA,
																					GL3.GL_UNSIGNED_BYTE,
																					0);

		pPixelBufferObject.unbind();
	}

	public void copyFrom(Buffer pBuffer, int pWidth, int pHeight)
	{
		mGLInterface.getGL().glTexParameteri(	GL_TEXTURE_2D,
																					GL3.GL_TEXTURE_WRAP_S,
																					GL3.GL_REPEAT);
		mGLInterface.getGL().glTexParameteri(	GL_TEXTURE_2D,
																					GL3.GL_TEXTURE_WRAP_T,
																					GL3.GL_REPEAT);
		mGLInterface.getGL().glTexParameteri(	GL_TEXTURE_2D,
																					GL3.GL_TEXTURE_MAG_FILTER,
																					GL3.GL_NEAREST);
		mGLInterface.getGL().glTexParameteri(	GL_TEXTURE_2D,
																					GL3.GL_TEXTURE_MIN_FILTER,
																					GL3.GL_NEAREST);
		mGLInterface.getGL().glTexImage2D(GL_TEXTURE_2D,
																			0,
																			GL3.GL_RGBA,
																			pWidth,
																			pHeight,
																			0,
																			GL3.GL_RGBA,
																			GL3.GL_UNSIGNED_BYTE,
																			pBuffer);
	}

	@Override
	public void close() throws GLException
	{
		mGLInterface.getGL().glDeleteTextures(1, mTextureId, 0);
	}

	public int getWidth()
	{
		return mTextureWidth;
	}

	public int getHeight()
	{
		return mTextureHeight;
	}

	@Override
	public GL3 getGL()
	{
		return null;
	}

	@Override
	public int getId()
	{
		return mTextureId[0];
	}

	@Override
	public String toString()
	{
		return "GLTexture [mGLInterface=" + mGLInterface
						+ ", mTextureId="
						+ Arrays.toString(mTextureId)
						+ ", mTextureWidth="
						+ mTextureWidth
						+ ", mTextureHeight="
						+ mTextureHeight
						+ "]";
	}

}
