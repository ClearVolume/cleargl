package cleargl;

/**
 * Created by ulrik on 19/11/15.
 */
public class GLVector {
  protected float[] elements;
  protected int dimension;

  public GLVector(float[] el) {
    this.elements = el;
    this.dimension = el.length;
  }

  public GLVector(float x, float y, float z) {
    this.elements = new float[]{x, y, z};
    this.dimension = 3;
  }

  public GLVector GLVector(float x, float y, float z) {
    this.elements = new float[]{x, y, z};
    this.dimension = 3;
    return this;
  }

  public GLVector(float x, float y, float z, float w) {
    this.elements = new float[]{x, y, z, w};
    this.dimension = 4;
  }

  public GLVector GLVector(float x, float y, float z, float w) {
    this.elements = new float[]{x, y, z, w};
    this.dimension = 4;
    return this;
  }

  public GLVector add(GLVector v) throws Exception {
    if(this.dimension != v.dimension) {
      throw new Exception("Vector dimension mismatch!");
    }

    GLVector n = new GLVector(this.getElements());

    for(int i = 0; i < this.dimension; i++) {
      n.setElement(i, n.getElement(i) + v.getElement(i));
    }

    return n;
  }

  public float scalarProduct(GLVector v) throws Exception {
    if(this.dimension != v.dimension) {
      throw new Exception("Vector dimension mismatch!");
    }

    float value = 0.0f;

    for(int i = 0; i < this.dimension; i++) {
      value += this.getElement(i) * v.getElement(i);
    }

    return value;
  }

  public GLVector mult(float num) {
    GLVector n = new GLVector(this.getElements());

    for(int i = 0; i < this.dimension; i++) {
      n.setElement(i, n.getElement(i)*num);
    }

    return n;
  }

  public GLVector mult(int num) {
    return mult((float)num);
  }

  public float[] getElements() {
    return elements;
  }

  public float getElement(int index) {
    return this.elements[index];
  }

  public void set(float[] elements) {
    this.elements = elements;
  }

  public void setElement(int index, float value) {
    this.elements[index] = value;
  }

  public float x() {
    return this.getElement(0);
  }

  public float y() {
    return this.getElement(1);
  }

  public float z() {
    return this.getElement(2);
  }

  public float w() {
    return this.getElement(3);
  }
}
