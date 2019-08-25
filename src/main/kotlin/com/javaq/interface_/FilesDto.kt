package com.javaq.interface_

import com.javaq.domain.model.File
import com.javaq.domain.model.Files
import com.javaq.domain.model.programLanguage.ProgramLanguageFactory
import java.lang.IllegalArgumentException


data class FilesDto(
        val files: List<FileDto>? = null
) {
    fun toDomain(): Files {
        files ?: throw IllegalArgumentException("Do not empty file.")
        return Files(files.map { it.toDomain() })
    }
}

data class FileDto(
        val source: String? = null,
        val programLanguage: String? = null,
        val fileName: String? = null
) {
    fun toDomain(): File {
        source ?: throw IllegalArgumentException("Do not empty source code")
        programLanguage ?: throw IllegalArgumentException("Do not empty program language.")
        fileName ?: throw IllegalArgumentException("Do not empty file name.")

        return File(
                source,
                ProgramLanguageFactory.create(programLanguage),
                fileName
        )
    }
}
