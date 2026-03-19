package com.example.zaikokanri

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform