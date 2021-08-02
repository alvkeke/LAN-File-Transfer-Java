package Server.Recv;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class RecvHandler extends Thread
{

    private ServerSocket mSocket;
    private File mSavePath;

    public RecvHandler()
    {
        mSavePath = new File(".");
    }

    public boolean setSavePath(File path)
    {
        if (!path.isDirectory())
            return false;

        mSavePath = path;
        return true;
    }

    public boolean setSavePath(String pathname)
    {
        File dir = new File(pathname);
        if (!dir.isDirectory()) return false;

        mSavePath = dir;
        return  true;
    }

    public void start(int port) throws Exception
    {
        mSocket = new ServerSocket(port);
        super.start();
    }

    @Override
    public void run()
    {

        if (mSocket == null)
        {
            System.out.println("RecvHandler[ERR] : socket was not initialized.");
            return;
        }

        while(true)
        {
            try
            {
                Socket s = mSocket.accept();
                new Thread(new RecvChildThread(s)).start();

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }


    class RecvChildThread implements Runnable
    {

        private Socket mSocket;

        RecvChildThread(Socket socket)
        {
            this.mSocket = socket;
        }

        @Override
        public void run()
        {

            if (mSocket == null)
            {
                System.out.println("RecvHandler[ERR] : create child thread failed, socket empty.");
                return;
            }

            try
            {

                DataInputStream dis = new DataInputStream(mSocket.getInputStream());

                long fileLen = dis.readLong();
                int nameLen = dis.readInt();

                byte[] nameBuf = new byte[nameLen];
                int nameLenRead = dis.read(nameBuf);

                System.out.print("File length: ");
                System.out.println(fileLen);

                System.out.print("Name length: ");
                System.out.print(nameLen);
                System.out.print(" : ");
                System.out.println(nameLenRead);

                System.out.print("Name: ");
                System.out.println(new String(nameBuf));

                byte[] read_buf = new byte[1024];
                long data_left = fileLen;

                System.out.println("=============== Data Read ===============");
                while(data_left>0)
                {
                    int read_len = dis.read(read_buf);
                    System.out.println("read length = " + read_len);
//                    System.out.println(new String(read_buf, read_len));
                    if (read_len < 0) break;
                    data_left -= read_len;
                }
                System.out.println("=============== Data Read ===============");

                dis.close();

                System.out.println("RecvHandler[SUC] : File received successfully.");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }


        }
    }

}
