package cleargl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import javax.media.opengl.GL4;
import javax.media.opengl.GLException;

import com.jogamp.common.nio.Buffers;

public class GLTexture<T> implements GLInterface, GLCloseable
{

	private GLInterface mGLInterface;
	private final int[] mTextureId = new int[1];
	private Class<T> mType;
	private int mBytesPerChannel;
	private int mTextureWidth;
	private int mTextureHeight;
	private int mTextureOpenGLDataType;
	private int mTextureOpenGLFormat;
	private int mTextureOpenGLInternalFormat;
	private int mMipMapLevels;
	private int mTextureDepth;
	private int mTextureTarget;
	private int mNumberOfChannels;

	// empty buffer
	private Buffer mEmptyBuffer;

	public GLTexture(	GLInterface pGLInterface,
	                 	Class<T> pType,
	                 	int pTextureWidth,
	                 	int pTextureHeight,
	                 	int pTextureDepth)
	{
		this(	pGLInterface,
		     	pType,
		     	4,
		     	pTextureWidth,
		     	pTextureHeight,
		     	pTextureDepth,
		     	true,
		     	1);
	}

	public GLTexture(	GLInterface pGLInterface,
	                 	Class<T> pType,
	                 	int pTextureWidth,
	                 	int pTextureHeight,
	                 	int pTextureDepth,
	                 	boolean pLinearInterpolation)
	{
		this(	pGLInterface,
		     	pType,
		     	4,
		     	pTextureWidth,
		     	pTextureHeight,
		     	pTextureDepth,
		     	pLinearInterpolation,
		     	1);
	}

	public GLTexture(	GLInterface pGLInterface,
	                 	Class<T> pType,
	                 	int pNumberOfChannels,
	                 	int pTextureWidth,
	                 	int pTextureHeight,
	                 	int pTextureDepth,
	                 	boolean pLinearInterpolation,
	                 	int pMipMapLevels)
	{
		super();
		mGLInterface = pGLInterface;
		mType = pType;
		mNumberOfChannels = pNumberOfChannels;
		mTextureWidth = pTextureWidth;
		mTextureHeight = pTextureHeight;
		mTextureDepth = pTextureDepth;
		mMipMapLevels = pMipMapLevels;

		mTextureTarget = mTextureDepth == 1	? GL4.GL_TEXTURE_2D
		                                   	: GL4.GL_TEXTURE_3D;
		mTextureOpenGLFormat = mNumberOfChannels == 4 ? GL4.GL_RGBA// GL_BGRA
		                                              : GL4.GL_RED;

		if (mType.equals(Byte.class))
		{
			mTextureOpenGLDataType = GL4.GL_UNSIGNED_BYTE;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL4.GL_RGBA8
			                                                     	: GL4.GL_R8;
			mBytesPerChannel = 1;
		}
		else if (mType.equals(Short.class))
		{
			mTextureOpenGLDataType = GL4.GL_SHORT;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL4.GL_RGBA16F
			                                                     	: GL4.GL_R16F;
			mBytesPerChannel = 2;
		}
		else if (mType.equals(Character.class))
		{
			mTextureOpenGLDataType = GL4.GL_UNSIGNED_SHORT;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL4.GL_RGBA16F
			                                                     	: GL4.GL_R16F;
			mBytesPerChannel = 2;
		}
		else if (mType.equals(Integer.class))
		{
			mTextureOpenGLDataType = GL4.GL_UNSIGNED_INT;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL4.GL_RGBA32F
			                                                     	: GL4.GL_R32F;
			mBytesPerChannel = 4;
		}
		else if (mType.equals(Float.class) || mType.equals(Double.class))
		{
			mTextureOpenGLDataType = GL4.GL_FLOAT;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL4.GL_RGBA32F
			                                                     	: GL4.GL_R32F;
			mBytesPerChannel = 4;
		}

		else
			throw new IllegalArgumentException("Data type not supported for texture !");

		mGLInterface.getGL().glGenTextures(1, mTextureId, 0);
		bind();
		mGLInterface.getGL()
		.glTexParameterf(	mTextureTarget,
		                 	GL4.GL_TEXTURE_MAG_FILTER,
		                 	pLinearInterpolation ? GL4.GL_LINEAR
		                 	                     : GL4.GL_NEAREST);
		mGLInterface.getGL()
		.glTexParameterf(	mTextureTarget,
		                 	GL4.GL_TEXTURE_MIN_FILTER,
		                 	pMipMapLevels > 1	? (pLinearInterpolation	? GL4.GL_LINEAR_MIPMAP_LINEAR
		                 	                 	                       	: GL4.GL_NEAREST_MIPMAP_NEAREST)
		                 	                 	                       	: (pLinearInterpolation	? GL4.GL_LINEAR
		                 	                 	                       	                       	: GL4.GL_NEAREST));
		mGLInterface.getGL().glTexParameterf(	mTextureTarget,
		                                     	GL4.GL_TEXTURE_WRAP_S,
		                                     	GL4.GL_CLAMP_TO_EDGE);
		mGLInterface.getGL().glTexParameterf(	mTextureTarget,
		                                     	GL4.GL_TEXTURE_WRAP_T,
		                                     	GL4.GL_CLAMP_TO_EDGE);

		mGLInterface.getGL().glTexStorage2D(mTextureTarget,
		                                    pMipMapLevels,
		                                    mTextureOpenGLInternalFormat,
		                                    mTextureWidth,
		                                    mTextureHeight);

		mGLInterface.getGL()
		.glTexSubImage2D(	mTextureTarget,
		                 	0,
		                 	0,
		                 	0,
		                 	mTextureWidth,
		                 	mTextureHeight,
		                 	mTextureOpenGLFormat,
		                 	mTextureOpenGLDataType,
		                 	Buffers.newDirectByteBuffer(mTextureWidth * mTextureHeight
		                 	                            * mNumberOfChannels
		                 	                            * mBytesPerChannel));

		if (mMipMapLevels > 1)
			mGLInterface.getGL().glGenerateMipmap(mTextureTarget);
	}

	public void unbind()
	{
		mGLInterface.getGL().glBindTexture(mTextureTarget, 0);
	}

	@SafeVarargs
	public static <T> void bindTextures(GLProgram pGLProgram,
	                                    GLTexture<T>... pTexturesToBind)
	{
		pGLProgram.bind();
		int lTextureUnit = 0;
		for (final GLTexture<T> lTexture : pTexturesToBind)
			lTexture.bind(lTextureUnit++);
	}

	public void bind(GLProgram pGLProgram)
	{
		pGLProgram.bind();
		bind();
	}

	public void bind()
	{
		mGLInterface.getGL().glActiveTexture(GL4.GL_TEXTURE0);
		mGLInterface.getGL().glBindTexture(mTextureTarget, getId());
	}

	public void bind(int pTextureUnit)
	{
		mGLInterface.getGL()
		.glActiveTexture(GL4.GL_TEXTURE0 + pTextureUnit);
		mGLInterface.getGL().glBindTexture(mTextureTarget, getId());
	}

	public void copyFrom(GLPixelBufferObject pPixelBufferObject)
	{
		bind();
		pPixelBufferObject.bind();
		mGLInterface.getGL().glTexSubImage2D(	mTextureTarget,
		                                     	0,
		                                     	0,
		                                     	0,
		                                     	mTextureWidth,
		                                     	mTextureHeight,
		                                     	mTextureOpenGLFormat,
		                                     	mTextureOpenGLDataType,
		                                     	0);
		if (mMipMapLevels > 1)
			mGLInterface.getGL().glGenerateMipmap(mTextureTarget);

		pPixelBufferObject.unbind();
	}



	public void clear()
	{
		bind();

		final int lNeededSize = mTextureWidth * mTextureHeight * 4;
		if (mEmptyBuffer == null || mEmptyBuffer.capacity() != lNeededSize)
		{
			mEmptyBuffer = ByteBuffer.allocateDirect(lNeededSize)
					.order(ByteOrder.nativeOrder());
		}

		mGLInterface.getGL().glTexSubImage2D(	mTextureTarget,
		                                     	0,
		                                     	0,
		                                     	0,
		                                     	mTextureWidth,
		                                     	mTextureHeight,
		                                     	mTextureOpenGLFormat,
		                                     	mTextureOpenGLDataType,
		                                     	mEmptyBuffer);
		if (mMipMapLevels > 1)
			mGLInterface.getGL().glGenerateMipmap(mTextureTarget);
	}

	public void copyFrom(Buffer pBuffer)
	{
		bind();
		mGLInterface.getGL().glTexSubImage2D(	mTextureTarget,
		                                     	0,
		                                     	0,
		                                     	0,
		                                     	mTextureWidth,
		                                     	mTextureHeight,
		                                     	mTextureOpenGLFormat,
		                                     	mTextureOpenGLDataType,
		                                     	pBuffer);
		if (mMipMapLevels > 1)
			mGLInterface.getGL().glGenerateMipmap(mTextureTarget);
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
	public GL4 getGL()
	{
		return mGLInterface.getGL();
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
