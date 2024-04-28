package com.ashish.phonesearch

sealed class Screen(val route: String) {
    data object SearchScreen: Screen("search_screen")
    data object ResultScreen: Screen("result_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}