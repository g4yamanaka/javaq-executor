package com.javaq.domain.model

data class Java(
        override val fileName: String = "Main.java",
        override val executeCommand: String = "javac Main.java && java Main -Xmx512m -Xms512m"
): ProgramLanguage
