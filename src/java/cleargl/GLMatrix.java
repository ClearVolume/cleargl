package cleargl;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

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

	public static GLMatrix getIdentity()
	{
		final GLMatrix lGLMatrix = new GLMatrix();
		lGLMatrix.setIdentity();
		return lGLMatrix;
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
		final float[] lQuaternionMatrix = pQuaternion.toMatrix(	new float[16],
																														0);
		FloatUtil.multMatrix(mMatrix, lQuaternionMatrix);
	}

	public float[] mult(float[] pVector)
	{
		final float[] lResultVector = new float[4];
		mulColMat4Vec4(lResultVector, mMatrix, pVector);
		return lResultVector;
	}

	private static float[] mulColMat4Vec4(final float[] result,
																				final float[] colMatrix,
																				final float[] vec)
	{

		result[0] = vec[0] * colMatrix[0]
								+ vec[1]
								* colMatrix[4]
								+ vec[2]
								* colMatrix[8]
								+ vec[3]
								* colMatrix[12];
		result[1] = vec[0] * colMatrix[1]
								+ vec[1]
								* colMatrix[5]
								+ vec[2]
								* colMatrix[9]
								+ vec[3]
								* colMatrix[13];
		result[2] = vec[0] * colMatrix[2]
								+ vec[1]
								* colMatrix[6]
								+ vec[2]
								* colMatrix[10]
								+ vec[3]
								* colMatrix[14];
		result[3] = vec[0] * colMatrix[3]
								+ vec[1]
								* colMatrix[7]
								+ vec[2]
								* colMatrix[11]
								+ vec[3]
								* colMatrix[15];

		return result;
	}

	private static float[] mulRowMat4Vec4(final float[] result,
																				final float[] colMatrix,
																				final float[] vec)
	{

		result[0] = vec[0] * colMatrix[0]
								+ vec[1]
								* colMatrix[1]
								+ vec[2]
								* colMatrix[2]
								+ vec[3]
								* colMatrix[3];
		result[1] = vec[0] * colMatrix[4]
								+ vec[1]
								* colMatrix[5]
								+ vec[2]
								* colMatrix[6]
								+ vec[3]
								* colMatrix[7];
		result[2] = vec[0] * colMatrix[8]
								+ vec[1]
								* colMatrix[9]
								+ vec[2]
								* colMatrix[10]
								+ vec[3]
								* colMatrix[11];
		result[3] = vec[0] * colMatrix[12]
								+ vec[1]
								* colMatrix[13]
								+ vec[2]
								* colMatrix[14]
								+ vec[3]
								* colMatrix[15];

		return result;
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

	public static void mult(float[] pVector, float pValue)
	{
		for (int i = 0; i < pVector.length; i++)
			pVector[i] *= pValue;
	}

	public static void add(float[] pVector, float pValue)
	{
		for (int i = 0; i < pVector.length; i++)
			pVector[i] += pValue;
	}

	public static void sub(float[] pA, float[] pB)
	{
		final int lLength = min(pA.length, pB.length);
		for (int i = 0; i < lLength; i++)
			pA[i] = pA[i] - pB[i];
	}

	public static void normalize(float[] pVector)
	{
		double lSumOfSquares = 0;
		for (int i = 0; i < pVector.length; i++)
			lSumOfSquares += pVector[i] * pVector[i];

		final double lNorm = sqrt(lSumOfSquares);

		if (abs(lNorm) == Double.MIN_VALUE)
			for (int i = 0; i < pVector.length; i++)
				pVector[i] = 0;

		for (int i = 0; i < pVector.length; i++)
			pVector[i] /= lNorm;
	}

}
