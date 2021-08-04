package Server.Ctrl;

import Server.Scan.Device;

import java.io.File;
import java.util.ArrayList;

public interface CtrlCallback
{
    boolean sendFile(File f, Device dev);
    ArrayList<Device> scanDevice();
    boolean setSavePath(File dir);
}
