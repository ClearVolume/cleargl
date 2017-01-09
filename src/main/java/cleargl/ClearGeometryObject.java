package cleargl;

/**
 * Created by dibrov on 29/12/16.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLException;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Hashtable;

import cleargl.GLCloseable;
import cleargl.GLError;
import cleargl.GLInterface;
import cleargl.GLMatrix;
import cleargl.GLProgram;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Hashtable;

public class ClearGeometryObject implements GLCloseable, GLInterface {
    private GLProgram mGLProgram;
    private GLMatrix mModelMatrix;
    private GLMatrix mViewMatrix;
    private GLMatrix mModelViewMatrix;
    private GLMatrix mProjectionMatrix;
    private final Hashtable<String, Integer> additionalBufferIds = new Hashtable();
    private final int[] mVertexArrayObject = new int[1];
    private final int[] mVertexBuffers = new int[3];
    private final int[] mIndexBuffer = new int[1];
    private boolean mIsDynamic = false;
    private final int mGeometryType;
    private int mGeometrySize = 3;
    private int mTextureCoordSize = 2;
    private int mStoredIndexCount = 0;
    private int mStoredPrimitiveCount = 0;
    private final int mId;
    private static int counter = 0;

    public ClearGeometryObject(GLProgram pGLProgram, int pVectorSize, int pGeometryType) {
        this.mGLProgram = pGLProgram;
        this.mGeometrySize = pVectorSize;
        this.mTextureCoordSize = this.mGeometrySize - 1;
        this.mGeometryType = pGeometryType;
        this.mId = counter++;
        this.getGL().getGL3().glGenVertexArrays(1, this.mVertexArrayObject, 0);
        this.getGL().glGenBuffers(3, this.mVertexBuffers, 0);
        this.getGL().glGenBuffers(1, this.mIndexBuffer, 0);
    }

    private static void printBuffer(FloatBuffer buf) {
        buf.rewind();
        System.err.print(buf.toString() + ": ");

        for(int i = 0; i < buf.remaining(); ++i) {
            System.err.print(buf.get(i) + " ");
        }

        System.err.println(" ");
        buf.rewind();
    }

    private static void printBuffer(IntBuffer buf) {
        buf.rewind();
        System.err.print(buf.toString() + ": ");

        for(int i = 0; i < buf.remaining(); ++i) {
            System.err.print(buf.get(i) + " ");
        }

        System.err.println(" ");
        buf.rewind();
    }

    public void setProgram(GLProgram program) {
        this.mGLProgram = program;
    }

    public void setVerticesAndCreateBuffer(FloatBuffer pVertexBuffer) {
        this.mStoredPrimitiveCount = pVertexBuffer.remaining() / this.mGeometrySize;
        this.getGL().getGL3().glBindVertexArray(this.mVertexArrayObject[0]);
        this.getGL().glBindBuffer('袒', this.mVertexBuffers[0]);
        this.getGL().getGL3().glEnableVertexAttribArray(0);
        this.getGL().glBufferData('袒', (long)(pVertexBuffer.limit() * 4), pVertexBuffer, this.isDynamic()?'裨':'裤');
        this.getGL().getGL3().glVertexAttribPointer(0, this.mGeometrySize, 5126, false, 0, 0L);
        this.getGL().getGL3().glBindVertexArray(0);
        this.getGL().glBindBuffer('袒', 0);
    }

    public void setArbitraryAndCreateBuffer(String name, FloatBuffer pBuffer, int pBufferGeometrySize) {
        if(!this.additionalBufferIds.containsKey(name)) {
            this.getGL().glGenBuffers(1, this.mVertexBuffers, this.mVertexBuffers.length - 1);
            this.additionalBufferIds.put(name, Integer.valueOf(this.mVertexBuffers[this.mVertexBuffers.length - 1]));
        }

        this.mStoredPrimitiveCount = pBuffer.remaining() / this.mGeometrySize;
        this.getGL().getGL3().glBindVertexArray(this.mVertexArrayObject[0]);
        this.getGL().glBindBuffer('袒', this.mVertexBuffers[this.mVertexBuffers.length - 1]);
        this.getGL().getGL3().glEnableVertexAttribArray(0);
        this.getGL().glBufferData('袒', (long)(pBuffer.limit() * 4), pBuffer, this.isDynamic()?'裨':'裤');
        this.getGL().getGL3().glVertexAttribPointer(this.mVertexBuffers.length - 1, pBufferGeometrySize, 5126, false, 0, 0L);
        this.getGL().getGL3().glBindVertexArray(0);
        this.getGL().glBindBuffer('袒', 0);
    }

    public GLProgram getProgram() {
        return this.mGLProgram;
    }

    public void updateVertices(FloatBuffer pVertexBuffer) {
        this.mStoredPrimitiveCount = pVertexBuffer.remaining() / this.mGeometrySize;
        if(!this.isDynamic()) {
            throw new UnsupportedOperationException("Cannot update non dynamic buffers!");
        } else {
            this.getGL().getGL3().glBindVertexArray(this.mVertexArrayObject[0]);
            this.getGL().glBindBuffer('袒', this.mVertexBuffers[0]);
            this.getGL().getGL3().glEnableVertexAttribArray(0);
            this.getGL().glBufferData('袒', (long)(pVertexBuffer.limit() * 4), pVertexBuffer, this.isDynamic()?'裨':'裤');
            this.getGL().getGL3().glVertexAttribPointer(0, this.mGeometrySize, 5126, false, 0, 0L);
            this.getGL().getGL3().glBindVertexArray(0);
            this.getGL().glBindBuffer('袒', 0);
        }
    }

    public void setNormalsAndCreateBuffer(FloatBuffer pNormalBuffer) {
        this.getGL().getGL3().glBindVertexArray(this.mVertexArrayObject[0]);
        this.getGL().glBindBuffer('袒', this.mVertexBuffers[1]);
        this.getGL().getGL3().glEnableVertexAttribArray(1);
        this.getGL().glBufferData('袒', (long)(pNormalBuffer.limit() * 4), pNormalBuffer, this.isDynamic()?'裨':'裤');
        this.getGL().getGL3().glVertexAttribPointer(1, this.mGeometrySize, 5126, false, 0, 0L);
        this.getGL().getGL3().glBindVertexArray(0);
        this.getGL().glBindBuffer('袒', 0);
    }

    public void updateNormals(FloatBuffer pNormalBuffer) {
        if(!this.isDynamic()) {
            throw new UnsupportedOperationException("Cannot update non dynamic buffers!");
        } else {
            this.getGL().getGL3().glBindVertexArray(this.mVertexArrayObject[0]);
            this.getGL().glBindBuffer('袒', this.mVertexBuffers[1]);
            this.getGL().getGL3().glEnableVertexAttribArray(1);
            this.getGL().glBufferSubData('袒', 0L, (long)(pNormalBuffer.limit() * 4), pNormalBuffer);
            this.getGL().getGL3().glVertexAttribPointer(1, this.mGeometrySize, 5126, false, 0, 0L);
            this.getGL().getGL3().glBindVertexArray(0);
            this.getGL().glBindBuffer('袒', 0);
        }
    }

    public void setTextureCoordsAndCreateBuffer(FloatBuffer pTextureCoordsBuffer) {
        this.getGL().getGL3().glBindVertexArray(this.mVertexArrayObject[0]);
        this.getGL().glBindBuffer('袒', this.mVertexBuffers[2]);
        this.getGL().getGL3().glEnableVertexAttribArray(2);
        this.getGL().glBufferData('袒', (long)(pTextureCoordsBuffer.limit() * 4), pTextureCoordsBuffer, this.isDynamic()?'裨':'裤');
        this.getGL().getGL3().glVertexAttribPointer(2, this.mTextureCoordSize, 5126, false, 0, 0L);
        this.getGL().getGL3().glBindVertexArray(0);
        this.getGL().glBindBuffer('袒', 0);
    }

    public void updateTextureCoords(FloatBuffer pTextureCoordsBuffer) {
        if(!this.isDynamic()) {
            throw new UnsupportedOperationException("Cannot update non dynamic buffers!");
        } else {
            this.getGL().getGL3().glBindVertexArray(this.mVertexArrayObject[0]);
            GLError.printGLErrors(this.getGL(), "1");
            this.getGL().getGL3().glBindBuffer('袒', this.mVertexBuffers[2]);
            GLError.printGLErrors(this.getGL(), "2");
            this.getGL().getGL3().glEnableVertexAttribArray(2);
            GLError.printGLErrors(this.getGL(), "3");
            this.getGL().glBufferSubData('袒', 0L, (long)(pTextureCoordsBuffer.limit() * 4), pTextureCoordsBuffer);
            GLError.printGLErrors(this.getGL(), "4");
            this.getGL().getGL3().glVertexAttribPointer(2, this.mTextureCoordSize, 5126, false, 0, 0L);
            GLError.printGLErrors(this.getGL(), "5");
            this.getGL().getGL3().glBindVertexArray(0);
            GLError.printGLErrors(this.getGL(), "6");
            this.getGL().glBindBuffer('袒', 0);
            GLError.printGLErrors(this.getGL(), "7");
        }
    }

    public void setIndicesAndCreateBuffer(IntBuffer pIndexBuffer) {
        this.mStoredIndexCount = pIndexBuffer.remaining();
        this.getGL().getGL3().glBindVertexArray(this.mVertexArrayObject[0]);
        this.getGL().glBindBuffer('袓', this.mIndexBuffer[0]);
        this.getGL().glBufferData('袓', (long)(pIndexBuffer.limit() * 4), pIndexBuffer, this.isDynamic()?'裨':'裤');
        this.getGL().getGL3().glBindVertexArray(0);
        this.getGL().glBindBuffer('袓', 0);
    }

    public void updateIndices(IntBuffer pIndexBuffer) {
        if(!this.isDynamic()) {
            throw new UnsupportedOperationException("Cannot update non dynamic buffers!");
        } else {
            this.mStoredIndexCount = pIndexBuffer.remaining();
            this.getGL().getGL3().glBindVertexArray(this.mVertexArrayObject[0]);
            this.getGL().glBindBuffer('袓', this.mIndexBuffer[0]);
            this.getGL().glBufferSubData('袓', 0L, (long)(pIndexBuffer.limit() * 4), pIndexBuffer);
            this.getGL().getGL3().glBindVertexArray(0);
            this.getGL().glBindBuffer('袓', 0);
        }
    }

    public void setMVP(GLMatrix m, GLMatrix v, GLMatrix p) {
        this.mModelMatrix = m;
        this.mViewMatrix = v;
        this.mProjectionMatrix = p;
    }

    public void setModelView(GLMatrix mv) {
        this.mModelViewMatrix = mv;
    }

    public void setProjection(GLMatrix p) {
        this.mProjectionMatrix = p;
    }

    public void draw() {
        if(this.mStoredIndexCount > 0) {
            this.draw(0, this.mStoredIndexCount);
        } else {
            this.draw(0, this.mStoredPrimitiveCount);
        }

    }

    public void draw(int pOffset, int pCount) {
        this.mGLProgram.use(this.getGL());
        if(this.mModelViewMatrix != null) {
            this.mGLProgram.getUniform("modelview").setFloatMatrix(this.mModelViewMatrix.getFloatArray(), false);
        }

        if(this.mProjectionMatrix != null) {
            this.mGLProgram.getUniform("projection").setFloatMatrix(this.mProjectionMatrix.getFloatArray(), false);
        }

        this.getGL().getGL3().glBindVertexArray(this.mVertexArrayObject[0]);
        if(this.mStoredIndexCount > 0) {
            this.getGL().glBindBuffer('袓', this.mIndexBuffer[0]);
            this.getGL().glDrawElements(this.mGeometryType, pCount, 5125, (long)pOffset);
            this.getGL().glBindBuffer('袓', 0);
        } else {
            this.getGL().glDrawArrays(this.mGeometryType, pOffset, pCount);
        }

        this.getGL().getGL3().glUseProgram(0);
    }

    public void close() throws GLException {
        this.getGL().getGL3().glDeleteVertexArrays(this.mVertexArrayObject.length, this.mVertexArrayObject, 0);
        this.getGL().glDeleteBuffers(this.mVertexBuffers.length, this.mVertexBuffers, 0);
        this.getGL().glDeleteBuffers(this.mIndexBuffer.length, this.mIndexBuffer, 0);
    }

    public GL getGL() {
        return this.mGLProgram == null?null:this.mGLProgram.getGL();
    }

    public int getId() {
        return this.mId;
    }

    public boolean isDynamic() {
        return this.mIsDynamic;
    }

    public void setDynamic(boolean pIsDynamic) {
        this.mIsDynamic = pIsDynamic;
    }
}
