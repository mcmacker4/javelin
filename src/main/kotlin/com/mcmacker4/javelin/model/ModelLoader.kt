package com.mcmacker4.javelin.model

import com.mcmacker4.javelin.gl.vertex.VertexArrayObject
import com.mcmacker4.javelin.gl.vertex.VertexAttribute
import com.mcmacker4.javelin.gl.vertex.VertexBufferObject
import com.mcmacker4.javelin.util.minus
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.glfwGetTime
import org.lwjgl.opengl.GL15.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.stream.Collectors


object ModelLoader {

    private var planeVao: VertexArrayObject? = null
    
    fun plane() : VertexArrayObject {
        return planeVao ?: loadMesh(planeVertices, planeNormals, texCoords, tangents)
    }
    
    private fun toFloatBuffer(data: FloatArray) : FloatBuffer {
        val buffer = MemoryUtil.memAllocFloat(data.size)
        buffer.put(data)
        buffer.flip()
        return buffer
    }

    fun loadMesh(vertices: FloatArray, normals: FloatArray, texCoords: FloatArray, tangents: FloatArray) : VertexArrayObject {
        val verticesAttribute = VertexAttribute(toFloatBuffer(vertices), GL_ARRAY_BUFFER, GL_STATIC_DRAW, 3)
        val normalsAttribute = VertexAttribute(toFloatBuffer(normals), GL_ARRAY_BUFFER, GL_STATIC_DRAW, 3)
        val texCoordsAttribute = VertexAttribute(toFloatBuffer(texCoords), GL_ARRAY_BUFFER, GL_STATIC_DRAW, 2)
        val tangentsAttribute = VertexAttribute(toFloatBuffer(tangents), GL_ARRAY_BUFFER, GL_STATIC_DRAW, 3)
        return VertexArrayObject(hashMapOf(
                Pair(VertexAttribute.ATTRIB_POSITION, verticesAttribute),
                Pair(VertexAttribute.ATTRIB_NORMAL, normalsAttribute),
                Pair(VertexAttribute.ATTRIB_TEXTURE_COORD, texCoordsAttribute),
                Pair(VertexAttribute.ATTRIB_TANGENTS, tangentsAttribute)
        ))
    }
    
    fun loadObj(name: String) : VertexArrayObject {
        
        val path = "models/$name.obj"
        val startTime = glfwGetTime()
        
        val vertices = arrayListOf<Vector3f>()
        val normals = arrayListOf<Vector3f>()
        val texCoords = arrayListOf<Vector2f>()

        val lines = ClassLoader.getSystemResourceAsStream(path)
                .bufferedReader().lines().collect(Collectors.toList())
        
        lines.forEach { line ->
            val parts = line.split(" ")
            when {
                line.startsWith("v ") -> vertices.add(Vector3f(parts[1].toFloat(), parts[2].toFloat(), parts[3].toFloat()))
                line.startsWith("vn ") -> normals.add(Vector3f(parts[1].toFloat(), parts[2].toFloat(), parts[3].toFloat()))
                line.startsWith("vt ") -> texCoords.add(Vector2f(parts[1].toFloat(), 1 - parts[2].toFloat()))
            }
        }
        
        val vao = processFaces(lines.filter { it.startsWith("f ") }, vertices, normals, texCoords)
        
        val elapsedTime = Math.round((glfwGetTime() - startTime) * 1000)
        println("Loaded OBJ: $path ($elapsedTime ms)")
        
        return vao
    }
    
    private fun processFaces(
            lines: List<String>,
            vertices: ArrayList<Vector3f>,
            normals: ArrayList<Vector3f>,
            texCoords: ArrayList<Vector2f>) : VertexArrayObject {
        
        val ordVertices = arrayListOf<Vector3f>()
        val ordNormals = arrayListOf<Vector3f>()
        val ordTexCoords = arrayListOf<Vector2f>()
        val ordTangents = arrayListOf<Vector3f>()
        
        fun processVertex(vIndices: List<Int>) {
            //Vertex
            ordVertices.add(vertices[vIndices[0]])
            //Normal
            ordNormals.add(normals[vIndices[2]])
            //TexCoord (UV)
            ordTexCoords.add(texCoords[vIndices[1]])
        }
        
        fun calculateTangent(
                vertex1: Pair<Vector3f, Vector2f>,
                vertex2: Pair<Vector3f, Vector2f>,
                vertex3: Pair<Vector3f, Vector2f>) {
            
            val tangent = Vector3f()
            
            val edge1 = vertex2.first - vertex1.first
            val edge2 = vertex3.first - vertex1.first
            val deltaUV1 = vertex2.second - vertex1.second 
            val deltaUV2 = vertex3.second - vertex1.second
            
            val f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y)

            tangent.x = f * (deltaUV2.y * edge1.x - deltaUV1.y * edge2.x)
            tangent.y = f * (deltaUV2.y * edge1.y - deltaUV1.y * edge2.y)
            tangent.z = f * (deltaUV2.y * edge1.z - deltaUV1.y * edge2.z)
            
            tangent.normalize()
            
            ordTangents.add(tangent)
            
        }
        
        lines.forEach { line ->
            //Turns an 'f' line into [[int, int, int], [int, int, int], [int, int, int]]
            val lineIndices: List<List<Int>> =
                    line.split(" ")
                        .filter { !it.startsWith("f") }
                        .map { it.split("/").map { it.toInt() - 1 } }
            //Process vertices one by one
            processVertex(lineIndices[0])
            calculateTangent(
                    Pair(vertices[lineIndices[0][0]], texCoords[lineIndices[0][1]]),
                    Pair(vertices[lineIndices[1][0]], texCoords[lineIndices[1][1]]),
                    Pair(vertices[lineIndices[2][0]], texCoords[lineIndices[2][1]])
            )
            processVertex(lineIndices[1])
            calculateTangent(
                    Pair(vertices[lineIndices[1][0]], texCoords[lineIndices[1][1]]),
                    Pair(vertices[lineIndices[2][0]], texCoords[lineIndices[2][1]]),
                    Pair(vertices[lineIndices[0][0]], texCoords[lineIndices[0][1]])
            )
            processVertex(lineIndices[2])
            calculateTangent(
                    Pair(vertices[lineIndices[2][0]], texCoords[lineIndices[2][1]]),
                    Pair(vertices[lineIndices[1][0]], texCoords[lineIndices[1][1]]),
                    Pair(vertices[lineIndices[0][0]], texCoords[lineIndices[0][1]])
            )
        }
        
        return createVAO(ordVertices, ordNormals, ordTexCoords, ordTangents)
        
    }
    
    private fun createVAO(
            ordVertices: ArrayList<Vector3f>,
            ordNormals: ArrayList<Vector3f>,
            ordTexCoords: ArrayList<Vector2f>,
            ordTangents: ArrayList<Vector3f>) : VertexArrayObject {
        
        val verticesBuffer = MemoryUtil.memAllocFloat(ordVertices.size * 3)
        val normalsBuffer = MemoryUtil.memAllocFloat(ordNormals.size * 3)
        val texCoordsBuffer = MemoryUtil.memAllocFloat(ordTexCoords.size * 2)
        val tangentsBuffer = MemoryUtil.memAllocFloat(ordTangents.size * 3)
        
        ordVertices.forEach { verticesBuffer.put(it.x); verticesBuffer.put(it.y); verticesBuffer.put(it.z);  }
        ordNormals.forEach { normalsBuffer.put(it.x); normalsBuffer.put(it.y); normalsBuffer.put(it.z);  }
        ordTexCoords.forEach { texCoordsBuffer.put(it.x); texCoordsBuffer.put(it.y); }
        ordTangents.forEach { tangentsBuffer.put(it.x); tangentsBuffer.put(it.y); tangentsBuffer.put(it.z); }
        
        verticesBuffer.flip()
        normalsBuffer.flip()
        texCoordsBuffer.flip()
        tangentsBuffer.flip()
        
        val verticesAttribute = VertexAttribute(verticesBuffer, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 3)
        val normalsAttribute = VertexAttribute(normalsBuffer, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 3)
        val texCoordsAttribute = VertexAttribute(texCoordsBuffer, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 2)
        val tangentsAttribute = VertexAttribute(tangentsBuffer, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 3)
        
        MemoryUtil.memFree(verticesBuffer)
        MemoryUtil.memFree(normalsBuffer)
        MemoryUtil.memFree(texCoordsBuffer)
        
        return VertexArrayObject(hashMapOf(
                Pair(VertexAttribute.ATTRIB_POSITION, verticesAttribute),
                Pair(VertexAttribute.ATTRIB_NORMAL, normalsAttribute),
                Pair(VertexAttribute.ATTRIB_TEXTURE_COORD, texCoordsAttribute),
                Pair(VertexAttribute.ATTRIB_TANGENTS, tangentsAttribute)
        ))
    }
    
    private val planeVertices = floatArrayOf(
            -1f, 1f, 0.0f,
            -1f, -1f, 0.0f,
            1f, -1f, 0.0f,
            -1f, 1f, 0.0f,
            1f, -1f, 0.0f,
            1f, 1f, 0.0f
    )

    private val planeNormals = floatArrayOf(
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f
    )

    private val texCoords = floatArrayOf(
            0f, 1f,
            0f, 0f,
            1f, 0f,
            0f, 1f,
            1f, 0f,
            1f, 1f
    )
    
    private val tangents = floatArrayOf(
            1f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f
    )
    
}