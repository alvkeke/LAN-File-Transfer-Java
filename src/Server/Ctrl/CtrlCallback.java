package Server.Ctrl;

import Server.Scan.Device;

import java.io.File;
import java.util.ArrayList;

public interface CtrlCallback
{
    boolean scanDevice();
    Device findDevice(String devName);
    ArrayList<Device> getOnlineDevices();
    boolean sendFile(File f, Device dev);
    boolean setSavePath(File dir);
}
