package com.javaq.domain.model

import com.javaq.domain.model.programLanguage.ProgramLanguage

data class File(
        val source: String,
        val programLanguage: ProgramLanguage,
        val fileName: String
)
