package com.mcmacker4.javelin.gl


abstract class GLObject {
    
    abstract val id: Int
    
    abstract fun bind()
    abstract fun unbind()
    abstract fun delete()

}