package Server;


import Server.Ctrl.CtrlCallback;
import Server.Ctrl.CtrlHandler;
import Server.Recv.RecvHandler;
import Server.Recv.Task;
import Server.Scan.Device;
import Server.Scan.ScanCallback;
import Server.Scan.ScanHandler;
import Server.Send.SendHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class MainEntry implements CtrlCallback, ScanCallback
{

    ArrayBlockingQueue<Task> queue;
    SendHandler mSendHandler;
    RecvHandler mRecvHandler;
    CtrlHandler mCtrlHandler;
    ScanHandler mScanHandler;

    private final String DEV_NAME = "alv-manjaro-laptop";
    // ========== TCP Port ==============
    private final int CTRL_PORT = 10001;
    private final int RECV_PORT = 10000;
    // ========== UDP Port ==============
    private final int SCAN_PORT = 10000;

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
        mScanHandler = new ScanHandler(this);
        try
        {
            mCtrlHandler.start(CTRL_PORT, true);
            mRecvHandler.start(RECV_PORT);
            mSendHandler.start();
            mScanHandler.start(SCAN_PORT);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

//        new Thread(new CtrlCloseTest()).start();
//        new Thread(new SendCtrlTest()).start();
//        new Thread(new AddSendTaskTest()).start();

        mScanHandler.startScan();


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

    @Override
    public void foundDevice(Device dev)
    {
        System.out.println(dev.getName());
        System.out.println(dev.getInetSocketAddress().getAddress());
        System.out.println(dev.getInetSocketAddress().getPort());
    }

    @Override
    public String getDeviceName()
    {
        return DEV_NAME;
    }

    @Override
    public int getRecvPort()
    {
        return RECV_PORT;
    }

    class CtrlCloseTest implements Runnable
    {

        @Override
        public void run()
        {

            Socket socket = new Socket();
            try
            {
                socket.connect(new InetSocketAddress(InetAddress.getLoopbackAddress(), 10001));
                socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();

            }
        }
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
            for (int i=0; i<2; i++)
            {
                try
                {
                    queue.add(new Task("/home/alvis/workspace/LAN-File-Transfer-Java/README.md", new Device("Device", "127.0.0.1", 10000)));
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
