package com.javaq.domain.model

import java.lang.Exception
import java.util.concurrent.TimeUnit

interface SourceExecutor {
    val programLanguage: ProgramLanguage
    val sourceCode: Source

    fun execute(): ExecuteResult

    companion object fun List<String>.runCommand(timeoutSeconds: Long = 3): ExecuteResult {
        return try {
            val process = ProcessBuilder(*this.toTypedArray()).start()
            process.waitFor(timeoutSeconds, TimeUnit.SECONDS)
            val stdOut = process.inputStream.bufferedReader().readText()
            val stdErr = process.errorStream.bufferedReader().readText()
            ExecuteResult(StdOut(stdOut), StdErr(stdErr))
        } catch (e: Exception) {
            ExecuteResult(StdOut(""), StdErr(""))
        }
    }
}
