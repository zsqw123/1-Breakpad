package com.example.breakpadapp

/**
 * Author zsqw123
 * Create by zsqw123
 * Date 2021/12/19 10:23 上午
 */
interface INativeBridge {
    fun makeCrash()
    fun initCrash(storePath: String)
}