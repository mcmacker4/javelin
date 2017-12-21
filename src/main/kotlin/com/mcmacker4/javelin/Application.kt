package com.mcmacker4.javelin

import com.mcmacker4.javelin.display.Display
import com.mcmacker4.javelin.gameobject.World
import com.mcmacker4.javelin.graphics.Renderer
import com.mcmacker4.javelin.input.Keyboard
import com.mcmacker4.javelin.util.Resources
import com.mcmacker4.javelin.util.Timer
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL11.*


abstract class Application {

    private val display: Display
    protected val world: World
    
    private lateinit var timer: Timer

    init {

        //Initialize GLFW        
        GLFWErrorCallback.createPrint(System.err).set()
        if(!glfwInit())
            throw RuntimeException("Could not initialize GLFW.")

        //Create display
        display = Display(1280, 720, "Javelin")
        
        //Close application on ESCAPE
        Keyboard.onKeyDown { key, _ -> if(key == GLFW_KEY_ESCAPE) stop() }
        
        //Create Shader
        val shaderProgram = Resources.loadShader("shader")
        
        //Create Renderer
        val renderer = Renderer()
        
        //Create World
        world = World(renderer)

    }

    fun start() {
        
        timer = Timer()
        
        //Game Loop
        while(!display.willClose()) {
            
            val delta = timer.update()

            world.update(delta)
            world.render()

            display.update()
            glfwPollEvents()
            
        }

        cleanUp()

    }

    fun stop() {
        display.close()
    }

    private fun cleanUp() {
        
        Resources.cleanUp()
        
        display.destroy()

        glfwTerminate()
        glfwSetErrorCallback(null).free()

    }

}