package com.vishnutadimeti.serverside;

/**
 * Created by vishnutadimeti on 11/27/16.
 */

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Scanner;

// This is the code for the Server

class Server {
    private MainActivity activity;
    private ServerSocket serverSocket;
    private static final int socketServerPORT = 8080;

    Server(MainActivity activity) {
        this.activity = activity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    int getPort() {
        return socketServerPORT;
    }

    void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(socketServerPORT);

                // Initializing Socket
                
                while (true) {
                    Socket socket = serverSocket.accept();
                    InputStream inStream = socket.getInputStream();
                    Scanner in = new Scanner(inStream);
                    
                    if (in.hasNextLine()) {
                        // Using Scanner to read data sent by the Client (i.e Username and Highscore)
                        String input = in.nextLine();
                        int points = Integer.parseInt(input.substring(0,2).trim());
                        String uname = input.substring(2, input.length());

                        if (input.equals("request")) {

                        } else {
                            // Adding records sent from the Client (Username and Highscore) to the Database
                            Database db = new Database(activity.getApplicationContext());
                            db.addRecord(points, uname);
                            try (OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
                            {
                                // Sending Username and Highscore from the Database to Client for the Leaderboard
                                out.write(db.getResults().toString());
                                out.flush();
                            }
                            Log.d("JSON: ", String.valueOf(db.getResults()));
                        }
                    }
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    // Method to return the IP Address as a String 
    
    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at : "
                                + inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}
