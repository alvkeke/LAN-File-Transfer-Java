package Server;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

class BroadcastHandler {

    static final String CMD_LOGIN_STR =         "_THIS_IS_AN_CLIENT_ONLINE_____";
    static final String CMD_LOGOUT_STR =        "_THIS_IS_AN_CLIENT_OFFLINE____";
    static final String CMD_BROADCAST_REQUEST = "_PLEASE_SEND_A_BROADCAST______";

    private String mUsername;
    private DatagramSocket mSocket;
    private int mBroadPort;
    private BroadcastCallback mCallback;
    private boolean inLoop;

    BroadcastHandler(String username, BroadcastCallback callback){
        mUsername = username;
        mCallback = callback;
    }

    boolean startListen(int port){

        inLoop = true;
        mBroadPort = port;
        try {
            mSocket = new DatagramSocket(mBroadPort);
            new Thread(new handleThread()).start();

            return true;
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return false;
    }

    class handleThread implements Runnable{
        @Override
        public void run() {

            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            while (inLoop){

                Arrays.fill(buf, (byte) 0);
                packet.setData(buf);
                try {
                    mSocket.receive(packet);

                    // cmd(30) + username
                    String data = new String(packet.getData()).trim();
                    if (data.length() <= 30){
                        continue;   // 防止字符串过短而导致程序关闭
                    }
                    String cmd = data.substring(0, 30);
                    String username = data.substring(30);

                    InetAddress remoteAddr = packet.getAddress();

                    // 判断设备名可以防止同一个设备名的两个设备同时在线
                    if (username.equals(mUsername)){
                        continue;
                    }

                    switch (cmd) {
                        case CMD_LOGIN_STR:
                            mCallback.gotClientOffline(username);
                            mCallback.gotClientOnline(username, remoteAddr);
                            break;
                        case CMD_LOGOUT_STR:
                            mCallback.gotClientOffline(username);
                            break;
                        case CMD_BROADCAST_REQUEST:
                            broadcast();
                            break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    void exit(){
        inLoop = false;
    }

    void broadcast(){
        // complete the method that broadcast this client's msg to other client
        String strSend = CMD_LOGIN_STR + mUsername;
        byte[] data = strSend.getBytes();
        try {
            InetAddress address = InetAddress.getByName("255.255.255.255");
            DatagramPacket packet = new DatagramPacket(data, data.length, address, mBroadPort);
            mSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void requestBroadcast(){

        // complete the method that broadcast this client's msg to other client
        String strSend = CMD_BROADCAST_REQUEST + mUsername;
        byte[] data = strSend.getBytes();
        try {
            InetAddress address = InetAddress.getByName("255.255.255.255");
            DatagramPacket packet = new DatagramPacket(data, data.length, address, mBroadPort);
            mSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
