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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.Disposable

@Service(Service.Level.PROJECT)
class FileSyncService(private val project: Project) : Disposable {
    private val logger = Logger.getInstance(FileSyncService::class.java)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun syncFile(fileName: String, content: String, token: String) {
        scope.launch {
            try {
                logger.info("Starting sync for file: $fileName")
                val host = if (token.startsWith('n')) "www.youquant.com" else "www.fmz.com"
                val uri = URI("https", host, "/rsync", null)
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

                (url.openConnection() as HttpURLConnection).apply {
                    doOutput = true
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                    setRequestProperty("Content-Length", postData.length.toString())
                    outputStream.use { outStream ->
                        outStream.write(postData.toByteArray(Charsets.UTF_8))
                    }
                    try {
                        val response = inputStream.bufferedReader().use { it.readText() }
                        if (response.contains("\"code\":0")) {
                            showNotification("FMZ Sync", "File [$fileName] synced successfully", NotificationType.INFORMATION)
                            logger.info("File [$fileName] synced successfully")
                        } else {
                            showNotification("FMZ Sync Error", "Error syncing file [$fileName]: $response", NotificationType.ERROR)
                            logger.warn("Error syncing file [$fileName]: $response")
                        }
                    } catch (e: Exception) {
                        showNotification("FMZ Sync Error", "Exception occurred: ${e.message}", NotificationType.ERROR)
                        logger.error("Exception occurred while syncing file [$fileName]", e)
                    } finally {
                        disconnect()
                    }
                }
            } catch (e: Exception) {
                logger.error("Unexpected error during file sync for [$fileName]", e)
                showNotification("FMZ Sync Error", "Unexpected error: ${e.message}", NotificationType.ERROR)
            }
        }
    }

    private fun showNotification(title: String, content: String, type: NotificationType) {
        if (project.isDisposed) {
            logger.warn("Project is disposed. Cannot show notification: $title")
            return
        }

        val notification = Notification("FMZSync", title, content, type)
        project.messageBus.syncPublisher(Notifications.TOPIC).notify(notification)
    }

    override fun dispose() {
        scope.cancel()
        logger.info("FileSyncService disposed")
    }
}

class FileSyncListener : ProjectActivity {
    private val logger = Logger.getInstance(FileSyncListener::class.java)

    override suspend fun execute(project: Project) {
        val connection: MessageBusConnection = project.messageBus.connect()
        connection.subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
            override fun before(events: List<VFileEvent>) {}

            override fun after(events: List<VFileEvent>) {
                val fileDocumentManager = FileDocumentManager.getInstance()
                events.forEach { event ->
                    try {
                        val virtualFile = event.file ?: return@forEach
                        val fileName = virtualFile.name
                        logger.info("Detected change in file: $fileName")
                        if (fileName.endsWith(".js") || fileName.endsWith(".py")) {
                            val document = fileDocumentManager.getDocument(virtualFile) ?: return@forEach
                            val content = document.text
                            val filteredContent = filterContent(content)
                            val token = extractToken(content) ?: return@forEach
                            logger.info("Syncing file: $fileName with token")
                            val service = project.getService(FileSyncService::class.java)
                            service.syncFile(fileName, filteredContent, token)
                        }
                    } catch (e: Exception) {
                        logger.error("Error processing file event", e)
                    }
                }
            }

            private fun filterContent(content: String): String {
                logger.info("Filtering content")
                return content.lines().filterNot { it.contains("fmz@") }.joinToString("\n")
            }

            private fun extractToken(content: String): String? {
                logger.info("Extracting token")
                val regex = "fmz@([a-zA-Z0-9]{32})".toRegex()
                return regex.find(content)?.groups?.get(1)?.value
            }
        })
    }
}