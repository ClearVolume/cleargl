package cleargl.testprog;

import cleargl.demo.ClearGLDemo;

import com.jogamp.nativewindow.AbstractGraphicsDevice;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

public class JOGLTestProgram
{


	public static void main(String[] args) throws InterruptedException
	{
		final AbstractGraphicsDevice lDefaultDevice = GLProfile.getDefaultDevice();
		final GLProfile lProfile = GLProfile.getMaxProgrammable(true);
		final GLCapabilities lCapabilities = new GLCapabilities(lProfile);

		System.out.println("Device: " + lDefaultDevice);
		System.out.println("Capabilities: " + lCapabilities);
		System.out.println("Profile: " + lProfile);

		final ClearGLDemo lClearGLDemo = new ClearGLDemo();
		lClearGLDemo.demo();

	}

}
