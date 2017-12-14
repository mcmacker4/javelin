package com.mcmacker4.javelin.model

import com.mcmacker4.javelin.gameobject.GameObject


@Deprecated("Use a component based GameObject instead.")
class Entity(val model: Model) : GameObject()