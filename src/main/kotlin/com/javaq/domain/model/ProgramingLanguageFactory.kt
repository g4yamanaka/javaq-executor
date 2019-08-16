package com.javaq.domain.model

object ProgramLanguageFactory {
    fun create(name: String): ProgramLanguage {
        return when(name.toLowerCase()) {
            "java" -> Java()
            else -> throw IllegalArgumentException("It is unsupported language $name.")
        }
    }
}
