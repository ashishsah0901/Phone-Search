package com.ashish.phonesearch

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.provider.CalendarContract
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Telephony
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
import java.lang.StringBuilder

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
            val externalStorageDownloadResult =
                loadDownloadedFromExternalStorage(context, searchText).map {
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
            val externalStoragePhotosResult =
                loadPhotosFromExternalStorage(context, searchText).map {
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
            val smsResult = loadSms(context, searchText).map {
                SearchResultItem(
                    storageType = StorageType.EXTERNAL,
                    itemType = SearchResultType.MESSAGE,
                    path = it
                )
            }
            val calenderEventsResult = loadCalenderEvents(context, searchText).map {
                SearchResultItem(
                    storageType = StorageType.EXTERNAL,
                    itemType = SearchResultType.CALENDER,
                    path = it
                )
            }
            val contactsResult = loadContacts(context, searchText).map {
                SearchResultItem(
                    storageType = StorageType.EXTERNAL,
                    itemType = SearchResultType.CONTACTS,
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
                    addAll(smsResult)
                    addAll(calenderEventsResult)
                    addAll(contactsResult)
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

private suspend fun loadFilesFromInternalStorage(
    context: Context,
    searchText: String
): List<String> {
    return withContext(Dispatchers.IO) {
        val files = context.filesDir.listFiles()
        files?.filter {
            it.canRead() && (it.name.contains(searchText) || it.readText().contains(searchText))
        }?.map {
            it.absolutePath
        } ?: listOf()
    }
}

private suspend fun loadAudioFromExternalStorage(
    context: Context,
    searchText: String
): List<String> {
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
            arrayOf(
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText
            ),
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
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

private suspend fun loadDownloadedFromExternalStorage(
    context: Context,
    searchText: String
): List<String> {
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
            arrayOf(
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText
            ),
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
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

private suspend fun loadFilesFromExternalStorage(
    context: Context,
    searchText: String
): List<String> {
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
            arrayOf(
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText
            ),
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
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

private suspend fun loadVideoFromExternalStorage(
    context: Context,
    searchText: String
): List<String> {
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
            arrayOf(
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText
            ),
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
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

private suspend fun loadPhotosFromExternalStorage(
    context: Context,
    searchText: String
): List<String> {
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
            arrayOf(
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText,
                searchText
            ),
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
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

private suspend fun loadCallLogs(context: Context, searchText: String): List<String> {
    return withContext(Dispatchers.IO) {
        val logs: MutableList<String> = ArrayList()

        context.contentResolver.query(
            CallLog.Calls.CONTENT_URI, null,
            CallLog.Calls.CACHED_NAME + "=? or " +
                    CallLog.Calls.NUMBER + "=? or " +
                    arrayOf(searchText, searchText),
            null, null
        )
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

private suspend fun loadSms(context: Context, searchText: String): List<String> {
    return withContext(Dispatchers.IO) {
        val cursor = context.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            null,
            Telephony.Sms.BODY + "=? or " +
                    Telephony.Sms.CREATOR + "=? or " +
                    Telephony.Sms.PERSON + "=? or " +
                    Telephony.Sms.SUBJECT + "=? or " +
                    Telephony.Sms.ADDRESS + "=? or ",
            arrayOf(searchText, searchText, searchText, searchText, searchText),
            null
        )
        val smsList = mutableListOf<String>()

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))
                smsList.add("Sender: $address\nMessage: $body")
            } while (cursor.moveToNext())
        }

        cursor?.close()
        smsList
    }
}

private suspend fun loadCalenderEvents(context: Context, searchText: String): List<String> {
    return withContext(Dispatchers.IO) {
        val cursor = context.contentResolver.query(
            CalendarContract.Instances.CONTENT_URI, null,
            CalendarContract.Instances.TITLE + "=? or " +
                    CalendarContract.Instances.DESCRIPTION + "=? or " +
                    CalendarContract.Instances.ORGANIZER + "=? or ",
            arrayOf(searchText, searchText, searchText), null
        )
        val calenderList = mutableListOf<String>()
        cursor?.use {
            val description = it.getString(2)
            val title = if (it.getString(6) == "1") "\uD83D\uDCC5 ${it.getString(1) ?: ""}"
            else "\uD83D\uDD53 ${it.getString(1) ?: ""} "
            calenderList.add("$title : $description")
        }
        calenderList
    }
}

@SuppressLint("Range")
private suspend fun loadContacts(context: Context, searchText: String) : List<String> {
    return withContext(Dispatchers.IO) {
        val cursor = context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + "=? or " +
            ContactsContract.Contacts.CONTACT_STATUS_LABEL + "=? or " +
            ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE + "=? or " +
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + "=? or " +
            ContactsContract.Contacts.DISPLAY_NAME_SOURCE + "=? or " +
            ContactsContract.Contacts.PHOTO_URI + "=? or " +
            ContactsContract.Contacts.Entity.DISPLAY_NAME + "=? or " +
            ContactsContract.Contacts.Entity.ACCOUNT_NAME + "=? or ",
            arrayOf(searchText, searchText, searchText, searchText, searchText, searchText, searchText, searchText),
            null
        )
        val contactsList = mutableListOf<String>()

        if (cursor != null && cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneNumber = (cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))).toInt()

                if (phoneNumber > 0) {
                    val cursorPhone = context.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null)

                    if (cursorPhone != null) {
                        if(cursorPhone.count > 0) {
                            while (cursorPhone.moveToNext()) {
                                val phoneNumValue = cursorPhone.getString(
                                    cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                val ans = StringBuilder().append("Contact: ").append(name).append(", Phone Number: ").append(
                                    phoneNumValue).append("\n\n")
                                contactsList.add(ans.toString())
                            }
                        }
                    }
                    cursorPhone?.close()
                }
            }
        }

        cursor?.close()
        contactsList
    }
}