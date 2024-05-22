package com.fmz.plugin

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiFile
import com.intellij.util.ProcessingContext


class MyCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    resultSet: CompletionResultSet
                ) {
                    val file = parameters.originalFile
                    if (shouldProvideCompletion(file)) {
                        val completions = CompletionUtil.getCompletions()
                        completions.forEach { completion ->
                            resultSet.addElement(LookupElementBuilder.create(completion))
                        }
                    }
                }
            }
        )
    }

    private fun shouldProvideCompletion(file: PsiFile): Boolean {
        val fileName = file.name
        return fileName.endsWith(".js") || fileName.endsWith(".py")
    }

}
