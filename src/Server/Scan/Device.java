package Server.Scan;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Device
{
    private final InetAddress mAddress;
    private final int mPort;
    private final String mName;

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

    public String getName()
    {
        return mName;
    }

    public InetSocketAddress getInetSocketAddress()
    {
        return new InetSocketAddress(mAddress, mPort);
    }

}
