package cleargl;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import coremem.types.NativeTypeEnum;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description>
 *
 * @author Ulrik Günther <hello@ulrik.is>
 */
public class GLFramebuffer {
  protected int framebufferId[];
  protected int buffers[];
  protected List<GLTexture> backingTextures;
  protected List<GLTexture> depthBuffers;
  protected int width;
  protected int height;
  protected boolean initialized;

  public GLFramebuffer(GL4 gl, int width, int height) {
    framebufferId = new int[1];
    buffers = new int[16];
    backingTextures = new ArrayList<>();
    depthBuffers = new ArrayList<>();
    this.width = width;
    this.height = height;

    gl.getGL().glGenFramebuffers(1, framebufferId, 0);

    initialized = true;
  }

  public void addFloatBuffer(GL4 gl, int channelDepth) {
    if(!initialized) {
      return;
    }

    gl.getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, getId());

    backingTextures.add(new GLTexture(
            gl,
            NativeTypeEnum.Float,
            3,
            width, height, 1, true, 1
    ));

    gl.getGL().glFramebufferTexture2D(GL.GL_FRAMEBUFFER,
            getCurrentFramebufferColorAttachment(),
            GL.GL_TEXTURE_2D,
            backingTextures.get(backingTextures.size()-1).getId(),
            1
    );

    gl.getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
  }

  public void addFloatRGBBuffer(GL4 gl, int channelDepth) {
    if(!initialized) {
      return;
    }

    gl.getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, getId());

    backingTextures.add(new GLTexture(
            gl,
            NativeTypeEnum.Float,
            3,
            width, height, 1, true, 1
    ));

    gl.getGL().getGL4().glFramebufferTexture(GL.GL_FRAMEBUFFER,
            getCurrentFramebufferColorAttachment(),
            backingTextures.get(backingTextures.size()-1).getId(),
            0
    );

    gl.getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
  }

  public void addUnsignedByteRGBABuffer(GL4 gl, int channelDepth) {
    if(!initialized) {
      return;
    }

    gl.getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, getId());

    backingTextures.add(new GLTexture(
            gl,
            NativeTypeEnum.UnsignedByte,
            4,
            width, height, 1, true, 1
    ));

    gl.getGL().glFramebufferTexture2D(GL.GL_FRAMEBUFFER,
            getCurrentFramebufferColorAttachment(),
            GL.GL_TEXTURE_2D,
            backingTextures.get(backingTextures.size()-1).getId(),
            0
    );

    gl.getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
  }

  public void addDepthBuffer(GL4 gl, int depth) {
    if(!initialized) {
      return;
    }

    gl.getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, getId());

    depthBuffers.add(new GLTexture(
            gl,
            NativeTypeEnum.Float,
            -1,
            width, height, 1, true, 1
    ));

    gl.getGL().getGL4().glFramebufferTexture(GL.GL_FRAMEBUFFER,
            GL.GL_DEPTH_ATTACHMENT,
            depthBuffers.get(depthBuffers.size()-1).getId(),
            0
    );

    gl.getGL().glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
  }

  public boolean checkAndSetDrawBuffers(GL4 gl) {
    if(!initialized) {
      return false;
    }

    gl.getGL4().glBindFramebuffer(GL.GL_FRAMEBUFFER, getId());
    int status = gl.getGL().getGL4().glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);

    if(status != GL.GL_FRAMEBUFFER_COMPLETE) {
      System.err.println("Framebuffer is incomplete, " + Integer.toHexString(status));
      return false;
    }

    int attachments[] = new int[backingTextures.size()];
    for(int i = 0; i < backingTextures.size(); i++) {
      attachments[i] = GL.GL_COLOR_ATTACHMENT0 + i;
    }

    gl.getGL().getGL4().glBindFramebuffer(GL.GL_FRAMEBUFFER, getId());
    gl.getGL().getGL4().glDrawBuffers(backingTextures.size(), IntBuffer.wrap(attachments));

    return true;
  }

  public void bindTexturesToUnitsWithOffset(GL4 gl, int offset) {
    int colorUnits = 0;
    for(int i = 0; i < backingTextures.size(); i++) {
      gl.glActiveTexture(GL.GL_TEXTURE0 + offset + i);
      gl.glBindTexture(GL.GL_TEXTURE_2D, backingTextures.get(i).getId());
      colorUnits = i;
    }

    if(depthBuffers.size() > 0) {
      for (int i = 0; i < depthBuffers.size(); i++) {
        gl.glActiveTexture(GL.GL_TEXTURE0 + offset + i + colorUnits + 1);
        gl.glBindTexture(GL.GL_TEXTURE_2D, depthBuffers.get(i).getId());
      }
    }
  }

  public void revertToDefaultFramebuffer(GL4 gl) {
    gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
  }

  @Override
  public String toString() {
    String info;
    if(!initialized) {
      info = "GLFramebuffer (not initialized)\n";
    } else {
      info = "GLFramebuffer " + framebufferId[0] + "\n|\n";

      for (GLTexture att : backingTextures) {
        info += String.format("+-\tColor Attachment %s, %dx%d/%d*%d, 0x%s\n", Integer.toHexString(att.getId()), att.getWidth(), att.getHeight(), att.getChannels(), att.getBitsPerChannel(), Integer.toHexString(att.getInternalFormat()));
      }

      info += "|\n";

      for (GLTexture att : depthBuffers) {
        info += String.format("+-\tDepth Attachment %s, %dx%d/%d*%d, 0x%s\n", Integer.toHexString(att.getId()), att.getWidth(), att.getHeight(), att.getChannels(), att.getBitsPerChannel(), Integer.toHexString(att.getInternalFormat()));
      }
    }
    return info;
  }

  public int getId() {
    if(initialized) {
      return framebufferId[0];
    } else {
      return -1;
    }
  }

  private int getCurrentFramebufferColorAttachment() {
    return GL.GL_COLOR_ATTACHMENT0 + backingTextures.size() - 1;
  }
}