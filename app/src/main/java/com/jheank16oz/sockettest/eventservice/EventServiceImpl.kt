/*
 * Copyright 2018 Mayur Rokade
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package com.jheank16oz.sockettest.eventservice



import android.util.Log
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URI

/**
 * Implementation of [EventService] which connects and disconnects to the server.
 * It also sends and receives events from the server.
 */
class EventServiceImpl private constructor() : EventService {

    override fun test() {
        Log.e("ws is emmiting", "->")
        mSocket?.emit("submitCoordinates","Test Developer board -> 🤖: $mSocketEventInfo and $mSocketEventNotification")
    }

    override fun getConnectionStatus() {
        mEventListener?.onResultConnection(mSocket?.connected()?:false)

    }

    private var mUsername: String? = null


    private val onConnect = Emitter.Listener { args ->
        if (mEventListener != null) mEventListener?.onConnect(*args)
    }

    private val onDisconnect = Emitter.Listener { args ->
        if (mEventListener != null) mEventListener?.onDisconnect(*args)
    }

    private val onConnectError = Emitter.Listener { args ->
        if (mEventListener != null) mEventListener?.onConnectError(*args)
    }

    private val onNewMessage = Emitter.Listener { args ->

        try {
            val mJsonObject = JSONArray(args[0].toString())
            Log.e("result", mJsonObject.toString())
            if (mEventListener != null) mEventListener?.onNewMessage(mJsonObject.toString())

        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }

    override fun connect() {
        if (mSocket != null){
            if (mSocket?.connected() == true){
                Log.e("ws already connected","*")
                return
            }else{
                // restart socket connections
                mSocket = null
            }

        }
        val manager = Manager(URI(SOCKET_URL))
        mSocket = manager.socket("/")


        // Register the incoming events and their listeners
        // on the socket.
        mSocket?.on(EVENT_CONNECT, onConnect)
        mSocket?.on(EVENT_DISCONNECT, onDisconnect)
        mSocket?.on(EVENT_CONNECT_ERROR, onConnectError)
        mSocket?.on(EVENT_CONNECT_TIMEOUT, onConnectError)
        mSocket?.on("messages", onNewMessage)
        Log.e("ws connecting to event", "messages")

        mSocket?.connect()

    }

    /**
     * Disconnect from the server.
     *
     */
    override fun disconnect() {
        mSocket?.disconnect()
    }



    /**
     * Set eventListener.
     *
     * When server sends events to the socket, those events are passed to the
     * RemoteDataSource -> Repository -> Presenter -> View using EventListener.
     *
     * @param listener
     */
    override fun setEventListener(listener: EventListener) {
        mEventListener = listener
    }



    companion object {

        private val TAG = EventServiceImpl::class.java.simpleName
        private const val SOCKET_URL = "http://192.168.90.87:8080"
        private const val EVENT_CONNECT = Socket.EVENT_CONNECT
        private const val EVENT_DISCONNECT = Socket.EVENT_DISCONNECT
        private const val EVENT_CONNECT_ERROR = Socket.EVENT_CONNECT_ERROR
        private const val EVENT_CONNECT_TIMEOUT = Socket.EVENT_CONNECT_TIMEOUT
        private var INSTANCE: EventService? = null
        private var mEventListener: EventListener? = null
        private var mSocket: Socket? = null
        private var mSocketEventInfo: String? = null
        private var mSocketEventNotification: String? = null

        /**
         * Returns single instance of this class, creating it if necessary.
         *
         * @return
         */
        fun getInstance():EventService?{
            if (INSTANCE == null) {
                INSTANCE = EventServiceImpl()
            }

            return INSTANCE
        }
    }
}
