package cleargl;

import javax.media.opengl.GL;
import javax.media.opengl.GL4;
import javax.media.opengl.GLException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by ulrik on 05/02/15.
 */
public class CLGeometryObject implements GLCloseable, GLInterface {

  GL4 gl;
  GLProgram prog;

  GLMatrix model;
  GLMatrix view;
  GLMatrix modelview;
  GLMatrix projection;

  int[] VertexArrayObject = new int[1];
  int[] VertexBuffers = new int[3];
  int[] IndexBuffer = new int[1];

  int geometryType;
  // length of vectors and texcoords
  int geometrySize = 3;
  int textureCoordSize = 2;

  int storedIndexCount = 0;
  int storedPrimitiveCount = 0;

  int id;
  static int counter = 0;

  IntBuffer indices;
  FloatBuffer vertices;
  FloatBuffer normals;
  FloatBuffer textureCoords;

  public CLGeometryObject(GL4 pGL, int vectorSize, int glGeometryType) {
    gl = pGL;

    geometrySize = vectorSize;
    textureCoordSize = geometrySize - 1;
    geometryType = glGeometryType;

    id = counter;
    counter++;

    // generate VAO for attachment of VBO and indices
    getGL().glGenVertexArrays(1, VertexArrayObject, 0);

    // generate three VBOs for coords, normals, texcoords
    getGL().glGenBuffers(3, VertexBuffers, 0);
    getGL().glGenBuffers(1, IndexBuffer, 0);
  }

  private static void printBuffer(FloatBuffer buf) {
    buf.rewind();
    System.err.print(buf.toString() + ": ");
    for (int i = 0; i < buf.remaining(); i++) {
      System.err.print(buf.get(i) + " ");
    }

    System.err.println(" ");

    buf.rewind();
  }

  private static void printBuffer(IntBuffer buf) {
    buf.rewind();
    System.err.print(buf.toString() + ": ");
    for (int i = 0; i < buf.remaining(); i++) {
      System.err.print(buf.get(i) + " ");
    }

    System.err.println(" ");

    buf.rewind();
  }

  public void setProgram(GLProgram program) {
    prog = program;
  }

  public void setVerticesAndCreateBuffer(FloatBuffer vertexBuffer) {
    getGL().glBindVertexArray(VertexArrayObject[0]);
    getGL().glBindBuffer(GL4.GL_ARRAY_BUFFER, VertexBuffers[0]);

    getGL().glEnableVertexAttribArray(0);
    getGL().glBufferData(	GL.GL_ARRAY_BUFFER,
            vertexBuffer.limit() * (Float.SIZE / Byte.SIZE),
            vertexBuffer,
            GL.GL_STATIC_DRAW);

    storedPrimitiveCount = vertexBuffer.remaining() / geometrySize;

    getGL().glVertexAttribPointer(0, geometrySize, GL4.GL_FLOAT, false, 0, 0);

    getGL().glBindVertexArray(0);
    getGL().glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
  }

  public void setNormalsAndCreateBuffer(FloatBuffer normalBuffer) {
    getGL().glBindVertexArray(VertexArrayObject[0]);
    getGL().glBindBuffer(GL4.GL_ARRAY_BUFFER, VertexBuffers[1]);

    getGL().glEnableVertexAttribArray(1);
    getGL().glBufferData(	GL.GL_ARRAY_BUFFER,
            normalBuffer.limit() * (Float.SIZE / Byte.SIZE),
            normalBuffer,
            GL.GL_STATIC_DRAW);

    getGL().glVertexAttribPointer(1, geometrySize, GL4.GL_FLOAT, false, 0, 0);

    getGL().glBindVertexArray(0);
    getGL().glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
  }

  public void setTextureCoordsAndCreateBuffer(FloatBuffer texcoordsBuffer) {
    getGL().glBindVertexArray(VertexArrayObject[0]);
    getGL().glBindBuffer(GL4.GL_ARRAY_BUFFER, VertexBuffers[2]);

    getGL().glEnableVertexAttribArray(2);
    getGL().glBufferData(	GL.GL_ARRAY_BUFFER,
            texcoordsBuffer.limit() * (Float.SIZE / Byte.SIZE),
            texcoordsBuffer,
            GL.GL_STATIC_DRAW);

    getGL().glVertexAttribPointer(2, textureCoordSize, GL4.GL_FLOAT, false, 0, 0);

    getGL().glBindVertexArray(0);
    getGL().glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
  }

  public void setMVP(GLMatrix m, GLMatrix v, GLMatrix p) {
    model = m;
    view = v;
    projection = p;
  }

  public void setModelView(GLMatrix mv) {
    modelview = mv;
  }

  public void setProjection(GLMatrix p) {
    projection = p;
  }

  public void setIndicesAndCreateBuffer(IntBuffer indexBuffer) {
    getGL().glBindVertexArray(VertexArrayObject[0]);
    getGL().glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, IndexBuffer[0]);

    getGL().glBufferData(	GL.GL_ELEMENT_ARRAY_BUFFER,
            indexBuffer.limit() * (Integer.SIZE / Byte.SIZE),
            indexBuffer,
            GL.GL_STATIC_DRAW);

    storedIndexCount = indexBuffer.remaining();

    getGL().glBindVertexArray(0);
    getGL().glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, 0);
  }

  public void draw() {
    prog.use(getGL());
    prog.getUniform("modelview").setFloatMatrix(modelview.getFloatArray(), false);
    prog.getUniform("projection").setFloatMatrix(projection.getFloatArray(), false);

    getGL().glBindVertexArray(VertexArrayObject[0]);

    if(storedIndexCount > 0) {
      getGL().glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, IndexBuffer[0]);
      getGL().glDrawElements(geometryType, 36, GL4.GL_UNSIGNED_INT, 0);

      getGL().glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, 0);
    } else {
      getGL().glDrawArrays(geometryType, 0, storedPrimitiveCount*geometrySize);
    }

    getGL().glUseProgram(0);
  }

  @Override
  public void close() throws GLException {
    getGL().glDeleteBuffers(VertexBuffers.length, VertexBuffers, 0);
    getGL().glDeleteVertexArrays(1, VertexArrayObject, 0);
  }

  @Override
  public GL4 getGL() {
    return gl;
  }

  @Override
  public int getId() {
    return id;
  }
}
