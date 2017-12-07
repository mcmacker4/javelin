package com.mcmacker4.javelin

import com.mcmacker4.javelin.display.Display
import com.mcmacker4.javelin.gl.shader.ShaderProgram
import com.mcmacker4.javelin.model.Model
import com.mcmacker4.javelin.model.ModelLoader
import com.mcmacker4.javelin.model.Renderer
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL11.*


class Javelin {
    
    private val display: Display
    
    private val vertices = floatArrayOf(
        -0.5f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f,
        -0.5f, 0.5f, 0.0f,
        0.5f, -0.5f, 0.0f,
        0.5f, 0.5f, 0.0f
    )
    
    private val model: Model
    private val renderer: Renderer
    private val shaderProgram: ShaderProgram
    
    init {

        GLFWErrorCallback.createPrint(System.err).set()

        if(!glfwInit())
            throw RuntimeException("Could not initialize GLFW.")

        display = Display(1280, 720, "Javelin")
        
        //Create Model
        model = ModelLoader.load(vertices)
        
        //Create Shader
        shaderProgram = ShaderProgram.load("shader")
        
        //Renderer
        renderer = Renderer(shaderProgram)
        
    }
    
    fun start() {
        
        while(!display.willClose()) {
            
            glClear(GL_COLOR_BUFFER_BIT.or(GL_DEPTH_BUFFER_BIT))
            
            renderer.draw(model)
            
            display.update()
            glfwPollEvents()
            
        }
        
        cleanUp()
        
    }
    
    fun stop() {
        display.close()
    }
    
    private fun cleanUp() {
        
        model.delete()
        shaderProgram.delete()
        
        display.destroy()

        glfwTerminate()
        glfwSetErrorCallback(null).free()
        
    }
    
}