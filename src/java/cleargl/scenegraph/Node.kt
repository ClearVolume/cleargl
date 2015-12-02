@file:JvmName("Node")
package cleargl.scenegraph

import cleargl.GLMatrix
import cleargl.GLProgram
import cleargl.GLVector
import com.jogamp.opengl.math.Quaternion
import java.sql.Timestamp
import java.util.*

open class Node(open var name: String) : Renderable {
    var nodeType = "Node"

    open protected var program: GLProgram? = null
    protected var model: GLMatrix = GLMatrix.getIdentity()

    protected var customViewProjection = false
    protected var view: GLMatrix = GLMatrix.getIdentity()
    protected var projection: GLMatrix = GLMatrix.getIdentity()
    var modelview: GLMatrix = GLMatrix.getIdentity()
    var mvp: GLMatrix? = GLMatrix.getIdentity()

    var position: GLVector = GLVector(0.0f, 0.0f, 0.0f)
    var scale: GLVector = GLVector(1.0f, 1.0f, 1.0f)
    var rotation: Quaternion = Quaternion(0.0f, 0.0f, 0.0f, 1.0f);

    protected var children: MutableList<Node>

    // metadata
    var createdAt: Long = 0
    var modifiedAt: Long = 0
    protected var needsUpdate = false
    var parent: Node? = null

    init {
        this.createdAt = (Timestamp(Date().time).time).toLong()

        this.children = ArrayList<Node>()
        // null should be the signal to use the default shader
        this.program = null
    }

    fun addChild(child: Node) {
        child.parent = this
        this.children.add(child)
    }

    fun removeChild(child: Node): Boolean {
        return this.children.remove(child)
    }

    fun removeChild(name: String): Boolean {
        for (c in this.children) {
            if (c.name.compareTo(name) == 0) {
                c.parent = null
                this.children.remove(c)
                return true
            }
        }

        return false
    }

    open fun draw() {

    }

    fun setMVP(m: GLMatrix, v: GLMatrix, p: GLMatrix) {

    }

    fun updateWorld(recursive: Boolean) {
        if (needsUpdate) {
            if (this.parent == null) {
                this.composeModel()
            } else {
                val m = parent!!.model
                this.composeModel()
                m.mult(this.model)

                this.model = m
            }
        }

        if (recursive) {
            for (c in this.children) {
                c.updateWorld(true)
            }
        }
    }

    fun composeModel() {
        val w = GLMatrix.getIdentity()
        w.mult(GLMatrix.getScaling(this.scale))
        w.mult(GLMatrix.getTranslation(this.position))
        w.mult(this.rotation)

        this.model = w
    }

    fun composeModelView() {
        modelview = model.clone()
        modelview!!.mult(this.view)
    }

    fun composeMVP() {
        composeModel()
        composeModelView()

        mvp = modelview!!.clone()
        mvp!!.mult(projection)
    }
}