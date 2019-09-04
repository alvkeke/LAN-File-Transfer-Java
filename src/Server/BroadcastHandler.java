package Server;

import java.io.IOException;
import java.net.*;

class BroadcastHandler {

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

            while (inLoop){

                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    mSocket.receive(packet);

                    // cmd(30) + username
                    String data = new String(packet.getData()).trim();
                    String cmd = data.substring(0, 30);
                    String username = data.substring(30);

                    if (cmd.equals(Cs.LOGIN_STR_CMD)){
                        mCallback.gotClientOffline(username);
                        mCallback.gotClientOnline(username, packet.getAddress());
                        // 接收到客户端上线时也广播一次登录信息
                        broadcast();
                    } else if (cmd.equals(Cs.LOGOUT_STR_CMD)) {
                        mCallback.gotClientOffline(username);
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
        String strSend = Cs.LOGIN_STR_CMD + mUsername;
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
