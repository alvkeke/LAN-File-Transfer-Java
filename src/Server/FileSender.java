package Server;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

class FileSender {

    String mDeviceName;
    InetAddress mAddress;
    int mPort;

    FileSender(String deviceName, InetAddress address, int port){
        mDeviceName = deviceName;
        mAddress = address;
        mPort = port;

    }

    void send(File file){
        try {
            Socket socket = new Socket(mAddress, mPort);
            FileInputStream fis = new FileInputStream(file);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeUTF(mDeviceName);
            dos.writeUTF(file.getName());
            byte[] buf = new byte[1024];
            int length;
            while ((length = fis.read(buf)) != -1){
                dos.write(buf, 0, length);
                dos.flush();
            }
            System.out.println("file send success");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("file send failed");
        }
    }
}
