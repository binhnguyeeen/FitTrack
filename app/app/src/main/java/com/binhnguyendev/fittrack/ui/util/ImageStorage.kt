package com.binhnguyendev.fittrack.ui.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * The system photo picker grants only temporary read access, so copy the
 * chosen image into app-internal storage and persist that stable path.
 */
object ImageStorage {
    suspend fun persist(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val dir = File(context.filesDir, "avatars").apply { mkdirs() }
            val dest = File(dir, "avatar_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri).use { input ->
                requireNotNull(input)
                dest.outputStream().use { output -> input.copyTo(output) }
            }
            // Drop older avatars to avoid unbounded growth.
            dir.listFiles()?.filter { it != dest }?.forEach { it.delete() }
            dest.absolutePath
        }.getOrNull()
    }
}
