package Server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

public class FileRecvThread implements Runnable{

    private Socket mSocket;
    private FileRecvCallback mCallback;

    static final byte RECV_FAILED_DATA_ERROR = 1;
    static final byte RECV_FAILED_INCREDIBLE = 2;

    FileRecvThread(FileRecvCallback callback, Socket socket){
        mCallback = callback;
        mSocket = socket;
    }

    @Override
    public void run() {
        /*
        * finish the method for receiving file
        * */
        try {
            DataInputStream dis = new DataInputStream(mSocket.getInputStream());

            byte[] device = new byte[256];
            byte[] fname = new byte[256];
            int len = dis.read(device);
            if (len < 256)
            {
                mCallback.recvFileFailed(RECV_FAILED_DATA_ERROR, null);
                dis.close();
                mSocket.close();
            }
            len = dis.read(fname);
            if (len < 256)
            {
                mCallback.recvFileFailed(RECV_FAILED_DATA_ERROR, null);
                dis.close();
                mSocket.close();
            }

            String username = new String(device).trim();
            String filename = new String(fname).trim();
            long fileLength = dis.readLong();

            if (!mCallback.isCredible(username)){
                mCallback.recvFileFailed(RECV_FAILED_INCREDIBLE, username);
                dis.close();
                mSocket.close();
                return;
            }

            // todo: change the directory, load from the configure file
//            File dir = new File("/home/alvis/download/fileTP/");
//            File dir = new File("D:\\Download\\fileTP\\");
            File dir = new File("C:\\Users\\alvis\\Downloads\\");
            File file = new File(dir, filename);
            if (file.exists()){
                file = new File(dir, filename +"_"+ new Date().getTime());
            }
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            long recvLength = 0;
            int partLength;
            while ((partLength = dis.read(buf)) != -1){
                fos.write(buf, 0, partLength);
                fos.flush();
                recvLength += partLength;
            }

            if (recvLength == fileLength)
            {
                mCallback.gotFile(file.getAbsolutePath());
            }
            else
            {
                mCallback.recvFileFailed(RECV_FAILED_DATA_ERROR, filename);
            }

            dis.close();
            fos.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
