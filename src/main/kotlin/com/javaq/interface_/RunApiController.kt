package com.javaq.interface_

import com.javaq.domain.model.*
import com.javaq.infrastracture.SourceExecuteWithDocker
import org.springframework.web.bind.annotation.*

@RestController
class RunApiController {

    @CrossOrigin
    @PostMapping("/api/run")
    fun run(@RequestBody filesDto: FilesDto): ExecuteResult {
        val files = filesDto.toDomain()
        val programLanguage =  files.list.first().programLanguage
        val source = files.list.first().source

        val executor: SourceExecutor = SourceExecuteWithDocker(programLanguage, Source(source))
        val result = executor.execute()
        return result
    }
}
