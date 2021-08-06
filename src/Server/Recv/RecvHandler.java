package Server.Recv;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class RecvHandler extends Thread
{

    private boolean mIsRunning;
    private ServerSocket mSocket;
    private File mSavePath;

    private boolean firstRecv = true;

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

    public void exit()
    {
        mIsRunning = false;
        try
        {
            mSocket.close();
        }
        catch (IOException ignored)
        {
        }
    }

    public void start(int port) throws Exception
    {
        mIsRunning = true;
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

        Socket s = null;
        while(mIsRunning)
        {
            try
            {
                s = mSocket.accept();
                new Thread(new RecvChildThread(s)).start();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            if (s != null) s.close();
            mSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }


    class RecvChildThread implements Runnable
    {

        private final Socket mSocket;

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

            File fout;

            try
            {

                DataInputStream dis = new DataInputStream(mSocket.getInputStream());

                long fileLen = dis.readLong();
                int nameLen = dis.readInt();

                byte[] nameBuf = new byte[nameLen];
                int nameLenRead = dis.read(nameBuf);

                if (nameLenRead != nameLen)
                {
                    System.out.println("RecvHandler[ERR] : File received failed: file name data not enough.");
                }

                String filename = new String(nameBuf);
                fout = new File(mSavePath, filename);
                if (fout.exists())
                {
                    int ibreak = filename.lastIndexOf(".");
                    String suffix = filename.substring(ibreak);
                    String prefix = filename.substring(0, ibreak);
                    long timestamp = new Date().getTime();

                    filename = prefix + '.' + timestamp + suffix;

                    fout = new File(mSavePath, filename);
                    System.out.println("new File name: " + filename);
                }

                byte[] read_buf = new byte[1024];
                long data_left = fileLen;

                FileOutputStream fos = new FileOutputStream(fout);

                while(data_left>0)
                {
                    int read_len = dis.read(read_buf);

                    if (read_len < 0)
                    {
                        System.out.println("RecvHandler[ERR] : Connection closed exception.");
                        break;
                    }

                    fos.write(read_buf, 0, read_len);

                    data_left -= read_len;
                }
                fos.flush();
                fos.close();

                dis.close();

                if (data_left != 0)
                {
                    System.out.println("RecvHandler[ERR] : File received failed: wrong data length, but data was stored.");
                    return;
                }

                System.out.println("RecvHandler[SUC] : File received successfully.");

                mSocket.close();
            }
            catch (IOException e)
            {
                System.out.println("RecvHandler[ERR] : File received failed: Exception.");
                e.printStackTrace();
            }

        }
    }

}
