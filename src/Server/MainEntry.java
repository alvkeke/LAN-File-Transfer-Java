package Server;


import Server.Scan.Device;
import Server.Ctrl.CtrlCallback;
import Server.Ctrl.CtrlHandler;
import Server.Recv.RecvHandler;
import Server.Recv.Task;
import Server.Send.SendHandler;

import java.io.File;
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
            new Thread(new AAA()).start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void sendFile(File f, Device dev)
    {
        mSendHandler.addTask(new Task(f, dev));
    }

    @Override
    public ArrayList<Device> scanDevice()
    {
        ArrayList<Device> array = new ArrayList<>();

        return array;
    }

    @Override
    public void setSavePath(File dir)
    {
        mRecvHandler.setSavePath(dir);
    }

    class AAA implements Runnable
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
