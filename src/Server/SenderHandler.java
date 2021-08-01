package Server;

import java.io.File;
import java.util.concurrent.BlockingQueue;

public class SenderHandler extends Thread
{

    private BlockingQueue<File> mWaitingList;

    public SenderHandler(BlockingQueue<File> queue)
    {
        mWaitingList = queue;

    }

    @Override
    public void run()
    {

        while(true)
        {
            try
            {
                File f2send = mWaitingList.take();
                sendFile(f2send);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void sendFile(File f)
    {
        System.out.println(f.getName());
    }


}
