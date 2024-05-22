package com.fmz.plugin

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*

class MyDocumentationProvider : AbstractDocumentationProvider() {
    private var docData: JSONObject? = null

    init {
        loadDocumentationData()
    }

    private fun loadDocumentationData() {
        val `is` = javaClass.getResourceAsStream("/META-INF/syntax_guide_en_US.json")
        val jsonString = `is`?.let { Scanner(it, StandardCharsets.UTF_8.name()).useDelimiter("\\A").next() }
        docData = JSONObject(jsonString)
    }

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        val parent = PsiTreeUtil.getParentOfType(element, PsiElement::class.java)

        if (parent != null) {
            val elementText = parent.text
            return generateDocumentation(elementText)
        }
        return null
    }

    private fun generateDocumentation(functionName: String): String? {
        val functions = docData?.getJSONObject("functions")?.getJSONObject("docs")
        val func = functions?.optJSONObject(functionName)  // 使用 optJSONObject 安全获取 JSONObject
        if (func != null) {
            val docBuilder = StringBuilder()
            // 处理描述
            docBuilder.append("<b>").append(functionName).append("()</b><br>")
            func.getJSONArray("desc")?.let { descArray ->
                descArray.forEach { item ->
                    docBuilder.append((item as JSONObject).getString("value")).append("<br><br>")
                }
            }

            // 处理参数，确保 args 存在
            func.optJSONArray("args")?.let { args ->
                docBuilder.append("<b>Parameters:</b><br>")
                args.forEach { arg ->
                    val argObj = arg as JSONObject
                    docBuilder.append(argObj.getString("name")).append(" - ")
                        .append(argObj.getJSONObject("info").getString("value")).append("<br>")
                }
                docBuilder.append("<br>")
            }

            // 处理返回值
            func.optJSONArray("returns")?.getJSONObject(0)?.getJSONObject("info")?.let {
                docBuilder.append("<b>Returns:</b> ").append(it.getString("value")).append("<br><br>")
            }

            // 处理示例
            docBuilder.append("<b>Example:</b><br>")
            func.optJSONArray("examples")?.forEach { example ->
                (example as JSONObject).optString("code")?.let {
                    docBuilder.append("<code>").append(it).append("</code><br><br>")
                }
            }

            return docBuilder.toString()
        }
        return null
    }

}