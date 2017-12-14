package com.mcmacker4.testgame

import com.mcmacker4.javelin.display.Display
import com.mcmacker4.javelin.gameobject.GameObject
import com.mcmacker4.javelin.gameobject.World
import com.mcmacker4.javelin.gameobject.component.Camera
import com.mcmacker4.javelin.gameobject.component.Mesh
import com.mcmacker4.javelin.input.Keyboard
import com.mcmacker4.javelin.model.ModelLoader
import com.mcmacker4.javelin.graphics.Renderer
import com.mcmacker4.javelin.util.Resources
import com.mcmacker4.javelin.util.Timer
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL11.*


class Javelin {

    private val display: Display
    private val world: World

    private val vertices = floatArrayOf(
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f
    )

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
        val renderer = Renderer(shaderProgram)
        
        //Create World
        world = World(renderer)
        
        //Create model GameObject
        val square = GameObject(Mesh(ModelLoader.loadMesh(vertices)))
        world.addGameObject(square)
        
        //Create camera GameObject
        val camera = GameObject(Camera(), CameraControl())
        camera.position.set(0f, 0f, 1f)
        world.setActiveCamera(camera)

    }

    fun start() {
        
        val timer = Timer()

        //Game Loop
        while(!display.willClose()) {
            
            val delta = timer.update()

            glClear(GL_COLOR_BUFFER_BIT.or(GL_DEPTH_BUFFER_BIT))
            
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

        display.destroy()

        glfwTerminate()
        glfwSetErrorCallback(null).free()

    }

}