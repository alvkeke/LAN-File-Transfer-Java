package Server.Scan;

public interface ScanCallback
{

    /**
     * this function will be called by ScanHandler, it was used to indicate that
     * there was a valid response;
     * @param dev device that response from local net
     */
    void foundDevice(Device dev);

    /**
     * the master of the ScanHandler need to pass self's device name by this method.
     * @return self's device name
     */
    String getDeviceName();

    /**
     * the master of the ScanHandler need to pass self's recv-port by this method.
     * @return self's recv-port;
     */
    int getRecvPort();

}
