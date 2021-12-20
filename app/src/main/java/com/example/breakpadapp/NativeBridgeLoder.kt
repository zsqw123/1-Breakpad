package com.example.breakpadapp

/**
 * Author zsqw123
 * Create by zsqw123
 * Date 2021/12/19 10:40 上午
 */
object NativeBridgeLoder {
    private var isLoaded = false
    private var loadRes: INativeBridge = NativeBridgeSafe
    private fun ensureLoad() {
        if (!isLoaded) {
            System.loadLibrary("breakpadapp")
            loadRes = NativeBridge
            println("======breakpad loaded")
            isLoaded = true
        }
    }


    fun load(): INativeBridge {
        ensureLoad()
        return loadRes
    }
}

private object NativeBridge : INativeBridge {
    external override fun makeCrash()
    external override fun initCrash(storePath: String)
}

private object NativeBridgeSafe : INativeBridge {
    override fun makeCrash() {
        println("触发兜底策略-makeCrash")
    }

    override fun initCrash(storePath: String) {
        println("触发兜底策略-initCrash")
    }
}