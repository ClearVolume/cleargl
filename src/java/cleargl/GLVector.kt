@file:JvmName("GLVector")
package cleargl

import java.nio.FloatBuffer

/**
 * Created by ulrik on 19/11/15.
 */
open public class GLVector(e: FloatArray)  {
    protected var dimension: Int = 0
    protected var elements: FloatArray? = null

    init {
        elements = e
        dimension = e.size
    }

    constructor(x: Float, y: Float, z: Float) : this(floatArrayOf(x, y, z))
    constructor(x: Float, y: Float, z: Float, w: Float) : this(floatArrayOf(x, y, z, w))

    @Throws(Exception::class)
    fun add(v: GLVector): GLVector {
        if (this.dimension != v.dimension) {
            throw Exception("Vector dimension mismatch!")
        }

        val n = GLVector(this.elements!!)

        for (i in 0..this.dimension - 1) {
            n.setElement(i, n.getElement(i) + v.getElement(i))
        }

        return n
    }

    @Throws(Exception::class)
    fun scalarProduct(v: GLVector): Float {
        if (this.dimension != v.dimension) {
            throw Exception("Vector dimension mismatch!")
        }

        var value = 0.0f

        for (i in 0..this.dimension - 1) {
            value += this.getElement(i) * v.getElement(i)
        }

        return value
    }

    fun mult(num: Float): GLVector {
        val n = GLVector(this.elements!!)

        for (i in 0..this.dimension - 1) {
            n.setElement(i, n.getElement(i) * num)
        }

        return n
    }

    fun mult(num: Int): GLVector {
        return mult(num.toFloat())
    }

    fun getElement(index: Int): Float {
        return this.elements!![index]
    }

    fun set(elements: FloatArray) {
        this.elements = elements
    }

    fun setElement(index: Int, value: Float) {
        this.elements!![index] = value
    }

    fun x(): Float {
        return this.getElement(0)
    }

    fun y(): Float {
        return this.getElement(1)
    }

    fun z(): Float {
        return this.getElement(2)
    }

    fun w(): Float {
        return this.getElement(3)
    }

    fun toFloatBuffer(): FloatBuffer {
        return FloatBuffer.wrap(elements)
    }

    override fun toString(): String {
        return "(${elements!![0]}, ${elements!![2]}, ${elements!![1]})"
    }
}
