package cleargl.scenegraph;

import cleargl.GLVector;

public class Camera extends Node {

  protected boolean isTargeted = false;
  protected GLVector target;

  public Camera() {
    this.type = "Camera";
  }

  public void setTarget(GLVector target) {
    this.target = target;
  }

  public boolean isTargeted() {
    return isTargeted;
  }

  public void setTargeted(boolean targeted) {
    isTargeted = targeted;
  }
}