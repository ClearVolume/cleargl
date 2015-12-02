@file:JvmName("Camera")
package cleargl.scenegraph

import cleargl.GLVector

class Camera : Node("Camera") {

    var isTargeted = false
    protected var target: GLVector = GLVector(0.0f, 0.0f, 0.0f)

    init {
        this.nodeType = "Camera"
    }

}