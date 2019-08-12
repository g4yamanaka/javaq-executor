package com.javaq.controller

import com.javaq.domain.form.ExecutedSourceOutputForm
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

@RestController
class RunApiController {
    @GetMapping("/api/run")
    fun run(): ExecutedSourceOutputForm {
        val language = "Java"
        val sourceCode = """
            class Main {
                public static void main(String[] args) {
                    System.out.println("Hello World");
                }
            }
        """.trimIndent()
/*
        val sourceCode = """
            print("Hello World")
        """.trimIndent()
*/

        val filenameAndExecCommand = when(language) {
            "ruby" -> Pair("Main.rb", "ruby Main.rb")
            "python" -> Pair("Main.py", "python3 Main.py")
            "c" -> Pair("Main.c", "cc -Wall -o Main Main.c && ./Main")
            "Java" -> Pair("Main.java", "javac Main.java && java Main -Xmx512m -Xms512m")
            else -> Pair("", "")
        }

        val (filename, execCommand) = filenameAndExecCommand

        val dockerCommand = listOf(
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
                "/bin/bash", "-c", execCommand
        )

        println("Running: ${dockerCommand.joinToString(" ")}")
        val containerId = dockerCommand.runCommand()?.substring(0, 12)
        containerId ?: throw IOException("Couldn't create container")
        println(containerId)

        val mkdirCommand = listOf(
                "sudo", "rm", "-rf", "/tmp/workspace", "&&",
                "sudo", "mkdir", "-p", "/tmp/workspace", "&&",
                "sudo", "chmod", "777", "/tmp/workspace"
        )

        println("Running: $mkdirCommand")
        val mkdirOutput = mkdirCommand.runCommand()
        println(mkdirOutput)

        val execSourceFile = File("/tmp/workspace/$filename")
        execSourceFile.createNewFile()
        execSourceFile.writeText(sourceCode)

        val copyFileToDockerCommand = listOf(
                "docker", "cp", "/tmp/workspace", "$containerId:/"
        )
        val copyFileToDockerOutput = copyFileToDockerCommand.runCommand()
        println("Running: $copyFileToDockerCommand")
        println(copyFileToDockerOutput)


        val dockerStartCommand = listOf(
                "docker", "start", "-i", "$containerId"
        )
        println("Running: $dockerStartCommand")
        val dockerStartOutput = dockerStartCommand.runCommand()
        println(dockerStartOutput)


        val getTimeCommand = listOf(
                "docker", "cp", "$containerId:/time.txt", "/tmp/time.txt"
        )
        println("Running: $getTimeCommand")
        val getTimeOutput = getTimeCommand.runCommand()
        println(getTimeOutput)

        val timeFile = File("/tmp/time.txt").absoluteFile
        val time = timeFile.useLines {
            it.toList().joinToString("\n")
        }


        val removeDockerCommand = listOf(
                "docker", "rm", "$containerId"
        )
        println("Running: $removeDockerCommand")
/*
        removeDockerCommand.runCommand()
*/

        return ExecutedSourceOutputForm(dockerStartOutput?: "", time)
    }


    fun List<String>.runCommand(timeoutSeconds: Long = 3): String? {
        return try {
            val process = ProcessBuilder(*this.toTypedArray())
                    .redirectInput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()

            process.waitFor(timeoutSeconds, TimeUnit.SECONDS)
            process.inputStream.bufferedReader().readText()
        } catch(e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
