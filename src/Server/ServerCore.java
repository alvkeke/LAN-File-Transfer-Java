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

    private final Configure mConf;

    enum ParamParseState {
        NONE,
        CONFIG,

    }

    public static void main(String[] args)
    {

        String conf_file = "config";

        ParamParseState state = ParamParseState.NONE;

        for (String s : args)
        {
            if (s == null) continue;
            if (state.equals(ParamParseState.CONFIG))
            {
                File fd_conf = new File(s);
                if (fd_conf.exists() && fd_conf.isFile())
                {
                    conf_file = s;
                    state = ParamParseState.NONE;
                }
            }
            else if (ParamParseState.NONE.equals(state))
            {
                switch (s)
                {
                    case "-c":
                        state = ParamParseState.CONFIG;
                        break;
                    default:
                        break;
                }
            }
        }

        Configure conf = new Configure(conf_file);
        ServerCore server = new ServerCore(conf);
        server.startServer();

    }

    private ServerCore(Configure conf)
    {

        mAvailableDevices = new ArrayList<>();
        queue = new ArrayBlockingQueue<>(1024);
        mConf = conf;

    }

    private void startServer()
    {
        mSendHandler = new SendHandler(queue);
        mRecvHandler = new RecvHandler();
        mCtrlHandler = new CtrlHandler(this);
        mScanHandler = new ScanHandler(this);

        mRecvHandler.setSavePath(mConf.getRecvPath());

        try
        {
            mCtrlHandler.start(mConf.getPortCtrl(), mConf.isLocalCtrlOnly());
            mRecvHandler.start(mConf.getPortRecv());
            mSendHandler.start();
            mScanHandler.start(mConf.getPortScan());
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
        return mConf.getDeviceName();
    }

    @Override
    public int getRecvPort()
    {
        return mConf.getPortRecv();
    }

}
