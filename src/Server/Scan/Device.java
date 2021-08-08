package Server.Scan;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Device
{
    private final InetAddress mAddress;
    private final int mPort;
    private String mName;

    public Device(String name, InetAddress addr, int port)
    {
        mName = name;
        mAddress = addr;
        mPort = port;
    }

    public Device(String name, String ip, int port) throws Exception
    {
        mName = name;
        mAddress = InetAddress.getByName(ip);
        mPort = port;
    }

    public boolean isSameDevice(Device dev)
    {
        InetAddress ip = dev.getInetSocketAddress().getAddress();
        int port = dev.getInetSocketAddress().getPort();

        return port == mPort && ip.equals(mAddress);
    }

    public void setName(String newName)
    {
        mName = newName;
    }

    public String getName()
    {
        return mName;
    }

    public InetSocketAddress getInetSocketAddress()
    {
        return new InetSocketAddress(mAddress, mPort);
    }

}
