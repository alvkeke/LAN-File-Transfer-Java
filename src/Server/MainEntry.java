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
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class MainEntry implements CtrlCallback, ScanCallback
{

    private final ArrayBlockingQueue<Task> queue;
    private final ArrayList<Device> mAvailableDevices;

    private final SendHandler mSendHandler;
    private final RecvHandler mRecvHandler;
    private final CtrlHandler mCtrlHandler;
    private final ScanHandler mScanHandler;

    private final String DEV_NAME = "GameBook[Win]";
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
        mAvailableDevices = new ArrayList<>();
        queue = new ArrayBlockingQueue<>(1024);

        mSendHandler = new SendHandler(queue);
        mRecvHandler = new RecvHandler();
        mCtrlHandler = new CtrlHandler(this);
        mScanHandler = new ScanHandler(this);
        try
        {
            mCtrlHandler.start(CTRL_PORT, false);
            mRecvHandler.start(RECV_PORT);
            mSendHandler.start();
            mScanHandler.start(SCAN_PORT);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        boolean ret = mScanHandler.startScan();
        System.out.println(ret);

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }


        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }




    }

    @Override
    public boolean scanDevice()
    {
        return mScanHandler.startScan();
    }

    @Override
    public Device findDevice(String devName)
    {
        for (Device d : mAvailableDevices)
        {
            if (d.getName().equals(devName))
                return d;
        }

        return null;
    }

    @Override
    public ArrayList<Device> getOnlineDevices()
    {
        return mAvailableDevices;
    }

    @Override
    public boolean sendFile(File f, Device dev)
    {
        if (f.isDirectory() || !f.exists()) return false;
        mSendHandler.addTask(new Task(f, dev));
        return true;
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
        if (dev == null) return;

        for (Device d : mAvailableDevices)
        {
            if (d.isSameDevice(dev))
            {
                if (!dev.getName().equals(d.getName()))
                    d.setName(dev.getName());
                return;
            }
        }

        mAvailableDevices.add(dev);

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

}
