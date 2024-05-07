package com.ashish.phonesearch

import android.content.ContentUris
import android.content.Context
import android.provider.CallLog
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
        mutableStateOf<List<SearchResultItem>>(emptyList())
    }
    val context = LocalContext.current
    LaunchedEffect(key1 = searchText) {
        // Read Internal Storage
        withContext(Dispatchers.IO) {
            val internalStorageFilesResult = loadFilesFromInternalStorage(context, searchText).map {
                SearchResultItem(
                    storageType = StorageType.INTERNAL,
                    itemType = SearchResultType.FILE,
                    path = it
                )
            }
            val externalStorageFilesResult = loadFilesFromExternalStorage(context, searchText).map {
                SearchResultItem(
                    storageType = StorageType.EXTERNAL,
                    itemType = SearchResultType.FILE,
                    path = it
                )
            }
            val externalStorageDownloadResult = loadDownloadedFromExternalStorage(context, searchText).map {
                SearchResultItem(
                    storageType = StorageType.EXTERNAL,
                    itemType = SearchResultType.FILE,
                    path = it
                )
            }
            val externalStorageVideoResult = loadVideoFromExternalStorage(context, searchText).map {
                SearchResultItem(
                    storageType = StorageType.EXTERNAL,
                    itemType = SearchResultType.VIDEO,
                    path = it
                )
            }
            val externalStorageAudioResult = loadAudioFromExternalStorage(context, searchText).map {
                SearchResultItem(
                    storageType = StorageType.EXTERNAL,
                    itemType = SearchResultType.AUDIO,
                    path = it
                )
            }
            val externalStoragePhotosResult = loadPhotosFromExternalStorage(context, searchText).map {
                SearchResultItem(
                    storageType = StorageType.EXTERNAL,
                    itemType = SearchResultType.PHOTO,
                    path = it
                )
            }
            val callLogResult = loadCallLogs(context, searchText).map {
                SearchResultItem(
                    storageType = StorageType.EXTERNAL,
                    itemType = SearchResultType.CALL_LOG,
                    path = it
                )
            }
            withContext(Dispatchers.Main) {
                isSearching = false
                resultItems = mutableListOf<SearchResultItem>().apply {
                    addAll(internalStorageFilesResult)
                    addAll(externalStorageAudioResult)
                    addAll(externalStorageDownloadResult)
                    addAll(externalStorageFilesResult)
                    addAll(externalStoragePhotosResult)
                    addAll(externalStorageVideoResult)
                    addAll(callLogResult)
                }
            }
        }
    }
    Column {
        if (isSearching) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(resultItems) {
                    Text(text = it.path)
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
        val collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

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
        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL)

        val projection = arrayOf(
            MediaStore.Downloads._ID
        )
        val photos = mutableListOf<String>()
        context.contentResolver.query(
            collection,
            projection,
            MediaStore.Downloads.DISPLAY_NAME + "=? or " +
                    MediaStore.Downloads.ALBUM + "=? or " +
                    MediaStore.Downloads.ALBUM_ARTIST + "=? or " +
                    MediaStore.Downloads.ARTIST + "=? or " +
                    MediaStore.Downloads.AUTHOR + "=? or " +
                    MediaStore.Downloads.GENRE + "=? or " +
                    MediaStore.Downloads.TITLE + "=? or " +
                    MediaStore.Downloads.VOLUME_NAME + "=? or " +
                    MediaStore.Downloads.RELATIVE_PATH + "=? or ",
            arrayOf(searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText),
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
        val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID
        )
        val photos = mutableListOf<String>()
        context.contentResolver.query(
            collection,
            projection,
            MediaStore.Files.FileColumns.DISPLAY_NAME + "=? or " +
                    MediaStore.Files.FileColumns.ALBUM + "=? or " +
                    MediaStore.Files.FileColumns.ALBUM_ARTIST + "=? or " +
                    MediaStore.Files.FileColumns.ARTIST + "=? or " +
                    MediaStore.Files.FileColumns.AUTHOR + "=? or " +
                    MediaStore.Files.FileColumns.GENRE + "=? or " +
                    MediaStore.Files.FileColumns.TITLE + "=? or " +
                    MediaStore.Files.FileColumns.VOLUME_NAME + "=? or " +
                    MediaStore.Files.FileColumns.RELATIVE_PATH + "=? or ",
            arrayOf(searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText),
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
        val collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

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
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

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

private suspend fun loadCallLogs(context: Context, searchText: String) : List<String> {
    return withContext(Dispatchers.IO) {
        val logs: MutableList<String> = ArrayList()

        context.contentResolver.query(
            CallLog.Calls.CONTENT_URI, null,
            CallLog.Calls.CACHED_NAME + "=? or " +
            CallLog.Calls.NUMBER + "=? or " +
            arrayOf(searchText, searchText),
            null, null)
        ?.use { cursor ->
            val number = cursor.getColumnIndex(CallLog.Calls.NUMBER)
            val log = cursor.getString(number)
            if (log != null) {
                logs.add(log)
            }
        }
        logs
    }
}