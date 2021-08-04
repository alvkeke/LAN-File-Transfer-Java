package Server;


import Server.Scan.Device;
import Server.Ctrl.CtrlCallback;
import Server.Ctrl.CtrlHandler;
import Server.Recv.RecvHandler;
import Server.Recv.Task;
import Server.Send.SendHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class MainEntry implements CtrlCallback
{

    ArrayBlockingQueue<Task> queue;
    SendHandler mSendHandler;
    RecvHandler mRecvHandler;
    CtrlHandler mCtrlHandler;

    public static void main(String[] args)
    {
        new MainEntry();
    }

    private MainEntry()
    {
        queue = new ArrayBlockingQueue<>(1024);

        mSendHandler = new SendHandler(queue);
        mRecvHandler = new RecvHandler();
        mCtrlHandler = new CtrlHandler(this);
        try
        {
            mCtrlHandler.start(10001);
            mRecvHandler.start(10000);
            mSendHandler.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        new Thread(new SendCtrlTest()).start();
//        new Thread(new AddSendTaskTest()).start();

    }

    @Override
    public boolean sendFile(File f, Device dev)
    {
        if (f.isDirectory() || !f.exists()) return false;
        mSendHandler.addTask(new Task(f, dev));
        return true;
    }

    @Override
    public ArrayList<Device> scanDevice()
    {
        ArrayList<Device> array = new ArrayList<>();


//        for (int i=0; i<5; i++)
//        {
//            try
//            {
//                array.add(
//                        new Device("dev"+1,
//                                InetAddress.getByName("192.168.1.10"+i),
//                                3000+i)
//                        );
//            }
//            catch (UnknownHostException e)
//            {
//                e.printStackTrace();
//            }
//        }

        return array;
    }

    @Override
    public boolean setSavePath(File dir)
    {
        if (dir.exists() && dir.isDirectory())
        {
            mRecvHandler.setSavePath(dir);
            return true;
        }
        return false;
    }

    class SendCtrlTest implements Runnable
    {

        @Override
        public void run()
        {

            Socket socket = new Socket();

            try
            {
                socket.connect(new InetSocketAddress(InetAddress.getLoopbackAddress(), 10001));

                OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());

                osw.write("scan\n");
//                osw.flush();

                osw.write("send README.md 127.0.0.1 10000\n");
                osw.write("send README.md 127.r.0.1 10r00\n");
                osw.write("send README.md 127.0.0.1 10r00\n");
//                osw.flush();

                osw.write("set save-path ../\n");
//                osw.flush();

                osw.write("exit\n");
                osw.flush();

                osw.close();
                socket.close();

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }


        }
    }

    class AddSendTaskTest implements Runnable
    {
        @Override
        public void run()
        {
            for (int i=0; i<1; i++)
            {
                try
                {
                    queue.add(new Task("/home/alvis/workspace/LAN-File-Transfer-Java/README.md", new Device("Device", "127.0.0.1", 10000)));
                    Thread.sleep(1000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


}
