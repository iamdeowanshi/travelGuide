package com.ithakatales.android.player;

import android.os.Looper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import timber.log.Timber;

/**
 * @author farhanali
 *
 * Ref: https://gist.github.com/frostymarvelous/26ac6cba11bf50e591a4
 */
public class EncryptedAudioHttpServer implements Runnable {

    public static int SERVER_PORT = 7990;

    private Thread thread;
    private ServerSocket socket;
    private Socket client;
    private MediaStreamingTask streamTask;

    private boolean isRunning;
    private int port;

    public EncryptedAudioHttpServer() {
        // Create listening socket
        SERVER_PORT ++;
        try {
            socket = new ServerSocket(SERVER_PORT, 0, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
            socket.setSoTimeout(5000);
            port = socket.getLocalPort();
        } catch (UnknownHostException e) { // impossible
        } catch (IOException e) {
            Timber.e("IOException during initializing server, possibly port is already in use: " + SERVER_PORT, e);
        }
    }

    public String getUrl(String path, String key, String iv) throws Exception {
        String url = String.format("http://127.0.0.1:%d%s?key=%s&iv=%s", port, path, URLEncoder.encode(key, "UTF-8"), iv);

        return url;
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        Looper.prepare();
        isRunning = true;
        while (isRunning) {
            try {
                client = socket.accept();
                if (client == null) {
                    continue;
                }
                Timber.d("client connected");

                streamTask = new MediaStreamingTask(client);
                if (streamTask.processRequest()) {
                    streamTask.execute();
                }
            } catch (SocketTimeoutException e) {
                // Do nothing
            } catch (IOException e) {
                Timber.e("Error connecting to client", e);
            }
        }
        Timber.d("Proxy interrupted. Shutting down.");
    }

}