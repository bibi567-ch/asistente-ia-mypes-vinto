package com.mypes.vinto.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.mypes.vinto.BuildConfig
import com.mypes.vinto.network.AppUpdateClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.security.MessageDigest

class AppUpdateManager(private val context: Context) {
    private val httpClient = OkHttpClient()

    suspend fun checkAndInstall(): Boolean = withContext(Dispatchers.IO) {
        val info = try {
            AppUpdateClient.create(BuildConfig.API_BASE_URL).latest()
        } catch (_: Exception) {
            return@withContext false
        }
        if (info.versionCode <= BuildConfig.VERSION_CODE) return@withContext false

        val request = Request.Builder().url(info.url).get().build()
        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) return@withContext false
        val apk = File(context.cacheDir, "updates/yapay-${info.versionCode}.apk")
        apk.parentFile?.mkdirs()
        response.body?.byteStream()?.use { input ->
            apk.outputStream().use { output -> input.copyTo(output) }
        } ?: return@withContext false
        if (sha256(apk) != info.sha256) {
            apk.delete()
            return@withContext false
        }

        withContext(Dispatchers.Main) {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apk
            )
            context.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/vnd.android.package-archive")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            )
        }
        true
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var read = input.read(buffer)
            while (read >= 0) {
                if (read > 0) digest.update(buffer, 0, read)
                read = input.read(buffer)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
