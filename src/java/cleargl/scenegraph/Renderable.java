package cleargl.scenegraph;

import cleargl.GLMatrix;
import cleargl.GLProgram;

public interface Renderable {
  public void setProgram(GLProgram program);
  public void draw();
  public void setMVP(GLMatrix m, GLMatrix v, GLMatrix p);
  public void setModel(GLMatrix model);
  public void setView(GLMatrix view);
  public void setModelView(GLMatrix mv);
  public void setProjection(GLMatrix p);

  public void updateWorld(boolean recursive);
}