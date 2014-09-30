package cleargl;

import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLException;

public class GLVertexArray implements GLCloseable, GLInterface
{

	private GLInterface mGLInterface;
	private int[] mVertexArrayId;
	private int mNumberOfIndices;


	public GLVertexArray(GLInterface pGLInterface)
	{
		super();
		mGLInterface = pGLInterface;
		mVertexArrayId = new int[1];
		getGL().glGenVertexArrays(1, mVertexArrayId, 0);
	}

	@Override
	public void close() throws GLException
	{
		mGLInterface.getGL().glDeleteVertexArrays(1, mVertexArrayId, 0);
	}

	public void addVertexAttributeArray(GLVertexAttributeArray pGLVertexAttributeArray,
																			FloatBuffer pFloatBuffer)
	{
		bind();

		GLAttribute lAttribute = pGLVertexAttributeArray.getAttribute();

		pGLVertexAttributeArray.bind();

		final int lElementsPerIndex = pGLVertexAttributeArray.getElementsPerIndex();
		mNumberOfIndices = pFloatBuffer.remaining() / lElementsPerIndex;

		getGL().glBufferData(	GL.GL_ARRAY_BUFFER,
													pFloatBuffer.remaining() * (Float.SIZE / 8),
													pFloatBuffer,
													GL.GL_STATIC_DRAW);
		getGL().glEnableVertexAttribArray(lAttribute.getIndex());
		getGL().glVertexAttribPointer(lAttribute.getIndex(),
																	lElementsPerIndex,
																	GL.GL_FLOAT,
																	false,
																	0,
																	0);
	}

	public void bind()
	{
		getGL().glBindVertexArray(getId());
	}

	public void unbind()
	{
		getGL().glBindVertexArray(0);
	}

	public void draw(int pType)
	{
		bind();
		getGL().glDrawArrays(pType, 0, mNumberOfIndices);
	}

	@Override
	public GL3 getGL()
	{
		return mGLInterface.getGL();
	}

	@Override
	public int getId()
	{
		return mVertexArrayId[0];
	}

	@Override
	public String toString()
	{
		return "GLVertexArray [mGLInterface=" + mGLInterface
						+ ", mVertexArrayId="
						+ Arrays.toString(mVertexArrayId)
						+ ", mNumberOfIndices="
						+ mNumberOfIndices
						+ "]";
	}


}
