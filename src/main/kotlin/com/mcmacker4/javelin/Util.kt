package com.mcmacker4.javelin

import java.util.stream.Collectors


fun readFile(name: String) : String {
    val inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(name)
            ?: throw Exception("File not found $name")
    return inputStream.bufferedReader().lines().collect(Collectors.joining("\n"))
}