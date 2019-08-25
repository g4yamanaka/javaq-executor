package com.javaq.application

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.javaq.adaptor.ExecuteTarget
import com.javaq.domain.model.*
import com.javaq.domain.model.programLanguage.ProgramLanguageFactory
import com.javaq.infrastracture.SourceExecuteWithDocker
import org.springframework.web.bind.annotation.*

@RestController
class RunApiController {
    @CrossOrigin
    @PostMapping("/api/run")
    fun run(@RequestBody request: String): ExecuteResult {
        val objectMapper = jacksonObjectMapper()
        val target = objectMapper.readValue<ExecuteTarget>(request)
        val sourceCode = target.files?.first()?.sourceCode
        val inputProgramLanguage = target.files?.first()?.programLanguage

        sourceCode ?: return ExecuteResult("", "Illegal source code")
        inputProgramLanguage ?: return  ExecuteResult("", "Illegal language")

        val programLanguage = ProgramLanguageFactory.create(inputProgramLanguage)
        val executor: SourceExecutor = SourceExecuteWithDocker(programLanguage, Source(sourceCode))
        val result = executor.execute()
        return result
    }
}