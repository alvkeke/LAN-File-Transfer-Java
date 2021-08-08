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

public class ServerCore implements CtrlCallback, ScanCallback
{

    private final ArrayBlockingQueue<Task> queue;
    private final ArrayList<Device> mAvailableDevices;

    private SendHandler mSendHandler;
    private RecvHandler mRecvHandler;
    private CtrlHandler mCtrlHandler;
    private ScanHandler mScanHandler;

    private final String mDeviceName;
    // ========== TCP Port ==============
    private final int mPortCtrl;
    private final int mPortRecv;
    // ========== UDP Port ==============
    private final int mPortScan;


    public static void main(String[] args)
    {

        Configure conf = new Configure("config");
        ServerCore server = new ServerCore(conf);
        server.startServer();

    }

    private ServerCore(Configure conf)
    {

        mAvailableDevices = new ArrayList<>();
        queue = new ArrayBlockingQueue<>(1024);

        mDeviceName = conf.getDeviceName();
        mPortCtrl = conf.tcpPortCtrl;
        mPortRecv = conf.tcpPortRecv;
        mPortScan = conf.udpPortScan;

    }

    private void startServer()
    {
        mSendHandler = new SendHandler(queue);
        mRecvHandler = new RecvHandler();
        mCtrlHandler = new CtrlHandler(this);
        mScanHandler = new ScanHandler(this);
        try
        {
            mCtrlHandler.start(mPortCtrl, false);
            mRecvHandler.start(mPortRecv);
            mSendHandler.start();
            mScanHandler.start(mPortScan);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        mScanHandler.startScan();
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
    public String getSavePath()
    {
        return mRecvHandler.getSavePath();
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
        return mDeviceName;
    }

    @Override
    public int getRecvPort()
    {
        return mPortRecv;
    }

}
