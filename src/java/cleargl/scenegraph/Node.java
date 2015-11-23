package cleargl.scenegraph;

import cleargl.GLMatrix;
import cleargl.GLProgram;
import cleargl.GLVector;
import com.jogamp.opengl.math.Quaternion;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Node implements Renderable, Serializable {
  protected String name;
  protected String nodeType = "Node";

  protected GLProgram program;
  protected GLMatrix model;

  protected boolean customModelView = false;
  protected GLMatrix view = null;
  protected GLMatrix projection = null;
  protected GLMatrix modelview = null;
  protected GLMatrix mvp = null;

  protected GLVector position;
  protected GLVector scale;
  protected Quaternion rotation;

  protected List<Node> children;

  // metadata
  protected long createdAt;
  protected long modifiedAt;
  protected boolean needsUpdate = false;
  protected Node parent;

  public Node getParent() {
    return parent;
  }

  public void setParent(Node parent) {
    this.parent = parent;
  }

  public String getNodeType() {
    return nodeType;
  }

  public void setNodeType(String nodeType) {
    this.nodeType = nodeType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public long getModifiedAt() {
    return modifiedAt;
  }

  public void setModifiedAt(long modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

  public Node(String name) {
    this.name = name;
    this.createdAt = (long)(new Timestamp(new Date().getTime()).getTime());

    this.children = new ArrayList<Node>();
    // null should be the signal to use the default shader
    this.program = null;
  }

  public void addChild(Node child) {
    child.setParent(this);
    this.children.add(child);
  }

  public boolean removeChild(Node child) {
    return this.children.remove(child);
  }

  public boolean removeChild(String name) {
    for(Node c: this.children) {
      if(c.getName().compareTo(name) == 0) {
        c.setParent(null);
        this.children.remove(c);
        return true;
      }
    }

    return false;
  }

  @Override
  public void setProgram(GLProgram program) {
    this.program = program;
  }

  @Override
  public void draw() {

  }

  @Override
  public void setMVP(GLMatrix m, GLMatrix v, GLMatrix p) {

  }

  public GLMatrix getModelview() {
    return this.modelview;
  }

  public GLMatrix getMVP() {
    return this.mvp;
  }

  public GLMatrix getProjection() {
    return this.projection;
  }

  @Override
  public void setModel(GLMatrix model) {
    this.model = model;
  }

  public GLMatrix getModel() {
    return this.model;
  }

  @Override
  public void setView(GLMatrix view) {
    this.view = view;
  }

  @Override
  public void setModelView(GLMatrix mv) {
    this.modelview = mv;
  }

  @Override
  public void setProjection(GLMatrix p) {
    this.projection = p;
  }

  @Override
  public void updateWorld(boolean recursive) {
    if(needsUpdate) {
      if (this.parent == null) {
        this.composeModel();
      } else {
        GLMatrix m = parent.getModel();
        this.composeModel();
        m.mult(this.model);

        this.model = m;
      }
    }

    if(recursive) {
      for (Node c : this.children) {
        c.updateWorld(true);
      }
    }
  }

  public void composeModel() {
    GLMatrix w = GLMatrix.getIdentity();
    w.mult(GLMatrix.getScaling(this.scale));
    w.mult(GLMatrix.getTranslation(this.position));
    w.mult(this.rotation);

    this.model = w;
  }

  public void composeModelView() {
    modelview = model.clone();
    modelview.mult(this.view);
  }

  public void composeMVP() {
    composeModel();
    composeModelView();

    mvp = modelview.clone();
    mvp.mult(projection);
  }

  public Node clone() {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(this);

      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream(bais);
      return (Node)ois.readObject();
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public GLVector getPosition() {
    return position;
  }

  public void setPosition(GLVector position) {
    this.position = position;
  }

  public GLVector getScale() {
    return scale;
  }

  public void setScale(GLVector scale) {
    this.scale = scale;
  }

  public Quaternion getRotation() {
    return rotation;
  }

  public void setRotation(Quaternion rotation) {
    this.rotation = rotation;
  }
}