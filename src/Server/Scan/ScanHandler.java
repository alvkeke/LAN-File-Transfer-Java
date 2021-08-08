package Server.Scan;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Semaphore;

public class ScanHandler extends Thread
{

    private boolean mIsRunning;
    private DatagramSocket mSocket;
    private final ScanCallback mCallback;
    private int mPort = -1;

    private static final String mScanCmd = "SCAN";
    private static final byte[] mScanCmdBytes = mScanCmd.getBytes(StandardCharsets.US_ASCII);
    private static final DatagramPacket mScanPkg = new DatagramPacket(mScanCmdBytes, mScanCmdBytes.length);

    public ScanHandler(ScanCallback callback)
    {
        mCallback = callback;
    }

    public void exit()
    {
        mIsRunning = false;
        mSocket.close();
    }

    public void start(int port) throws SocketException, UnknownHostException
    {

        mPort = port;
        mScanPkg.setPort(port);
        mScanPkg.setAddress(InetAddress.getByName("255.255.255.255"));
        mIsRunning = true;
        mSocket = new DatagramSocket(port);

        super.start();
    }

    public boolean startScan()
    {
        return new ScanStartThread().tryScan();
    }

    public void responseScan(InetAddress requestAddr)
    {
        byte[] name_buf = mCallback.getDeviceName().getBytes();
        int bb_len = name_buf.length + 4;
        // bb_len = len(name_buf) + len(port);
        ByteBuffer bb = ByteBuffer.allocate(bb_len);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        bb.putInt(mCallback.getRecvPort());
        bb.put(name_buf);
        byte[] pkg_data = bb.array();

        DatagramPacket packet = new DatagramPacket(pkg_data, pkg_data.length);
        packet.setAddress(requestAddr);
        packet.setPort(mPort);

        try
        {
            mSocket.send(packet);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void run()
    {

        if (mSocket == null)
        {
            System.out.println("ScanHandler[ERR]: socket was not prepared.");
            return;
        }

        while (mIsRunning)
        {
            byte[] recv_buf = new byte[40];
            DatagramPacket packet = new DatagramPacket(recv_buf, recv_buf.length);
            try
            {
                mSocket.receive(packet);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                continue;
            }

            int pkg_len = packet.getLength();

            if (pkg_len == mScanCmdBytes.length && new String(recv_buf, 0, pkg_len).equals(mScanCmd))
            {   // response a scan request, notify the here is a valid device.
                responseScan(packet.getAddress());
            }
            else
            {   // got an valid device response; this part will take place unpredictably.
                ByteBuffer bb = ByteBuffer.wrap(recv_buf);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                InetAddress addr = packet.getAddress();
                int port = bb.getInt();

                String dev_name = new String(recv_buf, Integer.BYTES, pkg_len - Integer.BYTES);

                mCallback.foundDevice(new Device(dev_name, addr, port));
            }
        }

    }

    class ScanStartThread implements Runnable
    {

        private final Semaphore sem;
        private boolean mResult = false;

        ScanStartThread()
        {
            sem = new Semaphore(0);
        }

        public boolean tryScan()
        {

            new Thread(this).start();

            try
            {
                sem.acquire();
            }
            catch (InterruptedException ignored)
            {
                return false;
            }

            return mResult;
        }

        @Override
        public void run()
        {
            mResult = true;
            try
            {
                mSocket.send(mScanPkg);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            sem.release();
        }

    }

}
