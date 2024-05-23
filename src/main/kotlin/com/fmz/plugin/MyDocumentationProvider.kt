package com.fmz.plugin

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId


class MyDocumentationProvider : AbstractDocumentationProvider() {
    private var docData: JSONObject? = null

    init {
        loadDocumentationData()
    }
    private fun isPluginActive(pluginId: String): Boolean {
        val plugin = PluginManagerCore.getPlugin(PluginId.getId(pluginId))
        return plugin != null && plugin.isEnabled
    }
    private fun loadDocumentationData() {
        var `is` = javaClass.getResourceAsStream("/META-INF/syntax_guide_en_US.json")
        //判断有没有激活com.intellij.zh插件,如果激活就读中文文档
        if (isPluginActive("com.intellij.zh")) {
            `is` = javaClass.getResourceAsStream("/META-INF/syntax_guide_zh_CN.json")
        }

        val jsonString = `is`?.let { Scanner(it, StandardCharsets.UTF_8.name()).useDelimiter("\\A").next() }
        docData = JSONObject(jsonString)
//        println("language = ${isPluginActive("com.intellij.zh")}")
    }

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        val parent = PsiTreeUtil.getParentOfType(element, PsiElement::class.java)

        if (parent != null) {
//            println("parent = $parent, parent.txt = ${parent.text} ,callExpression.firstChild = ${parent.firstChild.text}")
            val elementText = parent.firstChild.text
            if (elementText.matches(Regex("exchanges\\[\\d+]\\.\\w+"))) {
                val functionName = elementText.substringAfter("exchanges[").substringBefore("].")
                val exchangeVariable = "exchange"
                val newExpression = elementText.replace("exchanges[$functionName].", "$exchangeVariable.")
                return generateDocumentation(newExpression)
            }
            return generateDocumentation(elementText)
        }
        return null
    }
    private fun formatText(text: String): String {
        var formattedText = text.replace("\n", "<br>")

        // 将 Markdown 的代码块转换为 <code> 标签
        formattedText = formattedText.replace(Regex("```(.*?)```")) { matchResult ->
            val content = matchResult.groupValues[1]
            "<code>$content</code>"
        }

        // 将 Markdown 的粗体语法转换为 <b> 标签
        formattedText = formattedText.replace(Regex("\\*\\*(.*?)\\*\\*")) { matchResult ->
            val content = matchResult.groupValues[1]
            "<b>$content</b>"
        }

        // 将 Markdown 的斜体语法转换为 <i> 标签
        formattedText = formattedText.replace(Regex("\\*(.*?)\\*")) { matchResult ->
            val content = matchResult.groupValues[1]
            "<i>$content</i>"
        }

        return formattedText
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
                    docBuilder.append(formatText((item as JSONObject).getString("value"))).append("<br><br>")
                }
            }

            // 处理参数，确保 args 存在
            func.optJSONArray("args")?.let { args ->
                docBuilder.append("<b>Parameters:</b><br>")
                args.forEach { arg ->
                    val argObj = arg as JSONObject
                    docBuilder.append(argObj.getString("name")).append(" - ")
                        .append(formatText(argObj.getJSONObject("info").getString("value"))).append("<br>")
                }
                docBuilder.append("<br>")
            }

            // 处理返回值
            func.optJSONArray("returns")?.getJSONObject(0)?.getJSONObject("info")?.let {
                docBuilder.append("<b>Returns:</b> ").append(formatText(it.getString("value"))).append("<br><br>")
            }

            // 处理示例
            docBuilder.append("<b>Example:</b><br>")
            func.optJSONArray("examples")?.forEach { example ->
                (example as JSONObject).optString("code")?.let {
                    //要判断examples里面的lang不等行cpp,才会添加到文档中
                    if (example.optString("lang") != "cpp") {
                        docBuilder.append("<code>").append(formatText(it)).append("</code><br><br>")
                    }
                }
            }

            return docBuilder.toString()
        }
        return null
    }

}