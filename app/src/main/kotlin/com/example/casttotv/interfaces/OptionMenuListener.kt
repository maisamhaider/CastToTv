package com.example.casttotv.interfaces

interface OptionMenuListener {
    fun <T> item(itemId: Int, dataClass: T)
}