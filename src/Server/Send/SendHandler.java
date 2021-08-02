package Server.Send;

import Server.Device;
import Server.Recv.Task;

import java.io.File;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class SendHandler extends Thread
{

    private BlockingQueue<Task> mWaitingList;

    public SendHandler(BlockingQueue<Task> queue)
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
                Task t = mWaitingList.take();
                handleTask(t);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void handleTask(Task t)
    {

        File file = t.getFile();
        if (!file.exists())
        {
            System.out.print("SendHandler[ERR] : Missed file: ");
            System.out.println(file);
            return;
        }

        Device device = t.getDevice();
        Socket socket = new Socket();

        try
        {
            socket.connect(device.getInetSocketAddress(), 1000);
            sendFile(socket, file);
            socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void sendFile(Socket socket, File f)
    {
        System.out.println(f.getName());
    }


}
