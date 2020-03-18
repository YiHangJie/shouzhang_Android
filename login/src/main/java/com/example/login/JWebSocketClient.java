package com.example.login;

import android.util.Log;

import androidx.fragment.app.Fragment;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class JWebSocketClient extends WebSocketClient {



    public JWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d("JWebSocketClient", "----onOpen()----");
    }

    @Override
    public void onMessage(String message) {
        Log.e("JWebSocketClient", "----onMessage()----");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d("JWebSocketClient", "----onClose()----");
    }

    @Override
    public void onError(Exception ex) {
        Log.d("JWebSocketClient", "----onError()----");
    }
}
