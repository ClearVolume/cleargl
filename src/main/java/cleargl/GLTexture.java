package cleargl;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLException;
import coremem.ContiguousMemoryInterface;
import coremem.types.NativeTypeEnum;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Hashtable;

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

	private static ColorModel glAlphaColorModel = new ComponentColorModel(
					ColorSpace.getInstance(ColorSpace.CS_sRGB),
					new int[]{8, 8, 8, 8},
					true,
					false,
					ComponentColorModel.TRANSLUCENT,
					DataBuffer.TYPE_BYTE);

	private static ColorModel glColorModel = new ComponentColorModel(
					ColorSpace.getInstance(ColorSpace.CS_sRGB),
					new int[]{8, 8, 8, 0},
					false,
					false,
					ComponentColorModel.OPAQUE,
					DataBuffer.TYPE_BYTE);

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
										 int pMipMapLevels,
										 int precision)
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
			switch(mNumberOfChannels) {
				case 1:
					mTextureOpenGLInternalFormat = GL.GL_R8;
					break;
				case 3:
					mTextureOpenGLInternalFormat = GL.GL_RGB8;
					break;
				case 4:
					mTextureOpenGLInternalFormat = GL.GL_RGBA8;
					break;
				default:
					mTextureOpenGLInternalFormat = GL.GL_RGBA8;
			}
			mBytesPerChannel = 1;
		}
		else if (mType == NativeTypeEnum.UnsignedByte)
		{
			mTextureOpenGLDataType = GL.GL_UNSIGNED_BYTE;

			switch(mNumberOfChannels) {
				case 1:
					mTextureOpenGLInternalFormat = GL.GL_R8;
					break;
				case 3:
					mTextureOpenGLInternalFormat = GL.GL_RGB8;
					break;
				case 4:
					mTextureOpenGLInternalFormat = GL.GL_RGBA8;
					break;
				default:
					mTextureOpenGLInternalFormat = GL.GL_RGBA8;
			}
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
					if(precision == 16) {
						mTextureOpenGLInternalFormat = GL.GL_RGB16F;
						mBytesPerChannel = 2;
					} else if(precision == 32) {
						mTextureOpenGLInternalFormat = GL.GL_RGB32F;
						mBytesPerChannel = 4;
					}
					break;
				case 4:
					if(precision == 16) {
						mTextureOpenGLInternalFormat = GL.GL_RGBA16F;
						mBytesPerChannel = 2;
					} else if(precision == 32) {
						mTextureOpenGLInternalFormat = GL.GL_RGBA32F;
						mBytesPerChannel = 4;
					}
					break;
				case -1:
					if(precision == 24) {
						mTextureOpenGLInternalFormat = GL.GL_DEPTH_COMPONENT24;
						mBytesPerChannel = 3;
					} else {
						mTextureOpenGLInternalFormat = GL.GL_DEPTH_COMPONENT32;
						mBytesPerChannel = 4;
					}
					break;
			}
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

	public GLTexture(	GL4 pGL,
										 NativeTypeEnum pType,
										 int pNumberOfChannels,
										 int pTextureWidth,
										 int pTextureHeight,
										 int pTextureDepth,
										 boolean pLinearInterpolation,
										 int pMipMapLevels) {
		this(
						pGL,
						pType,
						pNumberOfChannels,
						pTextureWidth,
						pTextureHeight,
						pTextureDepth,
						pLinearInterpolation,
						pMipMapLevels,
						0
		);
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

	public static GLTexture loadFromFile(GL4 gl, String filename, boolean linearInterpolation, int mipmapLevels) {
		final BufferedImage bi;
		final ByteBuffer imageData;
		final InputStream fis;
		GLTexture tex;

		try {
			fis = Files.newInputStream(Paths.get(filename, ""));
			bi = ImageIO.read(fis);
			fis.close();
		} catch (Exception e) {
			System.err.println("GLTexture: could not read image from " + filename + ".");
			return null;
		}

		imageData = convertImageData(bi);

		int texWidth = 2;
		int texHeight = 2;

		while (texWidth < bi.getWidth()) {
			texWidth *= 2;
		}
		while (texHeight < bi.getHeight()) {
			texHeight *= 2;
		}

		tex = new GLTexture(gl,
						nativeTypeEnumFromBufferedImage(bi),
						bi.getColorModel().getNumComponents(),
						texWidth, texHeight, 1,
						linearInterpolation,
						mipmapLevels
		);
		System.out.println(tex);
		tex.clear();
		tex.copyFrom(imageData);
		tex.updateMipMaps();

		return tex;
	}

	private static NativeTypeEnum nativeTypeEnumFromBufferedImage(BufferedImage bi) {
		switch(bi.getData().getDataBuffer().getDataType()) {
			case DataBuffer.TYPE_BYTE: {
				return NativeTypeEnum.UnsignedByte;
			}
			case DataBuffer.TYPE_DOUBLE: {
				return NativeTypeEnum.Double;
			}
			case DataBuffer.TYPE_INT: {
				return NativeTypeEnum.Int;
			}
			case DataBuffer.TYPE_SHORT: {
				return NativeTypeEnum.Short;
			}
			default:
				return null;
		}
	}

	private static ByteBuffer convertImageData(BufferedImage bufferedImage) {
		ByteBuffer imageBuffer;
		WritableRaster raster;
		BufferedImage texImage;

		int texWidth = 2;
		int texHeight = 2;

		while (texWidth < bufferedImage.getWidth()) {
			texWidth *= 2;
		}
		while (texHeight < bufferedImage.getHeight()) {
			texHeight *= 2;
		}

		if(bufferedImage.getColorModel().hasAlpha()) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
			texImage = new BufferedImage(GLTexture.glAlphaColorModel, raster, false, new Hashtable<>());
		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
			texImage = new BufferedImage(GLTexture.glColorModel, raster, false, new Hashtable<>());
		}

		Graphics g = texImage.getGraphics();
		g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
		g.fillRect(0, 0, texWidth, texHeight);
		g.drawImage(bufferedImage, 0, 0, null);

		final byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		imageBuffer.rewind();

		return imageBuffer;
	}

}
