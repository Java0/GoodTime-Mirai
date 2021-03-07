package org.example.mirai.plugin

import goodtime.mirai.JavaPluginMain
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.utils.BotConfiguration

@ConsoleExperimentalApi
suspend fun main() {
    MiraiConsoleTerminalLoader.startAsDaemon()

    //�����kotlin
    //PluginMain.load()
    //PluginMain.enable()
    //�����java
    JavaPluginMain.INSTANCE.load()
    JavaPluginMain.INSTANCE.enable()

    val bot = MiraiConsole.addBot(3391246527, "jjp651121") {
        fileBasedDeviceInfo()
    }.alsoLogin()
    MiraiConsole.job.join()
}
