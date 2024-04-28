package com.ashish.phonesearch

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun ResultScreen(searchText: String) {
    var isSearching by remember {
        mutableStateOf(true)
    }
    var resultItems by remember {
        mutableStateOf<List<String>>(emptyList())
    }
    LaunchedEffect(key1 = searchText) {
        withContext(Dispatchers.IO) {
            delay(2000L)
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