package cleargl;


import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLException;

import coremem.ContiguousMemoryInterface;
import coremem.types.NativeTypeEnum;


public class GLTexture implements GLInterface, GLCloseable
{

	private final GLInterface mGLInterface;
	private final int[] mTextureId = new int[1];
	private final NativeTypeEnum mType;
	private int mBytesPerChannel;
	private final int mTextureWidth;
	private final int mTextureHeight;
	private int mTextureOpenGLDataType;
	private final int mTextureOpenGLFormat;
	private int mTextureOpenGLInternalFormat;
	private final int mMipMapLevels;
	private final int mTextureDepth;
	private final int mTextureTarget;
	private final int mNumberOfChannels;

	public GLTexture(	GLInterface pGLInterface,
										NativeTypeEnum pType,
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
										NativeTypeEnum pType,
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
										NativeTypeEnum pType,
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

		mTextureTarget = mTextureDepth == 1	? GL.GL_TEXTURE_2D
																				: GL2.GL_TEXTURE_3D;
		mTextureOpenGLFormat = mNumberOfChannels == 4 ? GL.GL_RGBA// GL_BGRA
																									: GL2.GL_RED;

		if (mType == NativeTypeEnum.Byte)
		{
			mTextureOpenGLDataType = GL.GL_BYTE;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL.GL_RGBA8
																														: GL2.GL_R8;
			mBytesPerChannel = 1;
		}
		else if (mType == NativeTypeEnum.UnsignedByte)
		{
			mTextureOpenGLDataType = GL.GL_UNSIGNED_BYTE;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL.GL_RGBA8
																														: GL2.GL_R8;
			mBytesPerChannel = 1;
		}
		else if (mType == NativeTypeEnum.Short)
		{
			mTextureOpenGLDataType = GL.GL_SHORT;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL.GL_RGBA16F
																														: GL2.GL_R16F;
			mBytesPerChannel = 2;
		}
		else if (mType == NativeTypeEnum.UnsignedShort)
		{
			mTextureOpenGLDataType = GL.GL_UNSIGNED_SHORT;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL.GL_RGBA16F
																														: GL2.GL_R16F;
			mBytesPerChannel = 2;
		}
		else if (mType == NativeTypeEnum.Int)
		{
			mTextureOpenGLDataType = GL2.GL_INT;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL.GL_RGBA32F
																														: GL2.GL_R32F;
			mBytesPerChannel = 4;
		}
		else if (mType == NativeTypeEnum.UnsignedInt)
		{
			mTextureOpenGLDataType = GL.GL_UNSIGNED_INT;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL.GL_RGBA32F
																														: GL2.GL_R32F;
			mBytesPerChannel = 4;
		}
		else if (mType == NativeTypeEnum.Float)
		{
			mTextureOpenGLDataType = GL.GL_FLOAT;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL.GL_RGBA32F
																														: GL2.GL_R32F;
			mBytesPerChannel = 4;
		}

		else
			throw new IllegalArgumentException("Data type not supported for texture !");

		mGLInterface.getGL().glGenTextures(1, mTextureId, 0);
		bind();
		mGLInterface.getGL()
								.glTexParameterf(	mTextureTarget,
																	GL.GL_TEXTURE_MAG_FILTER,
																	pLinearInterpolation ? GL.GL_LINEAR
																											: GL.GL_NEAREST);
		mGLInterface.getGL()
								.glTexParameterf(	mTextureTarget,
																	GL.GL_TEXTURE_MIN_FILTER,
																	mMipMapLevels > 1	? (pLinearInterpolation	? GL.GL_LINEAR_MIPMAP_LINEAR
																																						: GL.GL_NEAREST_MIPMAP_NEAREST)
																										: (pLinearInterpolation	? GL.GL_LINEAR
																																						: GL.GL_NEAREST));
		mGLInterface.getGL().glTexParameterf(	mTextureTarget,
																					GL.GL_TEXTURE_WRAP_S,
																					GL.GL_CLAMP_TO_EDGE);
		mGLInterface.getGL().glTexParameterf(	mTextureTarget,
																					GL.GL_TEXTURE_WRAP_T,
																					GL.GL_CLAMP_TO_EDGE);

		mGLInterface.getGL().glTexStorage2D(mTextureTarget,
																				mMipMapLevels,
																				mTextureOpenGLInternalFormat,
																				mTextureWidth,
																				mTextureHeight);

	}

	public void unbind()
	{
		mGLInterface.getGL().glBindTexture(mTextureTarget, 0);
	}

	@SafeVarargs
	public static <T> void bindTextures(GLProgram pGLProgram,
																			GLTexture... pTexturesToBind)
	{
		pGLProgram.bind();
		int lTextureUnit = 0;
		for (final GLTexture lTexture : pTexturesToBind)
			lTexture.bind(lTextureUnit++);
	}

	public void bind(GLProgram pGLProgram)
	{
		pGLProgram.bind();
		bind();
	}

	public void bind()
	{
		mGLInterface.getGL().glActiveTexture(GL.GL_TEXTURE0);
		mGLInterface.getGL().glBindTexture(mTextureTarget, getId());
	}

	public void bind(int pTextureUnit)
	{
		mGLInterface.getGL()
								.glActiveTexture(GL.GL_TEXTURE0 + pTextureUnit);
		mGLInterface.getGL().glBindTexture(mTextureTarget, getId());
	}

	public void clear()
	{
		bind();

		final int lNeededSize = mTextureWidth * mTextureHeight
														* mBytesPerChannel
														* mNumberOfChannels;

		// empty buffer
		final Buffer lEmptyBuffer = ByteBuffer.allocateDirect(lNeededSize)
																					.order(ByteOrder.nativeOrder());

		mGLInterface.getGL().glTexSubImage2D(	mTextureTarget,
																					0,
																					0,
																					0,
																					mTextureWidth,
																					mTextureHeight,
																					mTextureOpenGLFormat,
																					mTextureOpenGLDataType,
																					lEmptyBuffer);
		if (mMipMapLevels > 1)
			updateMipMaps();

	}

	public void updateMipMaps()
	{
		mGLInterface.getGL().glGenerateMipmap(mTextureTarget);
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
			updateMipMaps();

		pPixelBufferObject.unbind();
	}

	public void copyFrom(	Buffer pBuffer,
												int pLODLevel,
												boolean pAutoGenerateMipMaps)
	{
		bind();
		mGLInterface.getGL()
								.glTexSubImage2D(	mTextureTarget,
																	pLODLevel,
																	0,
																	0,
																	mTextureWidth >> pLODLevel,
																	mTextureHeight >> pLODLevel,
																	mTextureOpenGLFormat,
																	mTextureOpenGLDataType,
																	pBuffer);
		if (pAutoGenerateMipMaps && mMipMapLevels > 1)
			updateMipMaps();
	}

	public void copyFrom(Buffer pBuffer)
	{
		copyFrom(pBuffer, 0, true);
	}

	public void copyFrom(ContiguousMemoryInterface pContiguousMemory)
	{
		bind();
		final ByteBuffer lByteBuffer = pContiguousMemory.getByteBuffer();
		copyFrom(lByteBuffer);
	}

	public void copyFrom(	ContiguousMemoryInterface pContiguousMemory,
												int pLODLevel,
												boolean pAutoGenerateMipMaps)
	{
		bind();
		final ByteBuffer lByteBuffer = pContiguousMemory.getByteBuffer();
		copyFrom(lByteBuffer, pLODLevel, pAutoGenerateMipMaps);
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
	public GL getGL()
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
