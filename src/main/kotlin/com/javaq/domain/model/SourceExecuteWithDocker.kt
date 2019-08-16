package com.javaq.domain.model

import java.io.File
import java.io.IOException

class SourceExecuteWithDocker(override val programLanguage: ProgramLanguage,
                              override val sourceCode: Source
) : SourceExecutor {
    private lateinit var containerId: String

    override fun execute(): ExecuteResult {
        createDocker()
        mkdir()
        writeSourceToFile()
        copyFileToDocker()
        val executeResult = start()
        copyTimeFromDocker()
        removeDocker()
        return executeResult
    }

    private fun createDocker() {
        val (fullContainerId,_) = listOf(
                "docker", "create", "-i",
                "--net", "none",
                "--cpuset-cpus", "0",
                "--memory", "512m",
                "--memory-swap", "512m",
                "--ulimit", "fsize=1000000",
                "-w", "/workspace",
                "ubuntu-dev",
                "/usr/bin/time", "-q", "-f", "\"%e\"", "-o", "/time.txt",
                "timeout", "3",
                "/bin/bash", "-c", programLanguage.executeCommand
        ).runCommand()
        containerId = fullContainerId.value.substring(0, 12)
    }

    private fun mkdir() {
        listOf(
                "rm", "-rf", "/tmp/workspace"
        ).runCommand()

        listOf(
                "mkdir", "-p", "/tmp/workspace"
        ).runCommand()

        listOf(
                "chmod", "777", "/tmp/workspace"
        ).runCommand()
    }

    private fun writeSourceToFile() {
        val file = File("/tmp/workspace/${programLanguage.fileName}")
        file.createNewFile()
        file.writeText(sourceCode.value)
    }

    private fun copyFileToDocker() {
        listOf(
                "docker", "cp", "/tmp/workspace", "$containerId:/"
        ).runCommand()
    }

    private fun start(): ExecuteResult {
        val result = listOf(
                "docker", "start", "-i", containerId
        ).runCommand()

        return result
    }

    private fun copyTimeFromDocker() {
        listOf(
                "docker", "cp", "$containerId:/time.txt", "/tmp/time.txt"
        ).runCommand()
    }

    private fun getTime() {
        try {
            val timeFile = File("/tmp/time.txt").absoluteFile
            return timeFile.useLines {
                it.toList().joinToString { "\n" }
            }

        } catch (e: IOException) {
            e.stackTrace
        }
    }

    private fun removeDocker() {
        listOf(
                "docker", "rm", containerId
        ).runCommand()
    }
}
