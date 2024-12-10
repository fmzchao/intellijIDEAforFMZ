package com.fmz.plugin

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext

class MyReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        // 注册自定义的 PsiReferenceProvider
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(), MyFunctionReferenceProvider())
    }
}

class MyFunctionReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        // 返回自定义的 PsiReference 数组,根据元素内容决定解析逻辑
        val text = element.text
        if (text.matches(Regex("exchanges\\[\\w+]\\.\\w+"))) {
            val functionName = text.substringAfter("exchanges[").substringBefore("].")
            val exchangeVariable = "exchange"
            val newExpression = text.replace("exchanges[$functionName].", "$exchangeVariable.")

            if (CompletionUtil.getCompletions().contains(newExpression)) {
                return arrayOf(MyFunctionReference(element, newExpression))
            }
        }
        if (CompletionUtil.getCompletions().contains(text)) {
            return arrayOf(MyFunctionReference(element, text))
        }
        return PsiReference.EMPTY_ARRAY
    }
}

internal class MyFunctionReference(element: PsiElement?, private val functionName: String) :
    PsiReferenceBase<PsiElement?>(element!!) {

    override fun getRangeInElement(): TextRange {
        // 如果你需要改变引用范围，可以在这里实现
        return TextRange(0, element.textLength)
    }

    override fun resolve(): PsiElement? {
        // 解析逻辑，返回正确的元素
        return element
    }

    override fun getVariants(): Array<Any> {
        // 返回代码补全建议
        return CompletionUtil.getCompletions().toTypedArray()
    }
}
