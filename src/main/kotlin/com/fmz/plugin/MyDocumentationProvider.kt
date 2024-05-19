package com.fmz.plugin

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

class MyDocumentationProvider : AbstractDocumentationProvider() {

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        if (element != null) {
            val parent = PsiTreeUtil.getParentOfType(element, PsiElement::class.java)
            if (parent != null) {
                val elementText = parent.text
                return when {
                    elementText.startsWith("_G") -> generateGlobalPersistentDictionaryDoc()
                    elementText.startsWith("_D") -> generateDateFormatDoc()
                    elementText.startsWith("HttpQuery") -> generateHttpQueryDoc()
                    else -> null
                }
            }
        }
        return null
    }

    private fun generateGlobalPersistentDictionaryDoc(): String {
        return ("<b>_G</b><br>"
                + "持久化保存数据,该函数实现了一个可保存的全局字典功能。<br>"
                + "数据结构为KV表,永久保存在托管者本地数据库文件。<br>"
                + "<br>"
                + "示例用法:<br>"
                + "<code>_G.key = value;</code><br>"
                + "<code>var value = _G.key;</code>")
    }

    private fun generateDateFormatDoc(): String {
        return ("<b>_D(timestamp)</b><br>"
                + "将毫秒时间戳或者 <code>Date</code> 对象转换为时间字符串。<br>"
                + "<br>"
                + "@param timestamp 毫秒时间戳或 <code>Date</code> 对象<br>"
                + "@return 格式化后的时间字符串<br>"
                + "<br>"
                + "示例用法:<br>"
                + "<code>var dateString = _D(Date.now());</code><br>"
                + "<code>var dateString = _D(new Date());</code>")
    }

    private fun generateHttpQueryDoc(): String {
        return ("<b>HttpQuery(url)</b><br>"
                + "<b>HttpQuery(url, options)</b><br>"
                + "发送 HTTP 请求。<br>"
                + "<br>"
                + "@param url 请求的 URL<br>"
                + "@param options 可选的请求选项对象<br>"
                + "@return 请求的响应结果<br>"
                + "<br>"
                + "示例用法:<br>"
                + "<code>var response = HttpQuery('https://example.com');</code><br>"
                + "<code>var response = HttpQuery('https://example.com', { method: 'POST', data: { key: 'value' } });</code>")
    }
}