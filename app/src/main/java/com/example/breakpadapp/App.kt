package com.example.breakpadapp

import android.app.Application
import java.io.File

/**
 * Author zsqw123
 * Create by zsqw123
 * Date 2021/12/19 3:28 下午
 */
lateinit var app: App
    private set

class App : Application() {
    init {
        app = this
    }

    override fun onCreate() {
        super.onCreate()
        NativeBridgeLoder.load()
    }
}