package cleargl.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

/**
 * @author royer
 *
 */
public class GLVideoRecorder
{

	private ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime()
																																									.availableProcessors());

	private File mRootFolder;
	private volatile File mVideoFolder;
	private volatile long mVideoCounter;
	private volatile long mImageCounter;
	private volatile boolean mActive = false;

	private ThreadLocal<ByteBuffer> mPixelRGBBufferThreadLocal = new ThreadLocal<ByteBuffer>();
	private ThreadLocal<int[]> mPixelRGBIntsThreadLocal = new ThreadLocal<int[]>();

	public GLVideoRecorder(File pFolder)
	{
		super();
		setFolder(pFolder);
	}

	public File getFolder()
	{
		return mRootFolder;
	}

	public void setFolder(File pFolder)
	{
		mRootFolder = pFolder;
		mRootFolder.mkdirs();
	}

	public void toggleActive()
	{
		if (!mActive)
		{
			while (getNewVideoFolder())
				mVideoCounter++;
			mVideoFolder.mkdirs();

			mActive = true;
		}
		else
		{
			mActive = false;
		}
	}

	private boolean getNewVideoFolder()
	{
		String lVideoFolderName = String.format("Video.%d", mVideoCounter);
		mVideoFolder = new File(mRootFolder, lVideoFolderName);
		return mVideoFolder.exists();
	}

	public void screenshot(GLAutoDrawable pDrawable)
	{
		if (!mActive)
			return;
		String lFileName = String.format("image%d.png", mImageCounter);
		File lNewFile = new File(mVideoFolder, lFileName);
		writeDrawableToFile(pDrawable, lNewFile);
		mImageCounter++;
	}

	/**
	 * 
	 * Code adapted from: http://www.java-gaming.org/index.php/topic,5386.
	 * 
	 * @param pDrawable
	 *          JOGL drawable
	 * @param pOutputFile
	 *          output file
	 */
	private void writeDrawableToFile(	GLAutoDrawable pDrawable,
																		final File pOutputFile)
	{

		final int lWidth = pDrawable.getSurfaceWidth();
		final int lHeight = pDrawable.getSurfaceHeight();

		ByteBuffer lByteBuffer = mPixelRGBBufferThreadLocal.get();
		if (lByteBuffer == null || lByteBuffer.capacity() != lWidth * lHeight
																													* 3)
		{
			lByteBuffer = ByteBuffer.allocateDirect(lWidth * lHeight * 3)
															.order(ByteOrder.nativeOrder());
			mPixelRGBBufferThreadLocal.set(lByteBuffer);
		}

		final GL4 lGL4 = pDrawable.getGL().getGL4();

		lGL4.glReadBuffer(GL4.GL_BACK);
		lGL4.glPixelStorei(GL4.GL_PACK_ALIGNMENT, 1);
		lGL4.glReadPixels(0, // GLint x
											0, // GLint y
											lWidth, // GLsizei width
											lHeight, // GLsizei height
											GL4.GL_RGB, // GLenum format
											GL4.GL_UNSIGNED_BYTE, // GLenum type
											lByteBuffer); // GLvoid *pixels

		final ByteBuffer lFinalByteBuffer = lByteBuffer;
		mExecutorService.execute(new Runnable()
		{
			@Override
			public void run()
			{
				writeBufferToFile(pOutputFile,
													lWidth,
													lHeight,
													lFinalByteBuffer);

			}
		});

	}

	private void writeBufferToFile(	File pOutputFile,
																	int width,
																	int height,
																	ByteBuffer lByteBuffer)
	{
		try
		{
			int[] lPixelInts = mPixelRGBIntsThreadLocal.get();
			if (lPixelInts == null || lPixelInts.length != width * height)
			{
				lPixelInts = new int[width * height];
				mPixelRGBIntsThreadLocal.set(lPixelInts);
			}

			// Convert RGB bytes to ARGB ints with no transparency. Flip image
			// vertically by reading the
			// rows of pixels in the byte buffer in reverse - (0,0) is at bottom left
			// in
			// OpenGL.

			int p = width * height * 3; // Points to first byte (red) in each row.
			int q; // Index into ByteBuffer
			int i = 0; // Index into target int[]
			int w3 = width * 3; // Number of bytes in each row

			for (int row = 0; row < height; row++)
			{
				p -= w3;
				q = p;
				for (int col = 0; col < width; col++)
				{
					int iR = lByteBuffer.get(q++);
					int iG = lByteBuffer.get(q++);
					int iB = lByteBuffer.get(q++);

					lPixelInts[i++] = 0xFF000000 | ((iR & 0x000000FF) << 16)
														| ((iG & 0x000000FF) << 8)
														| (iB & 0x000000FF);
				}

			}

			BufferedImage bufferedImage = new BufferedImage(width,
																											height,
																											BufferedImage.TYPE_INT_ARGB);

			bufferedImage.setRGB(0, 0, width, height, lPixelInts, 0, width);

			ImageIO.write(bufferedImage, "PNG", pOutputFile);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

}
