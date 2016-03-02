package cleargl;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLException;
import coremem.ContiguousMemoryInterface;
import coremem.types.NativeTypeEnum;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class GLTexture implements GLInterface, GLCloseable
{

	private final GL4 mGL;
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
			this(pGLInterface.getGL().getGL4(),
							pType,
							pNumberOfChannels,
							pTextureWidth,
							pTextureHeight,
							pTextureDepth,
							pLinearInterpolation,
							pMipMapLevels
			);
	}

	public GLTexture(	GL4 pGL,
										 NativeTypeEnum pType,
										 int pNumberOfChannels,
										 int pTextureWidth,
										 int pTextureHeight,
										 int pTextureDepth,
										 boolean pLinearInterpolation,
										 int pMipMapLevels)
	{
		super();
		mGL = pGL;
		mType = pType;
		mNumberOfChannels = pNumberOfChannels;
		mTextureWidth = pTextureWidth;
		mTextureHeight = pTextureHeight;
		mTextureDepth = pTextureDepth;
		mMipMapLevels = pMipMapLevels;

		mTextureTarget = mTextureDepth == 1	? GL.GL_TEXTURE_2D
																				: GL2ES2.GL_TEXTURE_3D;
		mTextureOpenGLFormat = mNumberOfChannels == 4 ? GL.GL_RGBA// GL_BGRA
																									: GL2ES2.GL_RED;

		if (mType == NativeTypeEnum.Byte)
		{
			mTextureOpenGLDataType = GL.GL_BYTE;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL.GL_RGBA8
																														: GL.GL_R8;
			mBytesPerChannel = 1;
		}
		else if (mType == NativeTypeEnum.UnsignedByte)
		{
			mTextureOpenGLDataType = GL.GL_UNSIGNED_BYTE;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL.GL_RGBA8
																														: GL.GL_R8;
			mBytesPerChannel = 1;
		}
		else if (mType == NativeTypeEnum.Short)
		{
			mTextureOpenGLDataType = GL.GL_SHORT;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL.GL_RGBA16F
																														: GL.GL_R16F;
			mBytesPerChannel = 2;
		}
		else if (mType == NativeTypeEnum.UnsignedShort)
		{
			mTextureOpenGLDataType = GL.GL_UNSIGNED_SHORT;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL.GL_RGBA16F
																														: GL.GL_R16F;
			mBytesPerChannel = 2;
		}
		else if (mType == NativeTypeEnum.Int)
		{
			mTextureOpenGLDataType = GL2ES2.GL_INT;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL.GL_RGBA32F
																														: GL.GL_R32F;
			mBytesPerChannel = 4;
		}
		else if (mType == NativeTypeEnum.UnsignedInt)
		{
			mTextureOpenGLDataType = GL.GL_UNSIGNED_INT;
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4	? GL.GL_RGBA32F
																														: GL.GL_R32F;
			mBytesPerChannel = 4;
		}
		else if (mType == NativeTypeEnum.Float)
		{
			mTextureOpenGLDataType = GL.GL_FLOAT;
			switch(mNumberOfChannels) {
				case 1:
					mTextureOpenGLInternalFormat = GL.GL_R32F;
					break;
				case 3:
					mTextureOpenGLInternalFormat = GL.GL_RGB32F;
					break;
				case 4:
					mTextureOpenGLInternalFormat = GL.GL_RGBA32F;
					break;
				case -1:
					mTextureOpenGLInternalFormat = GL.GL_DEPTH_COMPONENT24;
					break;
			}

			mBytesPerChannel = 4;
		}
		else
			throw new IllegalArgumentException("Data type not supported for texture !");

		mGL.glGenTextures(1, mTextureId, 0);
		bind();
		mGL.glTexParameterf(	mTextureTarget,
																	GL.GL_TEXTURE_MAG_FILTER,
																	pLinearInterpolation ? GL.GL_LINEAR
																											: GL.GL_NEAREST);
		mGL.glTexParameterf(	mTextureTarget,
																	GL.GL_TEXTURE_MIN_FILTER,
																	mMipMapLevels > 1	? (pLinearInterpolation	? GL.GL_LINEAR_MIPMAP_LINEAR
																																						: GL.GL_NEAREST_MIPMAP_NEAREST)
																										: (pLinearInterpolation	? GL.GL_LINEAR
																																						: GL.GL_NEAREST));
		mGL.glTexParameterf(	mTextureTarget,
																					GL.GL_TEXTURE_WRAP_S,
																					GL.GL_CLAMP_TO_EDGE);
		mGL.glTexParameterf(	mTextureTarget,
																					GL.GL_TEXTURE_WRAP_T,
																					GL.GL_CLAMP_TO_EDGE);

		mGL.glTexStorage2D(mTextureTarget,
																				mMipMapLevels,
																				mTextureOpenGLInternalFormat,
																				mTextureWidth,
																				mTextureHeight);

	}

	public void unbind()
	{
		mGL.glBindTexture(mTextureTarget, 0);
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
		mGL.glActiveTexture(GL.GL_TEXTURE0);
		mGL.glBindTexture(mTextureTarget, getId());
	}

	public void bind(int pTextureUnit)
	{
		mGL.glActiveTexture(GL.GL_TEXTURE0 + pTextureUnit);
		mGL.glBindTexture(mTextureTarget, getId());
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

		mGL.glTexSubImage2D(	mTextureTarget,
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
		mGL.glGenerateMipmap(mTextureTarget);
	}

	public void copyFrom(GLPixelBufferObject pPixelBufferObject)
	{
		bind();
		pPixelBufferObject.bind();
		mGL.glTexSubImage2D(	mTextureTarget,
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
		mGL.glTexSubImage2D(	mTextureTarget,
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
		mGL.glDeleteTextures(1, mTextureId, 0);
	}

	public int getWidth()
	{
		return mTextureWidth;
	}

	public int getHeight()
	{
		return mTextureHeight;
	}

	public int getType() {
		return mTextureOpenGLDataType;
	}

	public int getChannels() {
		return mNumberOfChannels;
	}

	public int getInternalFormat() { return mTextureOpenGLInternalFormat; }

	public int getBitsPerChannel() {
		return mBytesPerChannel*8;
	}

	@Override
	public GL getGL()
	{
		return mGL.getGL();
	}

	@Override
	public int getId()
	{
		return mTextureId[0];
	}

	@Override
	public String toString()
	{
		return "GLTexture [mGLInterface=" + mGL
						+ ", mTextureId="
						+ Arrays.toString(mTextureId)
						+ ", mTextureWidth="
						+ mTextureWidth
						+ ", mTextureHeight="
						+ mTextureHeight
						+ ", mTextureOpenGLInternalFormat="
						+ mTextureOpenGLInternalFormat
						+ "]";
	}

  public void dumpToFile(ByteBuffer buf) {
    try {
      File file = new File("/Users/ulrik/" + this.getId() + ".dump");
      FileChannel channel = new FileOutputStream(file, false).getChannel();
      buf.rewind();
      channel.write(buf);
      channel.close();
    } catch(Exception e) {
      System.err.println("Unable to dump " + this.getId());
      e.printStackTrace();
    }
  }

}
