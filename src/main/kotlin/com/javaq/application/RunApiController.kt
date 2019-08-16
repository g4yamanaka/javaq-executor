package com.javaq.application

import com.javaq.adaptor.ExecuteTarget
import com.javaq.domain.model.*
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RunApiController {
    @PostMapping("/api/run")
    fun run(@ModelAttribute target: ExecuteTarget): ExecuteResult {
        val sourceCode = target.files.first().sourceCode
        val inputProgramLanguage = target.files.first().programLanguage

        val programLanguage = ProgramLanguageFactory.create(inputProgramLanguage)
        val executor: SourceExecutor = SourceExecuteWithDocker(programLanguage, Source(sourceCode))
        val result = executor.execute()
        return result
    }
}
