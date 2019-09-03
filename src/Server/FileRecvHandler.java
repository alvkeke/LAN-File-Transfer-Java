package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileRecvHandler {

    private ServerSocket serverSocket;

    boolean init(int port){

        try {
            serverSocket = new ServerSocket(port);
            new Thread(new ListenThread()).start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    class ListenThread implements Runnable{
        @Override
        public void run() {

            while (true){
                try {   //todo: complete the method
                    Socket socketAccept = serverSocket.accept();
                    new Thread(new FileRecvThread(socketAccept)).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
