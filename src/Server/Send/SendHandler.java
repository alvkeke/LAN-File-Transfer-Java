package Server.Send;

import Server.Scan.Device;
import Server.Recv.Task;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

public class SendHandler extends Thread
{

    private boolean mIsRunning;
    private BlockingQueue<Task> mWaitingList;

    public SendHandler(BlockingQueue<Task> queue)
    {
        mWaitingList = queue;
    }

    public void exit()
    {
        mIsRunning = false;
    }

    @Override
    public synchronized void start()
    {
        mIsRunning = true;
        super.start();
    }

    @Override
    public void run()
    {

        while(mIsRunning)
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

    public void addTask(Task t)
    {
        mWaitingList.add(t);
    }

    private void handleTask(Task t)
    {

        File file = t.getFile();
        if (!file.exists() || file.isDirectory())
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
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            sendFile(dos, file);

            dos.close();
            socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void sendFile(DataOutputStream dos, File f) throws IOException
    {
        // len[8], name_len[4], name[~], data[~]
        ByteBuffer bbf = ByteBuffer.allocate(Long.BYTES);
        bbf.putLong(f.length());
        dos.write(bbf.array());

        bbf = ByteBuffer.allocate(Integer.BYTES);
        bbf.putInt(f.getName().length());
        dos.write(bbf.array());

        dos.writeBytes(f.getName());

        FileInputStream fis = new FileInputStream(f);
        byte[] buf = new byte[1024];

        dos.flush();

        int read_len;
        while((read_len = fis.read(buf)) > 0)
        {
            dos.write(buf, 0, read_len);
            dos.flush();
        }

        fis.close();

    }


}
