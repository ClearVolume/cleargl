package cleargl;

import java.nio.Buffer;
import java.util.Arrays;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLException;

public class GLPixelBufferObject implements GLInterface, GLCloseable
{
	private GLInterface mGLInterface;
	private int[] mPixelBufferObjectId = new int[1];
	private int mTextureWidth;
	private int mTextureHeight;

	public GLPixelBufferObject(	GLInterface pGLInterface,
 int pWidth, int pHeight)
	{
		super();
		mGLInterface = pGLInterface;
		mTextureWidth = pWidth;
		mTextureHeight = pHeight;

		mGLInterface.getGL().glGenBuffers(1, mPixelBufferObjectId, 0);

	}

	public void bind()
	{
		mGLInterface.getGL().glBindBuffer(GL3.GL_PIXEL_UNPACK_BUFFER,
																			getId());
	}

	public void unbind()
	{
		mGLInterface.getGL().glBindBuffer(GL3.GL_PIXEL_UNPACK_BUFFER, 0);
	}

	public void copyFrom(Buffer pBuffer)
	{
		bind();
		mGLInterface.getGL().glBufferData(GL3.GL_PIXEL_UNPACK_BUFFER,
											mTextureWidth * mTextureHeight * 1 * 4,
											null,
											GL.GL_DYNAMIC_DRAW);
	}

	@Override
	public void close() throws GLException
	{
		mGLInterface.getGL().glDeleteBuffers(1, mPixelBufferObjectId, 0);
		mPixelBufferObjectId = null;
	}


	@Override
	public GL3 getGL()
	{
		return mGLInterface.getGL();
	}

	@Override
	public int getId()
	{
		return mPixelBufferObjectId[0];
	}

	@Override
	public String toString()
	{
		return "GLPixelBufferObject [mGLInterface=" + mGLInterface
						+ ", mPixelBufferObjectId="
						+ Arrays.toString(mPixelBufferObjectId)
						+ ", mTextureWidth="
						+ mTextureWidth
						+ ", mTextureHeight="
						+ mTextureHeight
						+ "]";
	}

}
