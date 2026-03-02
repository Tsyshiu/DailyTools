package com.github.tsyshiu.dailytools

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform