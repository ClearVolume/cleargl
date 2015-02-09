package cleargl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL4;
import javax.media.opengl.GLException;

/**
 * ClearGeometryObject -
 * 
 * Created by Ulrik Guenter on 05/02/15.
 */
public class ClearGeometryObject implements GLCloseable, GLInterface
{

	private GLProgram mGLProgram;

	private GLMatrix mModelMatrix;
	private GLMatrix mViewMatrix;
	private GLMatrix mModelViewMatrix;
	private GLMatrix mProjectionMatrix;

	private int[] mVertexArrayObject = new int[1];
	private int[] mVertexBuffers = new int[3];
	private int[] IndexBuffer = new int[1];

	private boolean mIsDynamic = false;

	private int mGeometryType;
	// length of vectors and texcoords
	private int mGeometrySize = 3;
	private int mTextureCoordSize = 2;

	private int mStoredIndexCount = 0;
	private int mStoredPrimitiveCount = 0;

	private int mId;
	private static int counter = 0;

	public ClearGeometryObject(	GLProgram pGLProgram,
															int pVectorSize,
															int pGeometryType)
	{
		mGLProgram = pGLProgram;
		mGeometrySize = pVectorSize;
		mTextureCoordSize = mGeometrySize - 1;
		mGeometryType = pGeometryType;

		mId = counter;
		counter++;

		// generate VAO for attachment of VBO and indices
		getGL().glGenVertexArrays(1, mVertexArrayObject, 0);

		// generate three VBOs for coords, normals, texcoords
		getGL().glGenBuffers(3, mVertexBuffers, 0);
		getGL().glGenBuffers(1, IndexBuffer, 0);
	}

	private static void printBuffer(FloatBuffer buf)
	{
		buf.rewind();
		System.err.print(buf.toString() + ": ");
		for (int i = 0; i < buf.remaining(); i++)
		{
			System.err.print(buf.get(i) + " ");
		}

		System.err.println(" ");

		buf.rewind();
	}

	private static void printBuffer(IntBuffer buf)
	{
		buf.rewind();
		System.err.print(buf.toString() + ": ");
		for (int i = 0; i < buf.remaining(); i++)
		{
			System.err.print(buf.get(i) + " ");
		}

		System.err.println(" ");

		buf.rewind();
	}

	public void setProgram(GLProgram program)
	{
		mGLProgram = program;
	}

	public void setVerticesAndCreateBuffer(FloatBuffer vertexBuffer)
	{
		getGL().glBindVertexArray(mVertexArrayObject[0]);
		getGL().glBindBuffer(GL4.GL_ARRAY_BUFFER, mVertexBuffers[0]);

		getGL().glEnableVertexAttribArray(0);
		getGL().glBufferData(	GL.GL_ARRAY_BUFFER,
													vertexBuffer.limit() * (Float.SIZE / Byte.SIZE),
													vertexBuffer,
													isIsDynamic() ? GL.GL_DYNAMIC_DRAW
																		: GL.GL_STATIC_DRAW);

		mStoredPrimitiveCount = vertexBuffer.remaining() / mGeometrySize;

		getGL().glVertexAttribPointer(0,
																	mGeometrySize,
																	GL4.GL_FLOAT,
																	false,
																	0,
																	0);

		getGL().glBindVertexArray(0);
		getGL().glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
	}

	public void updateVertices(FloatBuffer vertexBuffer)
	{
		if (!isIsDynamic())
			throw new UnsupportedOperationException("Cannot update non dynamic buffers!");

		getGL().glBindVertexArray(mVertexArrayObject[0]);
		getGL().glBindBuffer(GL4.GL_ARRAY_BUFFER, mVertexBuffers[0]);

		getGL().glEnableVertexAttribArray(0);
		getGL().glBufferSubData(GL.GL_ARRAY_BUFFER,
														0,
														vertexBuffer.limit() * (Float.SIZE / Byte.SIZE),
														vertexBuffer);

		mStoredPrimitiveCount = vertexBuffer.remaining() / mGeometrySize;

		getGL().glVertexAttribPointer(0,
																	mGeometrySize,
																	GL4.GL_FLOAT,
																	false,
																	0,
																	0);

		getGL().glBindVertexArray(0);
		getGL().glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
	}

	public void setNormalsAndCreateBuffer(FloatBuffer normalBuffer)
	{
		getGL().glBindVertexArray(mVertexArrayObject[0]);
		getGL().glBindBuffer(GL4.GL_ARRAY_BUFFER, mVertexBuffers[1]);

		getGL().glEnableVertexAttribArray(1);
		getGL().glBufferData(	GL.GL_ARRAY_BUFFER,
													normalBuffer.limit() * (Float.SIZE / Byte.SIZE),
													normalBuffer,
													isIsDynamic() ? GL.GL_DYNAMIC_DRAW
																		: GL.GL_STATIC_DRAW);

		getGL().glVertexAttribPointer(1,
																	mGeometrySize,
																	GL4.GL_FLOAT,
																	false,
																	0,
																	0);

		getGL().glBindVertexArray(0);
		getGL().glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
	}

	public void setTextureCoordsAndCreateBuffer(FloatBuffer texcoordsBuffer)
	{
		getGL().glBindVertexArray(mVertexArrayObject[0]);
		getGL().glBindBuffer(GL4.GL_ARRAY_BUFFER, mVertexBuffers[2]);

		getGL().glEnableVertexAttribArray(2);
		getGL().glBufferData(	GL.GL_ARRAY_BUFFER,
													texcoordsBuffer.limit() * (Float.SIZE / Byte.SIZE),
													texcoordsBuffer,
													isIsDynamic() ? GL.GL_DYNAMIC_DRAW
																		: GL.GL_STATIC_DRAW);

		getGL().glVertexAttribPointer(2,
																	mTextureCoordSize,
																	GL4.GL_FLOAT,
																	false,
																	0,
																	0);

		getGL().glBindVertexArray(0);
		getGL().glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
	}

	public void setIndicesAndCreateBuffer(IntBuffer indexBuffer)
	{
		getGL().glBindVertexArray(mVertexArrayObject[0]);
		getGL().glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, IndexBuffer[0]);

		getGL().glBufferData(	GL.GL_ELEMENT_ARRAY_BUFFER,
													indexBuffer.limit() * (Integer.SIZE / Byte.SIZE),
													indexBuffer,
													isIsDynamic() ? GL.GL_DYNAMIC_DRAW
																		: GL.GL_STATIC_DRAW);

		mStoredIndexCount = indexBuffer.remaining();

		getGL().glBindVertexArray(0);
		getGL().glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public void updateIndices(IntBuffer indexBuffer)
	{
		if (!isIsDynamic())
			throw new UnsupportedOperationException("Cannot update non dynamic buffers!");

		getGL().glBindVertexArray(mVertexArrayObject[0]);
		getGL().glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, IndexBuffer[0]);

		getGL().glBufferSubData(GL.GL_ELEMENT_ARRAY_BUFFER,
														0,
														indexBuffer.limit() * (Integer.SIZE / Byte.SIZE),
														indexBuffer);

		mStoredIndexCount = indexBuffer.remaining();

		getGL().glBindVertexArray(0);
		getGL().glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public void setMVP(GLMatrix m, GLMatrix v, GLMatrix p)
	{
		mModelMatrix = m;
		mViewMatrix = v;
		mProjectionMatrix = p;
	}

	public void setModelView(GLMatrix mv)
	{
		mModelViewMatrix = mv;
	}

	public void setProjection(GLMatrix p)
	{
		mProjectionMatrix = p;
	}

	public void draw()
	{
		mGLProgram.use(getGL());
		if (mModelViewMatrix != null)
			mGLProgram.getUniform("modelview")
								.setFloatMatrix(mModelViewMatrix.getFloatArray(),
																false);

		if (mProjectionMatrix != null)
			mGLProgram.getUniform("projection")
								.setFloatMatrix(mProjectionMatrix.getFloatArray(),
																false);

		getGL().glBindVertexArray(mVertexArrayObject[0]);

		if (mStoredIndexCount > 0)
		{
			getGL().glBindBuffer(	GL4.GL_ELEMENT_ARRAY_BUFFER,
														IndexBuffer[0]);
			getGL().glDrawElements(	mGeometryType,
															36,
															GL4.GL_UNSIGNED_INT,
															0);

			getGL().glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, 0);
		}
		else
		{
			getGL().glDrawArrays(	mGeometryType,
														0,
														mStoredPrimitiveCount * mGeometrySize);
		}

		getGL().glUseProgram(0);
	}

	@Override
	public void close() throws GLException
	{
		getGL().glDeleteBuffers(mVertexBuffers.length, mVertexBuffers, 0);
		getGL().glDeleteVertexArrays(1, mVertexArrayObject, 0);
	}

	@Override
	public GL4 getGL()
	{
		if (mGLProgram == null)
			return null;
		return mGLProgram.getGL();
	}

	@Override
	public int getId()
	{
		return mId;
	}

	public boolean isIsDynamic()
	{
		return mIsDynamic;
	}

	public void setDynamic(boolean pIsDynamic)
	{
		mIsDynamic = pIsDynamic;
	}

}
