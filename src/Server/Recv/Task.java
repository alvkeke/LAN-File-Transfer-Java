package Server.Recv;

import Server.Scan.Device;

import java.io.File;

public class Task
{
    private final File mFile;
    private final Device mDevice;

    public Task(File f2send, Device dev)
    {
        mFile = f2send;
        mDevice = dev;
    }

    public Task(String filename, Device dev)
    {
        mFile = new File(filename);
        mDevice = dev;
    }

    public final File getFile()
    {
        return mFile;
    }

    public final Device getDevice()
    {
        return mDevice;
    }

}
