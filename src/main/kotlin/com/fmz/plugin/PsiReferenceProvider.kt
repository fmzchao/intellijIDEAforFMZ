package com.fmz.plugin

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
    override fun resolve(): PsiElement? {
        // 这里添加你的解析逻辑,比如在项目中搜索函数定义
        return myElement // 示例返回当前元素
    }

    override fun getVariants(): Array<Any> {
        // 返回代码完成提示,包含多个函数名
        return emptyArray()
    }
}