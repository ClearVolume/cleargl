/*-
 * #%L
 * ClearGL facade API on top of JOGL.
 * %%
 * Copyright (C) 2014 - 2025 ClearVolume developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package cleargl;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Hashtable;

@SuppressWarnings({"WeakerAccess", "unused"})
public class GLTexture implements GLInterface, GLCloseable {

	private final GL4 mGL;

	private final int[] mTextureId = new int[1];

	private final GLTypeEnum mType;

	private int mBytesPerChannel;

	private final int mTextureWidth;

	private final int mTextureHeight;

	private final int mTextureDepth;

	private final int mTextureOpenGLDataType;

	private final int mTextureOpenGLFormat;

	private int mTextureOpenGLInternalFormat;

	private final int mMipMapLevels;

	private final int mTextureTarget;

	private final int mNumberOfChannels;

	private final boolean msRGB;

	private static ColorModel glAlphaColorModel = new ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB),
			new int[]{8, 8, 8, 8},
			true,
			false,
			ComponentColorModel.TRANSLUCENT,
			DataBuffer.TYPE_BYTE);

	private static ColorModel glAlphaColorModelsRGB = new ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_sRGB),
			new int[]{8, 8, 8, 8},
			true,
			false,
			ComponentColorModel.TRANSLUCENT,
			DataBuffer.TYPE_BYTE);

	private static ColorModel glColorModel = new ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB),
			new int[]{8, 8, 8, 0},
			false,
			false,
			ComponentColorModel.OPAQUE,
			DataBuffer.TYPE_BYTE);

	private static ColorModel glColorModelsRGB = new ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_sRGB),
			new int[]{8, 8, 8, 0},
			false,
			false,
			ComponentColorModel.OPAQUE,
			DataBuffer.TYPE_BYTE);

	public GLTexture(final GLInterface pGLInterface,
			final GLTypeEnum pType,
			final int pTextureWidth,
			final int pTextureHeight,
			final int pTextureDepth) {
		this(pGLInterface,
				pType,
				4,
				pTextureWidth,
				pTextureHeight,
				pTextureDepth,
				true,
				1);
	}

	public GLTexture(final GLInterface pGLInterface,
			final GLTypeEnum pType,
			final int pTextureWidth,
			final int pTextureHeight,
			final int pTextureDepth,
			final boolean pLinearInterpolation) {
		this(pGLInterface,
				pType,
				4,
				pTextureWidth,
				pTextureHeight,
				pTextureDepth,
				pLinearInterpolation,
				1);
	}

	public GLTexture(final GLInterface pGLInterface,
			final GLTypeEnum pType,
			final int pNumberOfChannels,
			final int pTextureWidth,
			final int pTextureHeight,
			final int pTextureDepth,
			final boolean pLinearInterpolation,
			final int pMipMapLevels) {
		this(pGLInterface.getGL().getGL4(),
				pType,
				pNumberOfChannels,
				pTextureWidth,
				pTextureHeight,
				pTextureDepth,
				pLinearInterpolation,
				pMipMapLevels);
	}

	public GLTexture(final GL4 pGL,
			final GLTypeEnum pType,
			final int pNumberOfChannels,
			final int pTextureWidth,
			final int pTextureHeight,
			final int pTextureDepth,
			final boolean pLinearInterpolation,
			final int pMipMapLevels,
			final int precision) {
		this(pGL,
				pType,
				pNumberOfChannels,
				pTextureWidth,
				pTextureHeight,
				pTextureDepth,
				pLinearInterpolation,
				pMipMapLevels,
				precision,
				true,
				false);
	}

	public GLTexture(final GL4 pGL,
					 final GLTypeEnum pType,
					 final int pNumberOfChannels,
					 final int pTextureWidth,
					 final int pTextureHeight,
					 final int pTextureDepth,
					 final boolean pLinearInterpolation,
					 final int pMipMapLevels,
					 final int precision,
					 final boolean normalized) {
		this(pGL,
				pType,
				pNumberOfChannels,
				pTextureWidth,
				pTextureHeight,
				pTextureDepth,
				pLinearInterpolation,
				pMipMapLevels,
				precision,
				normalized,
				false);
	}

	public GLTexture(final GL4 pGL,
			final GLTypeEnum pType,
			final int pNumberOfChannels,
			final int pTextureWidth,
			final int pTextureHeight,
			final int pTextureDepth,
			final boolean pLinearInterpolation,
			final int pMipMapLevels,
			final int precision,
			final boolean normalized,
			final boolean sRGB)

	{
		super();
		mGL = pGL;
		mType = pType;
		mNumberOfChannels = pNumberOfChannels;
		mTextureWidth = pTextureWidth;
		mTextureHeight = pTextureHeight;
		mTextureDepth = pTextureDepth;
		mMipMapLevels = pMipMapLevels;
		msRGB = sRGB;

		mTextureTarget = mTextureDepth == 1 ? GL4.GL_TEXTURE_2D
				: GL4.GL_TEXTURE_3D;
		switch (mNumberOfChannels) {
			case 1:
				if ((mType == GLTypeEnum.UnsignedShort || mType == GLTypeEnum.UnsignedByte) && mTextureDepth > 1
						&& !normalized) {
					mTextureOpenGLFormat = GL4.GL_RED_INTEGER;
				} else {
					mTextureOpenGLFormat = GL4.GL_RED;
				}
				break;
			case 2:
				if ((mType == GLTypeEnum.UnsignedShort || mType == GLTypeEnum.UnsignedByte) && mTextureDepth > 1
						&& !normalized) {
					mTextureOpenGLFormat = GL4.GL_RG_INTEGER;
				} else {
					mTextureOpenGLFormat = GL4.GL_RG;
				}
				break;
			case 3:
				if ((mType == GLTypeEnum.UnsignedShort || mType == GLTypeEnum.UnsignedByte) && mTextureDepth > 1
						&& !normalized) {
					mTextureOpenGLFormat = GL4.GL_RGB_INTEGER;
				} else {
					mTextureOpenGLFormat = GL4.GL_RGB;
				}
				break;
			case 4:
			default:
				if ((mType == GLTypeEnum.UnsignedShort || mType == GLTypeEnum.UnsignedByte) && mTextureDepth > 1
						&& !normalized) {
					mTextureOpenGLFormat = GL4.GL_RGBA_INTEGER;
				} else {
					mTextureOpenGLFormat = GL4.GL_RGBA;
				}
		}

		mTextureOpenGLDataType = mType.glType();
		if (mType == GLTypeEnum.Byte) {
			switch (mNumberOfChannels) {
				case 1:
					mTextureOpenGLInternalFormat = GL4.GL_R8;
					break;
				case 2:
					mTextureOpenGLInternalFormat = GL4.GL_RG8;
					break;
				case 3:
					mTextureOpenGLInternalFormat = sRGB ? GL4.GL_SRGB8 : GL4.GL_RGB8;
					break;
				case 4:
					mTextureOpenGLInternalFormat = sRGB ? GL4.GL_SRGB8_ALPHA8 : GL4.GL_RGBA8;
					break;
				default:
					mTextureOpenGLInternalFormat = sRGB ? GL4.GL_SRGB8_ALPHA8 : GL4.GL_RGBA8;
			}
			mBytesPerChannel = 1;
		} else if (mType == GLTypeEnum.UnsignedByte && mTextureDepth == 1) {
			switch (mNumberOfChannels) {
				case 1:
					mTextureOpenGLInternalFormat = GL4.GL_R8;
					break;
				case 2:
					mTextureOpenGLInternalFormat = GL4.GL_RG8;
					break;
				case 3:
					mTextureOpenGLInternalFormat = sRGB ? GL4.GL_SRGB8 : GL4.GL_RGB8;
					break;
				case 4:
					mTextureOpenGLInternalFormat = sRGB ? GL4.GL_SRGB8_ALPHA8 : GL4.GL_RGBA8;
					break;
				default:
					mTextureOpenGLInternalFormat = sRGB ? GL4.GL_SRGB8_ALPHA8 : GL4.GL_RGBA8;
			}
			mBytesPerChannel = 1;
		} else if (mType == GLTypeEnum.UnsignedByte && mTextureDepth > 1) {
			switch (mNumberOfChannels) {
				case 1:
					mTextureOpenGLInternalFormat = normalized ? GL4.GL_R8 : GL4.GL_R8UI;
					break;
				case 2:
					mTextureOpenGLInternalFormat = normalized ? GL4.GL_RG8 : GL4.GL_RG8UI;
					break;
				case 3:
					mTextureOpenGLInternalFormat = normalized ? GL4.GL_RGB8 : GL4.GL_RGB8UI;
					break;
				case 4:
					mTextureOpenGLInternalFormat = normalized ? GL4.GL_RGBA8 : GL4.GL_RGBA8UI;
					break;
				default:
					mTextureOpenGLInternalFormat = normalized ? GL4.GL_RGBA8 : GL4.GL_RGBA8UI;
			}
			mBytesPerChannel = 1;
		} else if (mType == GLTypeEnum.Short) {
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4 ? GL4.GL_RGBA16F
					: GL4.GL_R16F;
			mBytesPerChannel = 2;
		} else if (mType == GLTypeEnum.UnsignedShort) {
			switch (mNumberOfChannels) {
				case 1:
					mTextureOpenGLInternalFormat = normalized ? GL4.GL_R16 : GL4.GL_R16UI;
					break;
				case 2:
					mTextureOpenGLInternalFormat = normalized ? GL4.GL_RG16 : GL4.GL_RG16UI;
					break;
				case 3:
					mTextureOpenGLInternalFormat = normalized ? GL4.GL_RGB16 : GL4.GL_RGB16UI;
					break;
				case 4:
				default:
					mTextureOpenGLInternalFormat = normalized ? GL4.GL_RGBA16 : GL4.GL_RGBA16UI;
			}

			mBytesPerChannel = 2;
		} else if (mType == GLTypeEnum.Int) {
			mTextureOpenGLInternalFormat = mNumberOfChannels == 4 ? GL4.GL_RGBA32F
					: GL4.GL_R32F;
			mBytesPerChannel = 4;
		} else if (mType == GLTypeEnum.UnsignedInt) {
			switch (mNumberOfChannels) {
				case 1:
					mTextureOpenGLInternalFormat = GL4.GL_R32UI;
					break;
				case 2:
					mTextureOpenGLInternalFormat = GL4.GL_RG32UI;
					break;
				case 3:
					mTextureOpenGLInternalFormat = GL4.GL_RGB32UI;
					break;
				case 4:
				default:
					mTextureOpenGLInternalFormat = GL4.GL_RGBA32UI;
			}

			mBytesPerChannel = 4;
		} else if (mType == GLTypeEnum.Float) {
			switch (mNumberOfChannels) {
				case 1:
					mTextureOpenGLInternalFormat = GL4.GL_R32F;
					mBytesPerChannel = 4;
					break;
				case 2:
					if (precision == 16) {
						mTextureOpenGLInternalFormat = GL4.GL_RG16F;
						mBytesPerChannel = 4;
					} else if (precision == 32) {
						mTextureOpenGLInternalFormat = GL4.GL_RG32F;
						mBytesPerChannel = 4;
					}
				case 3:
					if (precision == 16) {
						mTextureOpenGLInternalFormat = GL4.GL_RGB16F;
						mBytesPerChannel = 4;
					} else if (precision == 32) {
						mTextureOpenGLInternalFormat = GL4.GL_RGB32F;
						mBytesPerChannel = 4;
					}
					break;
				case 4:
					if (precision == 16) {
						mTextureOpenGLInternalFormat = GL4.GL_RGBA16F;
						mBytesPerChannel = 4;
					} else {
						mTextureOpenGLInternalFormat = GL4.GL_RGBA32F;
						mBytesPerChannel = 4;
					}
					break;
				case -1:
					if (precision == 24) {
						mTextureOpenGLInternalFormat = GL4.GL_DEPTH_COMPONENT24;
						mBytesPerChannel = 3;
					} else {
						mTextureOpenGLInternalFormat = GL4.GL_DEPTH_COMPONENT32;
						mBytesPerChannel = 4;
					}
					break;
			}
		} else
			throw new IllegalArgumentException("Data type not supported for texture !");

		mGL.glGenTextures(1, mTextureId, 0);
		bind();

		if (mTextureTarget == GL4.GL_TEXTURE_2D) {
			mGL.glTexParameteri(mTextureTarget,
					GL4.GL_TEXTURE_MAG_FILTER,
					pLinearInterpolation ? GL4.GL_LINEAR
							: GL4.GL_NEAREST);
			mGL.glTexParameteri(mTextureTarget,
					GL4.GL_TEXTURE_MIN_FILTER,
					mMipMapLevels > 1 ? (pLinearInterpolation ? GL4.GL_LINEAR_MIPMAP_LINEAR
							: GL4.GL_NEAREST_MIPMAP_NEAREST)
							: (pLinearInterpolation ? GL4.GL_LINEAR
									: GL4.GL_NEAREST));
			mGL.glTexParameteri(mTextureTarget,
					GL4.GL_TEXTURE_WRAP_S,
					GL4.GL_REPEAT);
			mGL.glTexParameteri(mTextureTarget,
					GL4.GL_TEXTURE_WRAP_T,
					GL4.GL_REPEAT);

			mGL.glTexStorage2D(mTextureTarget,
					mMipMapLevels,
					mTextureOpenGLInternalFormat,
					mTextureWidth,
					mTextureHeight);
		} else {
			mGL.glTexParameteri(mTextureTarget,
					GL4.GL_TEXTURE_MIN_FILTER,
					pLinearInterpolation ? GL4.GL_LINEAR
							: GL4.GL_NEAREST);
			mGL.glTexParameteri(mTextureTarget,
					GL4.GL_TEXTURE_MAG_FILTER,
					pLinearInterpolation ? GL4.GL_LINEAR
							: GL4.GL_NEAREST);
			mGL.glTexParameteri(mTextureTarget,
					GL4.GL_TEXTURE_WRAP_S,
					GL4.GL_CLAMP_TO_EDGE);
			mGL.glTexParameteri(mTextureTarget,
					GL4.GL_TEXTURE_WRAP_T,
					GL4.GL_CLAMP_TO_EDGE);
			mGL.glTexParameteri(mTextureTarget,
					GL4.GL_TEXTURE_WRAP_R,
					GL4.GL_CLAMP_TO_EDGE);

			mGL.glTexStorage3D(mTextureTarget,
					1,
					mTextureOpenGLInternalFormat,
					mTextureWidth,
					mTextureHeight,
					mTextureDepth);
		}
	}

	public GLTexture(final GL4 pGL,
			final GLTypeEnum pType,
			final int pNumberOfChannels,
			final int pTextureWidth,
			final int pTextureHeight,
			final int pTextureDepth,
			final boolean pLinearInterpolation,
			final int pMipMapLevels) {
		this(
				pGL,
				pType,
				pNumberOfChannels,
				pTextureWidth,
				pTextureHeight,
				pTextureDepth,
				pLinearInterpolation,
				pMipMapLevels,
				0);
	}

	public void unbind() {
		mGL.glBindTexture(mTextureTarget, 0);
	}

	public void delete() {
		mGL.glDeleteTextures(1, mTextureId, 0);
	}

	@SafeVarargs
	public static <T> void bindTextures(final GLProgram pGLProgram,
			final GLTexture... pTexturesToBind) {
		pGLProgram.bind();
		int lTextureUnit = 0;
		for (final GLTexture lTexture : pTexturesToBind)
			lTexture.bind(lTextureUnit++);
	}

	public void bind(final GLProgram pGLProgram) {
		pGLProgram.bind();
		bind();
	}

	public void bind() {
		mGL.glActiveTexture(GL4.GL_TEXTURE0);
		mGL.glBindTexture(mTextureTarget, getId());
	}

	public void bind(final int pTextureUnit) {
		mGL.glActiveTexture(GL4.GL_TEXTURE0 + pTextureUnit);
		mGL.glBindTexture(mTextureTarget, getId());
	}

	public void setClamp(final boolean clampS, final boolean clampT) {
		mGL.glTexParameterf(mTextureTarget,
				GL4.GL_TEXTURE_WRAP_S,
				clampS ? GL4.GL_CLAMP_TO_EDGE : GL4.GL_REPEAT);
		mGL.glTexParameterf(mTextureTarget,
				GL4.GL_TEXTURE_WRAP_T,
				clampT ? GL4.GL_CLAMP_TO_EDGE : GL4.GL_REPEAT);
	}

	public void setRepeatModeS(final int mode) {
		mGL.glTexParameteri( mTextureTarget,
				GL4.GL_TEXTURE_WRAP_S,
				mode);
	}

	public void setRepeatModeT(final int mode) {
		mGL.glTexParameteri( mTextureTarget,
				GL4.GL_TEXTURE_WRAP_T,
				mode);
	}

	public void setRepeatModeR(final int mode) {
		mGL.glTexParameteri( mTextureTarget,
				GL4.GL_TEXTURE_WRAP_R,
				mode);
	}

	public void setTextureBorderColor(final float[] color) {
		mGL.glTexParameterfv( mTextureTarget,
				GL4.GL_TEXTURE_BORDER_COLOR,
				color,
				0);
	}

	public void clear() {
		bind();

		final int lNeededSize = mTextureWidth * mTextureHeight
				* mTextureDepth
				* mBytesPerChannel
				* mNumberOfChannels;

		// empty buffer
		final Buffer lEmptyBuffer = ByteBuffer.allocateDirect(lNeededSize)
				.order(ByteOrder.nativeOrder());

		if (mTextureTarget == GL4.GL_TEXTURE_2D) {
			mGL.glTexSubImage2D(mTextureTarget,
					0,
					0,
					0,
					mTextureWidth,
					mTextureHeight,
					mTextureOpenGLFormat,
					mTextureOpenGLDataType,
					lEmptyBuffer);
		} else {
			mGL.glTexSubImage3D(mTextureTarget,
					0,
					0,
					0,
					0,
					mTextureWidth,
					mTextureHeight,
					mTextureDepth,
					mTextureOpenGLFormat,
					mTextureOpenGLDataType,
					lEmptyBuffer);
		}
		if (mMipMapLevels > 1)
			updateMipMaps();

	}

	public void updateMipMaps() {
		mGL.glGenerateMipmap(mTextureTarget);
	}

	public void copyFrom(final GLPixelBufferObject pPixelBufferObject) {
		bind();
		pPixelBufferObject.bind();
		mGL.glTexSubImage2D(mTextureTarget,
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

	public void copyFrom(final Buffer pBuffer,
			final int pLODLevel,
			final boolean pAutoGenerateMipMaps) {
		bind();
		pBuffer.rewind();

		if (mTextureTarget == GL4.GL_TEXTURE_2D) {
			mGL.glTexSubImage2D(mTextureTarget,
					pLODLevel,
					0,
					0,
					mTextureWidth >> pLODLevel,
					mTextureHeight >> pLODLevel,
					mTextureOpenGLFormat,
					mTextureOpenGLDataType,
					pBuffer);
		} else {
			mGL.glTexSubImage3D(mTextureTarget,
					pLODLevel,
					0,
					0,
					0,
					mTextureWidth >> pLODLevel,
					mTextureHeight >> pLODLevel,
					mTextureDepth >> pLODLevel,
					mTextureOpenGLFormat,
					mTextureOpenGLDataType,
					pBuffer);
		}

		if (pAutoGenerateMipMaps && mMipMapLevels > 1 && mTextureTarget == GL4.GL_TEXTURE_2D) {
			updateMipMaps();
		}
	}

	public void copyFrom(final Buffer pBuffer,
			final int width, final int height, final int depth,
			final int x, final int y, final int z,
			boolean pAutoGenerateMipMaps) {
		bind();
		pBuffer.rewind();

		if (mTextureTarget == GL4.GL_TEXTURE_2D) {
			mGL.glTexSubImage2D(mTextureTarget,
					0,
					x,
					y,
					width,
					height,
					mTextureOpenGLFormat,
					mTextureOpenGLDataType,
					pBuffer);
		} else {
			mGL.glTexSubImage3D(mTextureTarget,
					0,
					x,
					y,
					z,
					width,
					height,
					depth,
					mTextureOpenGLFormat,
					mTextureOpenGLDataType,
					pBuffer);
		}

		if (pAutoGenerateMipMaps && mMipMapLevels > 1 && mTextureTarget == GL4.GL_TEXTURE_2D) {
			updateMipMaps();
		}
	}

	public void copyFrom(final Buffer pBuffer) {
		copyFrom(pBuffer, 0, true);
	}

	@Override
	public void close() throws GLException {
		mGL.glDeleteTextures(1, mTextureId, 0);
	}

	public int getWidth() {
		return mTextureWidth;
	}

	public int getHeight() {
		return mTextureHeight;
	}

	public int getDepth() {
		return mTextureDepth;
	}

	public int getType() {
		return mTextureOpenGLDataType;
	}

	public GLTypeEnum getNativeType() {
		return mType;
	}

	public int getTextureTarget() {
		return mTextureTarget;
	}

	public int getChannels() {
		return mNumberOfChannels;
	}

	public int getFormat() {
		return mTextureOpenGLFormat;
	}

	public int getInternalFormat() {
		return mTextureOpenGLInternalFormat;
	}

	public int getBitsPerChannel() {
		return mBytesPerChannel * 8;
	}

	public boolean getsRGB() {
		return msRGB;
	}

	@Override
	public GL4 getGL() {
		return mGL.getGL().getGL4();
	}

	@Override
	public int getId() {
		return mTextureId[0];
	}

	@Override
	public String toString() {
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

	public void dumpToFile(final ByteBuffer buf) {
		try {
			final File file = new File("/Users/ulrik/" + this.getId() + ".dump");
			final FileChannel channel = new FileOutputStream(file, false).getChannel();
			buf.rewind();
			channel.write(buf);
			channel.close();
		} catch (final Exception e) {
			System.err.println("Unable to dump " + this.getId());
			e.printStackTrace();
		}
	}

	public static GLTexture loadFromFile(final GL4 gl, final String filename, final boolean linearInterpolation,
			final int mipmapLevels) throws FileNotFoundException {
		return loadFromFile(gl, filename, linearInterpolation, true, mipmapLevels);
	}

	public static GLTexture loadFromFile(final GL4 gl, final String filename, final boolean linearInterpolation,
			final boolean generateMipmaps, final int maxMipmapLevels) throws FileNotFoundException {
		FileInputStream inputStream = new FileInputStream(filename);
		final String type = filename.substring(filename.lastIndexOf('.')).toLowerCase();

		return loadFromFile(gl, inputStream, type, linearInterpolation, generateMipmaps, maxMipmapLevels);
	}

	public static GLTexture loadFromFile(final GL4 gl, final InputStream input, final String type,
			final boolean linearInterpolation,
			final boolean generateMipmaps, final int maxMipmapLevels) {
		BufferedImage bi;
		BufferedImage flippedImage;
		final ByteBuffer imageData;
		FileInputStream fis = null;
		FileChannel channel = null;
		int[] pixels = null;
		GLTexture tex;

		if (type.toLowerCase().endsWith("tga")) {
			byte[] buffer = null;

			try {
				BufferedInputStream s = new BufferedInputStream(input);
				buffer = new byte[s.available()];
				s.read(buffer);
				s.close();

				pixels = TGAReader.read(buffer, TGAReader.ARGB);
				final int width = TGAReader.getWidth(buffer);
				final int height = TGAReader.getHeight(buffer);
				bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				bi.setRGB(0, 0, width, height, pixels, 0, width);
			} catch (final Exception e) {
				System.err.println("GLTexture: could not read image from TGA");
				return null;
			}
		} else {
			try {
				bi = ImageIO.read(input);

			} catch (final Exception e) {
				System.err.println("GLTexture: could not read image." + e.getMessage());
				return null;
			}
		}

		// convert to OpenGL UV space
		flippedImage = createFlipped(bi);
		imageData = bufferedImageToRGBABuffer(flippedImage);

		int texWidth = 2;
		int texHeight = 2;

		while (texWidth < bi.getWidth()) {
			texWidth *= 2;
		}
		while (texHeight < bi.getHeight()) {
			texHeight *= 2;
		}

		int levels = Math.min(maxMipmapLevels,
				1 + (int) Math.floor(Math.log(Math.max(texWidth, texHeight) / Math.log(2.0))));

		if (!generateMipmaps) {
			levels = 1;
		}

		int channelCount = bi.getColorModel().getNumComponents();

		// work around a Java2D issue
		if (channelCount == 3 && type.toLowerCase().endsWith("png")) {
			channelCount = 4;
		}

		tex = new GLTexture(gl,
				nativeTypeEnumFromBufferedImage(bi),
				channelCount,
				texWidth, texHeight, 1,
				linearInterpolation,
				levels,
				32,
				true,
				flippedImage.getColorModel().getColorSpace().isCS_sRGB());

		tex.clear();
		tex.copyFrom(imageData);
		tex.updateMipMaps();

		return tex;
	}

	private static GLTypeEnum nativeTypeEnumFromBufferedImage(final BufferedImage bi) {
		switch (bi.getData().getDataBuffer().getDataType()) {
			case DataBuffer.TYPE_BYTE: {
				return GLTypeEnum.UnsignedByte;
			}
			case DataBuffer.TYPE_DOUBLE: {
				return GLTypeEnum.Double;
			}
			case DataBuffer.TYPE_INT: {
				return GLTypeEnum.UnsignedByte;
			}
			case DataBuffer.TYPE_SHORT: {
				return GLTypeEnum.Short;
			}
			default:
				return null;
		}
	}

	private static ByteBuffer bufferedImageToRGBABuffer(final BufferedImage bufferedImage) {
		ByteBuffer imageBuffer;
		WritableRaster raster;
		BufferedImage texImage;
		final ColorModel rgbColorModel;
		final ColorModel rgbaColorModel;

		int texWidth = 2;
		int texHeight = 2;

		while (texWidth < bufferedImage.getWidth()) {
			texWidth *= 2;
		}
		while (texHeight < bufferedImage.getHeight()) {
			texHeight *= 2;
		}

		if(bufferedImage.getColorModel().getColorSpace().isCS_sRGB()) {
		    rgbColorModel = glColorModelsRGB;
			rgbaColorModel = glAlphaColorModelsRGB;
		} else {
			rgbColorModel = glColorModel;
			rgbaColorModel = glAlphaColorModel;
		}

		if (bufferedImage.getColorModel().hasAlpha()) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
			texImage = new BufferedImage(rgbaColorModel, raster, false, new Hashtable<>());
		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
			texImage = new BufferedImage(rgbColorModel, raster, false, new Hashtable<>());
		}

		final Graphics g = texImage.getGraphics();
		g.setColor(new Color(0.0f, 0.0f, 0.0f, 1.0f));
		g.fillRect(0, 0, texWidth, texHeight);
		g.drawImage(bufferedImage, 0, 0, null);
		g.dispose();

		final byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		imageBuffer.rewind();

		return imageBuffer;
	}

	// the following three routines are from
	// http://stackoverflow.com/a/23458883/2129040,
	// authored by MarcoG
	private static BufferedImage createFlipped(final BufferedImage image) {
		final AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getScaleInstance(1, -1));
		at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
		return createTransformed(image, at);
	}

	private static BufferedImage createRotated(final BufferedImage image) {
		final AffineTransform at = AffineTransform.getRotateInstance(
				Math.PI, image.getWidth() / 2, image.getHeight() / 2.0);
		return createTransformed(image, at);
	}

	private static BufferedImage createTransformed(
			final BufferedImage image, final AffineTransform at) {
		final BufferedImage newImage = new BufferedImage(
				image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = newImage.createGraphics();
		g.transform(at);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}

}
