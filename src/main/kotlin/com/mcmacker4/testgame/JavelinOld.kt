package com.mcmacker4.testgame

import com.mcmacker4.javelin.display.Display
import com.mcmacker4.javelin.gl.shader.ShaderProgram
import com.mcmacker4.javelin.graphics.Camera
import com.mcmacker4.javelin.input.Keyboard
import com.mcmacker4.javelin.model.Model
import com.mcmacker4.javelin.model.ModelLoader
import com.mcmacker4.javelin.graphics.RendererOld
import com.mcmacker4.javelin.model.Entity
import com.mcmacker4.javelin.util.Resources
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL11.*

@Deprecated("Old")
class JavelinOld {
    
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
    private val camera: Camera
    private val rendererOld: RendererOld
    private val shaderProgram: ShaderProgram
    
    init {

        GLFWErrorCallback.createPrint(System.err).set()

        if(!glfwInit())
            throw RuntimeException("Could not initialize GLFW.")

        display = Display(1280, 720, "Javelin")
        
        //Create Model
        model = ModelLoader.load(vertices)
        
        //Create Shader
        shaderProgram = Resources.loadShader("shader")
        
        //Create Camera
        camera = Camera(
                Vector3f(0.0f, 0.0f, 1.0f),
                Vector3f(0.0f, 0.0f, 0.0f),
                Math.PI * 0.5,
                display.width / display.height.toFloat()
        )
        
        display.onResize { width, height -> camera.aspect = width / height.toFloat() }
        
        Keyboard.onKeyDown { key, _ ->  
            if(key == GLFW_KEY_ESCAPE) stop()
        }
        
        //Renderer
        rendererOld = RendererOld(shaderProgram, camera)
        
        glEnable(GL_DEPTH_TEST)

        //Create Entities
        for(i in 0 until 100) {
            val entity = Entity(model)
//            entity.setPosition(
//                    (Math.random().toFloat() - 0.5f) * 10,
//                    (Math.random().toFloat() - 0.5f) * 10,
//                    (Math.random().toFloat() - 0.5f) * 10
//            )
//            entity.setRotation(
//                    (Math.random() * Math.PI).toFloat(),
//                    (Math.random()* Math.PI).toFloat(),
//                    (Math.random()* Math.PI).toFloat()
//            )
//            entity.setScale(
//                    Math.random().toFloat() + 0.5f,
//                    Math.random().toFloat() + 0.5f,
//                    Math.random().toFloat() + 0.5f
//            )
            rendererOld.addEntity(entity)
        }
        
    }
    
    fun start() {
        
        var lastTime = glfwGetTime()
        var lastFPS = glfwGetTime()
        var frameCount = 0
        
        while(!display.willClose()) {
            
            val now = glfwGetTime()
            val delta = (now - lastTime).toFloat()
            lastTime = now
            
            frameCount++
            if(now - lastFPS > 1) {
                println("FPS: $frameCount")
                frameCount = 0
                lastFPS = now
            }
            
            glClear(GL_COLOR_BUFFER_BIT.or(GL_DEPTH_BUFFER_BIT))
            
            camera.update(delta)
            
            rendererOld.draw()
            
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