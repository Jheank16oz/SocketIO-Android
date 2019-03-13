package com.jheank16oz.sockettest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jheank16oz.sockettest.eventservice.EventListener
import com.jheank16oz.sockettest.eventservice.EventServiceImpl
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var globalMessage: String= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val event = EventServiceImpl.getInstance()
        event?.setEventListener(object:EventListener{
            override fun onConnect(vararg args: Any) {
                Log.e("ws onConnect", "->")

            }

            override fun onDisconnect(vararg args: Any) {
                Log.e("ws onDisconnect", "->")

            }

            override fun onConnectError(vararg args: Any) {
                Log.e("ws onConnectError", "->")

            }

            override fun onNewMessage(message: String) {
                Log.e("ws onNewMessage", "->")


                runOnUiThread {
                    log.text = message
                }

            }

            override fun onResultConnection(connected: Boolean) {
                Log.e("ws onResultConnection", "->")

            }

        })
        event?.connect()
    }
}
