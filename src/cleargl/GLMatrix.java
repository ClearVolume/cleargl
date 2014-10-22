package cleargl;

import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.Quaternion;

public class GLMatrix
{

	private float[] mMatrix;

	public GLMatrix()
	{
		mMatrix = new float[16];
	}

	public void setIdentity()
	{
		FloatUtil.makeIdentity(mMatrix);
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
		float[] lPosition = new float[]
		{ pPosX, pPosY, pPosZ };
		float[] lLookAt = new float[]
		{ pLookAtX, pLookAtY, pLookAtZ };
		float[] lUp = new float[]
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

	public void translate(float pDeltaX, float pDeltaY, float pDeltaZ)
	{
		float[] lTranslationMatrix = FloatUtil.makeTranslation(	new float[16],
																														true,
																														pDeltaX,
																														pDeltaY,
																														pDeltaZ);

		FloatUtil.multMatrix(mMatrix, lTranslationMatrix);
	}

	public void mult(Quaternion pQuaternion)
	{
		float[] lQuaternionMatrix = pQuaternion.toMatrix(new float[16], 0);
		FloatUtil.multMatrix(mMatrix, lQuaternionMatrix);
	}

	public float[] getFloatArray()
	{
		return mMatrix;
	}

	public void invert()
	{
		FloatUtil.invertMatrix(mMatrix, mMatrix);
	}

	@Override
	public String toString()
	{
		StringBuilder lStringBuilder = new StringBuilder();
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
