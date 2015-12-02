package cleargl.scenegraph

internal class Scene : Node("RootNode") {

    fun addNode(n: Node, parent: Node) {
        if (n.name == "RootNode") {
            throw IllegalStateException("Only one RootNode may exist per scenegraph. Please choose a different name.")
        }
    }
}