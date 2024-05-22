package com.fmz.plugin

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.util.messages.MessageBusConnection
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.thread
import java.io.BufferedReader

@Service
class FileSyncService(val project: Project) {
    fun syncFile(fileName: String, content: String, token: String) {
        thread {

            try {
                // 使用 URI 构造器，然后转换为 URL
                val uri = URI("https", "www.fmz.${if (token.startsWith('n')) "cn" else "com"}", "/rsync", null)
                val url = uri.toURL()

                val params = mapOf(
                    "token" to token,
                    "method" to "push",
                    "content" to content,
                    "version" to "0.0.2",
                    "client" to "idea"
                )
                val postData = params.map {
                    "${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}"
                }.joinToString("&")

                val conn = url.openConnection() as HttpURLConnection
                conn.doOutput = true
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.setRequestProperty("Content-Length", postData.length.toString())

                conn.outputStream.use { outStream ->
                    outStream.write(postData.toByteArray(Charsets.UTF_8))
                    outStream.flush()  // 确保数据完全写入
                }

                conn.connect()
                val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                if (response.contains("\"code\":0")) {
                    showNotification("FMZ Sync", "File [$fileName] synced successfully", NotificationType.INFORMATION)
                } else {
                    showNotification("FMZ Sync Error", "Error syncing file [$fileName] fail: $response", NotificationType.ERROR, 10000)
                }
            } catch (e: Exception) {
                showNotification("FMZ Sync Error", "Exception occurred: ${e.message}", NotificationType.ERROR)
            } catch (e: Exception) {
                showNotification(
                    "FMZ Sync Error",
                    "Error syncing file [$fileName]: ${e.message}",
                    NotificationType.ERROR,
                    10000
                )
            }
        }
    }

    private fun showNotification(title: String, content: String, type: NotificationType, duration: Long = 1500) {
        val notification = Notification("FMZSync", title, content, type)
        Notifications.Bus.notify(notification)

        // Close the notification after the specified duration
        Timer().schedule(object : TimerTask() {
            override fun run() {
                notification.expire()
            }
        }, duration)
    }
}

class FileSyncListener : ProjectActivity {
    override suspend fun execute(project: Project) {
        val connection: MessageBusConnection = project.messageBus.connect()
        connection.subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
            override fun before(events: List<VFileEvent>) {}

            override fun after(events: List<VFileEvent>) {
                val fileDocumentManager = FileDocumentManager.getInstance()
                events.forEach { event ->
                    val virtualFile = event.file ?: return@forEach
                    val fileName = virtualFile.name
                    if (fileName.endsWith(".js") || fileName.endsWith(".py")) {
                        val document = fileDocumentManager.getDocument(virtualFile) ?: return@forEach
                        val content = document.text
                        val filteredContent = filterContent(content)
                        val token = extractToken(content) ?: return@forEach
                        val service = project.getService(FileSyncService::class.java)
                        service.syncFile(fileName, filteredContent, token)
                    }
                }
            }

            private fun filterContent(content: String): String {
                return content.lines().filterNot { it.contains("fmz@") }.joinToString("\n")
            }

            private fun extractToken(content: String): String? {
                val regex = "fmz@([a-zA-Z0-9]{32})".toRegex()
                return regex.find(content)?.groups?.get(1)?.value
            }
        })
    }
}
