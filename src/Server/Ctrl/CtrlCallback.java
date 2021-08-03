package Server.Ctrl;

import Server.Scan.Device;

import java.io.File;
import java.util.ArrayList;

public interface CtrlCallback
{
    void sendFile(File f, Device dev);
    ArrayList<Device> scanDevice();
    void setSavePath(File dir);
}
