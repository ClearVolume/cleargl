package cleargl;

import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.Quaternion;

public class GLMatrix
{

	private final float[] mMatrix;

	public GLMatrix()
	{
		mMatrix = new float[16];
	}

	public float get(int pRow, int pColumn)
	{
		return mMatrix[4 * pRow + pColumn];
	}

	public void set(int pRow, int pColumn, float pValue)
	{
		mMatrix[4 * pRow + pColumn] = pValue;
	}

	public void mult(int pRow, int pColumn, float pValue)
	{
		mMatrix[4 * pRow + pColumn] *= pValue;
	}

	public void setIdentity()
	{
		FloatUtil.makeIdentity(mMatrix);
	}

	public void mult(GLMatrix pGLMatrix)
	{
		FloatUtil.multMatrix(mMatrix, pGLMatrix.mMatrix);
	}

	public void multinv(GLMatrix pGLMatrix)
	{
		pGLMatrix.invert();
		mult(pGLMatrix);
	}

	public void setPerspectiveProjectionMatrix(	float pFOV,
																							float pAspectRatio,
																							float pNearPlane,
																							float pFarPlane)
	{
		FloatUtil.makePerspective(mMatrix,
															0,
															true,
															pFOV,
															pAspectRatio,
															pNearPlane,
															pFarPlane);
	}

	public void setOrthoProjectionMatrix(	float pLeft,
																				float pRight,
																				float pBottom,
																				float pTop,
																				float pZNear,
																				float pZFar)
	{
		FloatUtil.makeOrtho(mMatrix,
												0,
												true,
												pLeft,
												pRight,
												pBottom,
												pTop,
												pZNear,
												pZFar);
	}

	public static GLMatrix getOrthoProjectionMatrix(float pLeft,
																									float pRight,
																									float pBottom,
																									float pTop,
																									float pZNear,
																									float pZFar)
	{
		final GLMatrix lGLMatrix = new GLMatrix();
		lGLMatrix.setOrthoProjectionMatrix(	pLeft,
																				pRight,
																				pBottom,
																				pTop,
																				pZNear,
																				pZFar);
		return lGLMatrix;
	}

	public void setCamera(float pPosX,
												float pPosY,
												float pPosZ,
												float pLookAtX,
												float pLookAtY,
												float pLookAtZ,
												float pUpX,
												float pUpY,
												float pUpZ)
	{
		final float[] lPosition = new float[]
		{ pPosX, pPosY, pPosZ };
		final float[] lLookAt = new float[]
		{ pLookAtX, pLookAtY, pLookAtZ };
		final float[] lUp = new float[]
		{ pUpX, pUpY, pUpZ };

		FloatUtil.makeLookAt(	mMatrix,
													0,
													lPosition,
													0,
													lLookAt,
													0,
													lUp,
													0,
													new float[16]);

	}

	public void euler(final double bankX,
										final double headingY,
										final double attitudeZ)
	{
		FloatUtil.makeRotationEuler(mMatrix,
																0,
																(float) bankX,
																(float) headingY,
																(float) attitudeZ);

	}

	public void rotEuler(	final double bankX,
												final double headingY,
												final double attitudeZ)
	{
		final float[] lRotMatrix = FloatUtil.makeRotationEuler(	new float[16],
																											0,
																											(float) bankX,
																											(float) headingY,
																											(float) attitudeZ);
		FloatUtil.multMatrix(mMatrix, lRotMatrix);
	}

	public void translate(float pDeltaX, float pDeltaY, float pDeltaZ)
	{
		final float[] lTranslationMatrix = FloatUtil.makeTranslation(	new float[16],
																														true,
																														pDeltaX,
																														pDeltaY,
																														pDeltaZ);

		FloatUtil.multMatrix(mMatrix, lTranslationMatrix);
	}

	public void scale(float pScaleX, float pScaleY, float pScaleZ)
	{

		final float[] lScaleMatrix = FloatUtil.makeScale(	new float[16],
																								true,
																								pScaleX,
																								pScaleY,
																								pScaleZ);

		FloatUtil.multMatrix(mMatrix, lScaleMatrix);
	}

	public void mult(Quaternion pQuaternion)
	{
		final float[] lQuaternionMatrix = pQuaternion.toMatrix(new float[16], 0);
		FloatUtil.multMatrix(mMatrix, lQuaternionMatrix);
	}

	public void copy(final GLMatrix rhs)
	{
		System.arraycopy(	rhs.getFloatArray(),
											0,
											mMatrix,
											0,
											mMatrix.length);
	}

	public float[] getFloatArray()
	{
		return mMatrix;
	}

	public void invert()
	{
		final float[] tmp = new float[16];
		System.arraycopy(mMatrix, 0, tmp, 0, mMatrix.length);

		FloatUtil.invertMatrix(tmp, mMatrix);
	}

	public void transpose()
	{
		final float[] tmp = new float[16];
		System.arraycopy(mMatrix, 0, tmp, 0, mMatrix.length);

		FloatUtil.transposeMatrix(tmp, mMatrix);
	}

	@Override
	public String toString()
	{
		final StringBuilder lStringBuilder = new StringBuilder();
		FloatUtil.matrixToString(	lStringBuilder,
															"GLMatrix ",
															"%10.5f",
															mMatrix,
															0,
															4,
															4,
															true);
		return lStringBuilder.toString();
	}

}
