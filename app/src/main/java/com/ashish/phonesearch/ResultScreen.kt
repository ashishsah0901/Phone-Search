package com.ashish.phonesearch

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ResultScreen(searchText: String) {
    var isSearching by remember {
        mutableStateOf(true)
    }
    var resultItems by remember {
        mutableStateOf<List<String>>(emptyList())
    }
    val context = LocalContext.current
    LaunchedEffect(key1 = searchText) {
        // Read Internal Storage
        withContext(Dispatchers.IO) {
            val internalStorageResult = loadFilesFromInternalStorage(context, searchText)
            withContext(Dispatchers.Main) {
                isSearching = false
                resultItems = listOf("Abc", "CDE")
            }
        }
    }
    Column {
        if (isSearching) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(resultItems) {
                    Text(text = it)
                }
            }
        }
    }
}

private suspend fun loadFilesFromInternalStorage(context: Context, searchText: String): List<String> {
    return withContext(Dispatchers.IO) {
        val files = context.filesDir.listFiles()
        files?.filter { it.canRead() && (it.name.contains(searchText) || it.readText().contains(searchText)) }?.map {
            it.absolutePath
        } ?: listOf()
    }
}

private suspend fun loadAudioFromExternalStorage(context: Context, searchText: String): List<String> {
    return withContext(Dispatchers.IO) {
        val collection = sdk29AndUp {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.Media._ID
        )
        val photos = mutableListOf<String>()
        context.contentResolver.query(
            collection,
            projection,
            MediaStore.Images.Media.DISPLAY_NAME + "=? or " +
                    MediaStore.Images.Media.ALBUM + "=? or " +
                    MediaStore.Images.Media.ALBUM_ARTIST + "=? or " +
                    MediaStore.Images.Media.ARTIST + "=? or " +
                    MediaStore.Images.Media.AUTHOR + "=? or " +
                    MediaStore.Images.Media.DESCRIPTION + "=? or " +
                    MediaStore.Images.Media.GENRE + "=? or " +
                    MediaStore.Images.Media.TITLE + "=? or " +
                    MediaStore.Images.Media.VOLUME_NAME + "=? or " +
                    MediaStore.Images.Media.RELATIVE_PATH + "=? or ",
            arrayOf(searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText),
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while(cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                contentUri.path?.let { photos.add(it) }
            }
            photos.toList()
        } ?: listOf()
    }
}

private suspend fun loadDownloadedFromExternalStorage(context: Context, searchText: String): List<String> {
    return withContext(Dispatchers.IO) {
        val collection = sdk29AndUp {
            MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.Media._ID
        )
        val photos = mutableListOf<String>()
        context.contentResolver.query(
            collection,
            projection,
            MediaStore.Images.Media.DISPLAY_NAME + "=? or " +
                    MediaStore.Images.Media.ALBUM + "=? or " +
                    MediaStore.Images.Media.ALBUM_ARTIST + "=? or " +
                    MediaStore.Images.Media.ARTIST + "=? or " +
                    MediaStore.Images.Media.AUTHOR + "=? or " +
                    MediaStore.Images.Media.DESCRIPTION + "=? or " +
                    MediaStore.Images.Media.GENRE + "=? or " +
                    MediaStore.Images.Media.TITLE + "=? or " +
                    MediaStore.Images.Media.VOLUME_NAME + "=? or " +
                    MediaStore.Images.Media.RELATIVE_PATH + "=? or ",
            arrayOf(searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText),
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while(cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                contentUri.path?.let { photos.add(it) }
            }
            photos.toList()
        } ?: listOf()
    }
}

private suspend fun loadFilesFromExternalStorage(context: Context, searchText: String): List<String> {
    return withContext(Dispatchers.IO) {
        val collection = sdk29AndUp {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.Media._ID
        )
        val photos = mutableListOf<String>()
        context.contentResolver.query(
            collection,
            projection,
            MediaStore.Images.Media.DISPLAY_NAME + "=? or " +
                    MediaStore.Images.Media.ALBUM + "=? or " +
                    MediaStore.Images.Media.ALBUM_ARTIST + "=? or " +
                    MediaStore.Images.Media.ARTIST + "=? or " +
                    MediaStore.Images.Media.AUTHOR + "=? or " +
                    MediaStore.Images.Media.DESCRIPTION + "=? or " +
                    MediaStore.Images.Media.GENRE + "=? or " +
                    MediaStore.Images.Media.TITLE + "=? or " +
                    MediaStore.Images.Media.VOLUME_NAME + "=? or " +
                    MediaStore.Images.Media.RELATIVE_PATH + "=? or ",
            arrayOf(searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText),
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while(cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                contentUri.path?.let { photos.add(it) }
            }
            photos.toList()
        } ?: listOf()
    }
}

private suspend fun loadVideoFromExternalStorage(context: Context, searchText: String): List<String> {
    return withContext(Dispatchers.IO) {
        val collection = sdk29AndUp {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Video.Media._ID
        )
        val photos = mutableListOf<String>()
        context.contentResolver.query(
            collection,
            projection,
            MediaStore.Video.Media.DISPLAY_NAME + "=? or " +
                    MediaStore.Video.Media.ALBUM + "=? or " +
                    MediaStore.Video.Media.ALBUM_ARTIST + "=? or " +
                    MediaStore.Video.Media.ARTIST + "=? or " +
                    MediaStore.Video.Media.AUTHOR + "=? or " +
                    MediaStore.Video.Media.DESCRIPTION + "=? or " +
                    MediaStore.Video.Media.GENRE + "=? or " +
                    MediaStore.Video.Media.TITLE + "=? or " +
                    MediaStore.Video.Media.VOLUME_NAME + "=? or " +
                    MediaStore.Video.Media.RELATIVE_PATH + "=? or ",
            arrayOf(searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText),
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while(cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                contentUri.path?.let { photos.add(it) }
            }
            photos.toList()
        } ?: listOf()
    }
}

private suspend fun loadPhotosFromExternalStorage(context: Context, searchText: String): List<String> {
    return withContext(Dispatchers.IO) {
        val collection = sdk29AndUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.Media._ID
        )
        val photos = mutableListOf<String>()
        context.contentResolver.query(
            collection,
            projection,
            MediaStore.Images.Media.DISPLAY_NAME + "=? or " +
                    MediaStore.Images.Media.ALBUM + "=? or " +
                    MediaStore.Images.Media.ALBUM_ARTIST + "=? or " +
                    MediaStore.Images.Media.ARTIST + "=? or " +
                    MediaStore.Images.Media.AUTHOR + "=? or " +
                    MediaStore.Images.Media.DESCRIPTION + "=? or " +
                    MediaStore.Images.Media.GENRE + "=? or " +
                    MediaStore.Images.Media.TITLE + "=? or " +
                    MediaStore.Images.Media.VOLUME_NAME + "=? or " +
                    MediaStore.Images.Media.RELATIVE_PATH + "=? or ",
            arrayOf(searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText),
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while(cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                contentUri.path?.let { photos.add(it) }
            }
            photos.toList()
        } ?: listOf()
    }
}

inline fun <T> sdk29AndUp(onSdk29: () -> T): T? {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        onSdk29()
    } else null
}