package com.ashish.phonesearch

data class SearchResultItem(
    val storageType: StorageType,
    val itemType: SearchResultType,
    val path: String
)
