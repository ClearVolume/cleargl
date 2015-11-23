package cleargl.scenegraph;

class Scene extends Node {

  public Scene() {
    super("RootNode");
  }

  public void addNode(Node n, Node parent) {
    if(n.getName().equals("RootNode")) {
      throw new IllegalStateException("Only one RootNode may exist per scenegraph. Please choose a different name.");
    }
  }
}