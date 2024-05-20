package com.fmz.plugin


import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.lang.javascript.TypeScriptFileType
import java.io.InputStreamReader

class TypeScriptDefinitionProvider : StartupActivity {

    override fun runActivity(project: Project) {
        val psiManager = PsiManager.getInstance(project)
        val psiFileFactory = PsiFileFactory.getInstance(project)
        val application = ApplicationManager.getApplication()

        val resourcePath = "/typings/lib.d.ts"
        val inputStream = javaClass.getResourceAsStream(resourcePath)

        if (inputStream != null) {
            val content = InputStreamReader(inputStream).readText()

            application.runWriteAction {
                // 在项目的根目录下创建一个虚拟文件夹和文件
                val projectBaseDir = project.baseDir
                val customTypesDir = projectBaseDir.findChild("customTypes") ?: projectBaseDir.createChildDirectory(this, "customTypes")
                val virtualFile = customTypesDir.findChild("lib.d.ts") ?: customTypesDir.createChildData(this, "lib.d.ts")

                // 写入内容到虚拟文件
                virtualFile.setBinaryContent(content.toByteArray())

                // 从虚拟文件创建 PSI 文件
                val psiFile = psiFileFactory.createFileFromText("lib.d.ts", TypeScriptFileType.INSTANCE, content)

                // 检查文件是否已存在
                val existingFile = psiManager.findFile(virtualFile)
                if (existingFile == null) {
                    psiManager.findDirectory(customTypesDir)?.add(psiFile)
                }
            }
        } else {
            println("Resource $resourcePath not found")
        }
    }
}