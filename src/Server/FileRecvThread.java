package Server;

import java.net.Socket;

public class FileRecvThread implements Runnable{

    private Socket mSocket;

    FileRecvThread(Socket socket){
        mSocket = socket;
    }

    @Override
    public void run() {

    }
}
