package com.mcmacker4.javelin.model

import com.mcmacker4.javelin.gl.vertex.VertexArrayObject
import com.mcmacker4.javelin.gl.vertex.VertexAttribute
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW
import org.lwjgl.system.MemoryUtil


object ModelLoader {

    private val planeVertices = floatArrayOf(
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f
    )
    
    private var planeVao: VertexArrayObject? = null
    
    fun plane() : VertexArrayObject {
        return planeVao ?: loadMesh(planeVertices)
    }
    
    fun loadMesh(vertices: FloatArray) : VertexArrayObject {
        val buffer = MemoryUtil.memAllocFloat(vertices.size).put(vertices)
        buffer.flip()
        val vbo = VertexAttribute(buffer, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 3)
        return VertexArrayObject(hashMapOf(Pair(VertexAttribute.ATTRIB_POSITION, vbo)))
    }
    
    fun loadObj(name: String) : VertexArrayObject {
        
        val vertices = arrayListOf<Vector3f>()
        val normals = arrayListOf<Vector3f>()
        val texCoords = arrayListOf<Vector2f>()

        val ordVertices = arrayListOf<Vector3f>()
        val ordNormals = arrayListOf<Vector3f>()
        val ordTexCoords = arrayListOf<Vector2f>()
        
        fun processFace(lineParts: List<String>) {
            lineParts.forEachIndexed { i, part ->
                if(i != 0) {
                    val indices = part.split("/").map { it.toInt() }
                    ordVertices.add(vertices[indices[0] - 1])
                    ordNormals.add(normals[indices[2] - 1])
                    ordTexCoords.add(texCoords[indices[1] - 1])
                }
            }
        }
        
        ClassLoader.getSystemResourceAsStream("models/$name.obj")
                .bufferedReader().lines().forEach { line ->
            
            val parts = line.split(" ")

            when {
                line.startsWith("f ") -> processFace(parts)
                line.startsWith("v ") -> vertices.add(Vector3f(parts[1].toFloat(), parts[2].toFloat(), parts[3].toFloat()))
                line.startsWith("vn ") -> normals.add(Vector3f(parts[1].toFloat(), parts[2].toFloat(), parts[3].toFloat()))
                line.startsWith("vt ") -> texCoords.add(Vector2f(parts[1].toFloat(), 1 - parts[2].toFloat()))
            }
        }
        
        val verticesBuffer = MemoryUtil.memAllocFloat(ordVertices.size * 3)
        val normalsBuffer = MemoryUtil.memAllocFloat(ordNormals.size * 3)
        val texCoordsBuffer = MemoryUtil.memAllocFloat(ordTexCoords.size * 2)
        
        ordVertices.forEach {
            verticesBuffer.put(it.x)
            verticesBuffer.put(it.y)
            verticesBuffer.put(it.z)
        }
        
        ordNormals.forEach {
            normalsBuffer.put(it.x)
            normalsBuffer.put(it.y)
            normalsBuffer.put(it.z)
        }
        
        ordTexCoords.forEach {
            texCoordsBuffer.put(it.x)
            texCoordsBuffer.put(it.y)
        }
        
        verticesBuffer.flip()
        normalsBuffer.flip()
        texCoordsBuffer.flip()
        
        val verticesAttribute = VertexAttribute(verticesBuffer, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 3)
        val normalsAttribute = VertexAttribute(normalsBuffer, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 3)
        val texCoordsAttribute = VertexAttribute(texCoordsBuffer, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 2)
        
        return VertexArrayObject(hashMapOf(
                Pair(VertexAttribute.ATTRIB_POSITION, verticesAttribute),
                Pair(VertexAttribute.ATTRIB_NORMAL, normalsAttribute),
                Pair(VertexAttribute.ATTRIB_TEXTURE_COORD, texCoordsAttribute)
        ))
        
    }
    
}