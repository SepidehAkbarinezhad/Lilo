package com.sepideh.lilo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform