package Server;


import Server.Recv.RecvHandler;
import Server.Recv.Task;
import Server.Send.SendHandler;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;

public class MainEntry {

    ArrayBlockingQueue<Task> queue;

    public static void main(String[] args)
    {
        new MainEntry();
    }

    private MainEntry()
    {
        queue = new ArrayBlockingQueue<>(1024);

        SendHandler sendHandler = new SendHandler(queue);
        RecvHandler recvHandler = new RecvHandler();
        recvHandler.initialize(12345);
        recvHandler.start();
        sendHandler.start();

        new Thread(new AAA()).start();

    }

    class AAA implements Runnable
    {
        @Override
        public void run()
        {
            for (int i=0; i<5; i++)
            {
                try
                {
                    queue.add(new Task("README.md", new Device("Device", "127.0.0.1", 12345)));
//                    Thread.sleep(1000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


}
