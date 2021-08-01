package Server;


import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;

public class MainEntry {

    ArrayBlockingQueue<File> queue;

    public static void main(String[] args)
    {
        new MainEntry();
    }

    private MainEntry()
    {
        queue = new ArrayBlockingQueue<>(1024);

        SenderHandler senderHandler = new SenderHandler(queue);
        senderHandler.start();

        new Thread(new AAA()).start();

    }

    class AAA implements Runnable
    {
        @Override
        public void run()
        {
            for (int i=0; i<5; i++)
            {
                queue.add(new File("testfile"+i));
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


}
