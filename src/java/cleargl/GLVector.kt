@file:JvmName("GLVector")
package cleargl

import java.nio.FloatBuffer

/**
 * Created by ulrik on 19/11/15.
 */
open public class GLVector(vararg e: Float)  {
    protected var dimension: Int = 0
    protected var elements: FloatArray? = null

    init {
        elements = e
        dimension = e.size
    }

    @Throws(Exception::class)
    fun add(v: GLVector): GLVector {
        if (this.dimension != v.dimension) {
            throw Exception("Vector dimension mismatch: ${this.dimension} vs. ${v.dimension}")
        }

        val n = GLVector()
        val e = FloatArray(v.dimension)
        n.set(e)

        for (i in 0..this.dimension - 1) {
            n.setElement(i, this.getElement(i) + v.getElement(i))
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
        val n = GLVector()

        for (i in 0..this.dimension - 1) {
            n.setElement(i, this.getElement(i) * num)
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

    fun magnitude(): Float =
        Math.sqrt(elements!!.map { e -> Math.pow(e.toDouble(), 2.0)}.sum()).toFloat()

    fun normalize() {
        elements = elements!!.map { e -> e/magnitude() }.toFloatArray()
    }

    fun getNormalized(): GLVector {
        var n: GLVector = GLVector(0.0f, 0.0f, 0.0f)
        n.set(elements!!.map { e -> e/magnitude() }.toFloatArray())
        return n
    }

    operator fun plus(b: GLVector): GLVector {
        return add(b)
    }

    operator fun minus(b: GLVector): GLVector {
        return add(b.mult(-1.0f))
    }

    override fun toString(): String {
        return "(${elements!![0]}, ${elements!![2]}, ${elements!![1]})"
    }
}
