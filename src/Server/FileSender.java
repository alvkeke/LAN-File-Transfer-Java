package Server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

class FileSender {

    private String mDeviceName;
    private InetAddress mAddress;
    private int mPort;

    FileSender(String deviceName, InetAddress address, int port){
        mDeviceName = deviceName;
        mAddress = address;
        mPort = port;
    }

    void send(File mFile){

        try
        {
            if (mFile.isDirectory() || !mFile.exists())
            {
                return;
            }
            long fileLength = mFile.length();
            // todo: 修改这里的设备名称
            byte[] deviceName = Arrays.copyOf("alv-xiaomi-4s".getBytes(), 256);
            byte[] filename = Arrays.copyOf(mFile.getName().getBytes(), 256);

            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(mAddress, mPort));

            // todo: 完成文件传输的功能
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.write(deviceName);
            dos.write(filename);
            dos.writeLong(fileLength);
            dos.flush();

            FileInputStream fis = new FileInputStream(mFile);
            byte[] buf = new byte[1024];
            int length = 0;
            long progress = 0;
            while((length = fis.read(buf, 0, buf.length)) != -1) {
                dos.write(buf, 0, length);
                dos.flush();
                progress += length;
            }

            dos.close();
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
