package Server.Scan;

public interface ScanCallback
{
    void foundDevice(Device dev);

    String getDeviceName();
    int getRecvPort();

}
