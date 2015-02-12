package cleargl;

import javax.media.opengl.GL4;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by ulrik on 11/02/15.
 */

public class ClearTextRenderer {
  protected BufferedImage image;
  protected Graphics2D g2d;
  protected GLProgram mProg;
  protected GLMatrix ModelMatrix = new GLMatrix();
  protected GLMatrix ViewMatrix = new GLMatrix();
  protected GLMatrix ProjectionMatrix = new GLMatrix();

  protected GL4 mGL4;
  protected final boolean mShouldCache;

  protected HashMap<String, ByteBuffer> textureCache = new HashMap<>();

  public ClearTextRenderer(GL4 pGL4, boolean shouldCache) {
    init(pGL4);
    mShouldCache = true;
  }

  public void init(GL4 pGL4) {
    mGL4 = pGL4;
    try {
      mProg = GLProgram.buildProgram(	pGL4,
              ClearTextRenderer.class,
              new String[]{"shaders/text_vert.glsl",
                      "shaders/text_frag.glsl"});
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void drawTextAtPosition(String text, int screenX, int screenY, Font font, FloatBuffer color, boolean antiAliased) {

    int windowSizeX = mGL4.getContext().getGLDrawable().getSurfaceWidth()/2;
    int windowSizeY = mGL4.getContext().getGLDrawable().getSurfaceHeight()/2;

    int width = text.length() * font.getSize();
    int height = font.getSize();

    // don't store more then 50 textures
    if(textureCache.size() > 50) {
      textureCache.clear();
    }

    if(!mShouldCache || (mShouldCache && !textureCache.containsKey(text))) {
      ByteBuffer imageBuffer;

      ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace
              .getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 8},
              true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
      WritableRaster raster;

      if (font == null) {
        System.err.println("Font invalid for text \"" + text + "\"");
        return;
      }

      raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
              font.getSize() * text.length(), 18, 4, null);

      image = new BufferedImage(glAlphaColorModel, raster, true, new Hashtable());

      g2d = image.createGraphics();
      g2d.setFont(font);

      if (antiAliased) {
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        g2d.setRenderingHints(rh);
      }

      g2d.drawString(text, 0, font.getSize());

      byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

      imageBuffer = ByteBuffer.allocateDirect(data.length);
      imageBuffer.order(ByteOrder.nativeOrder());
      imageBuffer.put(data, 0, data.length);
      imageBuffer.flip();

      if(!mShouldCache) {
        textureCache.clear();
      }

      textureCache.put(text, imageBuffer);
    }

    mGL4.glClear(GL4.GL_DEPTH_BUFFER_BIT | GL4.GL_STENCIL_BUFFER_BIT);

    mGL4.glDisable(mGL4.GL_CULL_FACE);
    mGL4.glDisable(mGL4.GL_DEPTH_TEST);

    mGL4.glEnable(mGL4.GL_BLEND);
    mGL4.glBlendFunc(mGL4.GL_SRC_ALPHA, mGL4.GL_ONE_MINUS_SRC_ALPHA);

    int[] uiTexture = new int[1];
    int[] ui_vbo = new int[3];
    int[] ui_vao = new int[1];

    float w = width;
    float h = height;
    float x = (float)screenX;
    float y = (float)screenY;

    FloatBuffer vertices = FloatBuffer.wrap(new float[]{
            x, y+h, 0.0f,
            x+w, y+h, 0.0f,
            x, y, 0.0f,
            x+w, y, 0.0f
    });

    FloatBuffer normals = FloatBuffer.wrap(new float[]{
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
    });

    FloatBuffer texCoords = FloatBuffer.wrap(new float[] {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
    });

    mGL4.glUseProgram(mProg.getId());

    ModelMatrix.setIdentity();
    ViewMatrix.setIdentity();
    ProjectionMatrix.setOrthoProjectionMatrix(0.0f, windowSizeX, 0.0f, windowSizeY, -1.0f, 1.0f);

    mGL4.glGenVertexArrays(1, ui_vao, 0);
    mGL4.glBindVertexArray(ui_vao[0]);
    mGL4.glGenBuffers(3, ui_vbo, 0);

    mGL4.glBindBuffer(GL4.GL_ARRAY_BUFFER, ui_vbo[0]);
    mGL4.glBufferData(GL4.GL_ARRAY_BUFFER, vertices.limit() * (Float.SIZE/Byte.SIZE), vertices, GL4.GL_STATIC_DRAW);
    mGL4.glEnableVertexAttribArray(0);
    mGL4.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);

    mGL4.glBindBuffer(GL4.GL_ARRAY_BUFFER, ui_vbo[1]);
    mGL4.glBufferData(GL4.GL_ARRAY_BUFFER, normals.limit() * (Float.SIZE/Byte.SIZE), normals, GL4.GL_STATIC_DRAW);
    mGL4.glEnableVertexAttribArray(1);
    mGL4.glVertexAttribPointer(1, 3, GL4.GL_FLOAT, false, 0, 0);

    mGL4.glBindBuffer(GL4.GL_ARRAY_BUFFER, ui_vbo[2]);
    mGL4.glBufferData(GL4.GL_ARRAY_BUFFER, texCoords.limit() * (Float.SIZE/Byte.SIZE), texCoords, GL4.GL_STATIC_DRAW);
    mGL4.glEnableVertexAttribArray(2);
    mGL4.glVertexAttribPointer(2, 2, GL4.GL_FLOAT, false, 0, 0);

    mGL4.glActiveTexture(GL4.GL_TEXTURE1);
    mGL4.glGenTextures(1, uiTexture, 0);
    mGL4.glBindTexture(GL4.GL_TEXTURE_2D, uiTexture[0]);

    mGL4.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
    mGL4.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);

    mGL4.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_BASE_LEVEL, 0);
    mGL4.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAX_LEVEL, 0);

    mGL4.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA8, width, height, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, textureCache.get(text));

    mProg.getUniform("uitex").set(1);
    ModelMatrix.mult(ViewMatrix);
    mProg.getUniform("ModelViewMatrix").setFloatMatrix(ModelMatrix.getFloatArray(), false);
    mProg.getUniform("ProjectionMatrix").setFloatMatrix(ProjectionMatrix.getFloatArray(), false);
    mGL4.glUseProgram(mProg.getId());

    mGL4.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);

    mGL4.glDisableVertexAttribArray(0);

    mGL4.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
    mGL4.glBindTexture(GL4.GL_TEXTURE_2D, 0);

    mGL4.glDeleteTextures(1, uiTexture, 0);
    mGL4.glDeleteBuffers(3, ui_vbo, 0);
    mGL4.glDeleteVertexArrays(1, ui_vao, 0);
  }

}
