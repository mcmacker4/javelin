package com.mcmacker4.javelin.util

import com.mcmacker4.javelin.gl.shader.ShaderProgram
import java.io.FileNotFoundException
import java.util.stream.Collectors


object Resources {
    
    fun loadShader(name: String) : ShaderProgram {
        val vertex = loadTextFile("shaders/$name.v.glsl")
        val fragment = loadTextFile("shaders/$name.f.glsl")
        return ShaderProgram(vertex, fragment)
    }
    
    fun loadModel(name: String) {
        
    }
    
    fun loadTextFile(path: String) : String {
        val stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(path)
            ?: throw FileNotFoundException(path)
        return stream.bufferedReader()
                .lines()
                .collect(Collectors.joining("\n"))
    }
    
}