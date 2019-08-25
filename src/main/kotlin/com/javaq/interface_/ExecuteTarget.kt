package com.javaq.interface_


data class ExecuteTarget(
        var files: List<File>? = null

){
    class File(
            var sourceCode: String? = null,
            var programLanguage: String? = null,
            var fileName: String? = null
    )
}
