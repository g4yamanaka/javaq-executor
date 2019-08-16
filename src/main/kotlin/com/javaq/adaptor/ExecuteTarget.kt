package com.javaq.adaptor


data class ExecuteTarget(
        var files: List<File>

){
    class File(
            var sourceCode: String,
            var programLanguage: String,
            var fileName: String
    )
}
