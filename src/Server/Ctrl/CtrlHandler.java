package Server.Ctrl;

import Server.Scan.Device;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class CtrlHandler extends Thread
{

    private boolean mIsRunning;
    private ServerSocket mSocket;
    private final CtrlCallback mCallback;

    public CtrlHandler(CtrlCallback callback)
    {
        mCallback = callback;
    }

    public void exit()
    {
        mIsRunning = false;
        try
        {
            mSocket.close();
        }
        catch (IOException ignored)
        {
        }
    }

    public void start(int port, boolean localOnly) throws Exception
    {
        mIsRunning = true;
        if (localOnly)
            mSocket = new ServerSocket(port, 0, InetAddress.getByName("localhost"));
        else
            mSocket = new ServerSocket(port);
        super.start();
    }

    private String handleCommand(ArrayList<String> cmdline)
    {

        if (cmdline.size() == 0) return "";

        String cmd = cmdline.get(0);
        StringBuilder sb = new StringBuilder();

        if ("exit".equals(cmd))
        {
            return null;
        }
        else if ("scan".equals(cmd))
        {
            boolean ret = mCallback.scanDevice();
            if (ret)
                sb.append("success");
            else
                sb.append("failed: cannot send request package.");
        }
        else if ("send".equals(cmd) && cmdline.size() == 3)
        {
            Device dev = mCallback.findDevice(cmdline.get(1));
            if (dev == null)
            {
                sb.append("failed: cannot find device has name: ")
                        .append(cmdline.get(1));
            }
            else
            {
                if (mCallback.sendFile(new File(cmdline.get(2)), dev))
                    sb.append("success");
                else
                    sb.append("failed: file is not exist.");
            }
        }
        else if ("send-addr".equals(cmd) && cmdline.size() == 4)
        {
            try
            {
                InetAddress addr = InetAddress.getByName(cmdline.get(1));
                int port = Integer.parseInt(cmdline.get(2));

                // device name is useless here.
                if (!mCallback.sendFile(new File(cmdline.get(3)), new Device("", addr, port)))
                    sb.append("failed: file is not exist.");
                else
                    sb.append("success");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                sb.append("failed: ip or port input error.");
            }
        }
        else if ("set".equals(cmd) && cmdline.size() == 3)
        {

            String data_item = cmdline.get(1);
            if ("save-path".equals(data_item))
            {
                if (mCallback.setSavePath(new File(cmdline.get(2))))
                    sb.append("success");
                else
                    sb.append("failed: not a valid directory.");

            }
            else
                sb.append("failed: configuration item not found.");
        }
        else if ("get".equals(cmd) && cmdline.size() == 2)
        {
            String data_item = cmdline.get(1);
            if ("dev-ol".equals(data_item))
            {
                ArrayList<Device> devices = mCallback.getOnlineDevices();
                for (Device d : devices)
                {
                    sb.append(d.getName())
                            .append(" ")
                            .append(d.getInetSocketAddress().getAddress().toString())
                            .append(" ")
                            .append(d.getInetSocketAddress().getPort())
                            .append("|");
                }
            }
            else if ("save-path".equals(data_item))
            {
                sb.append(mCallback.getSavePath());
            }
            else
                sb.append("failed: data item not found.");
        }
        else
        {
            sb.append("failed: command not found.");
        }

        sb.append("\n");
        return sb.toString();
    }

    @Override
    public void run()
    {
        if (mSocket == null)
        {
            System.out.println("RecvHandler[ERR] : socket was not initialized.");
            return;
        }

        while (mIsRunning)
        {
            Socket s;
            BufferedWriter bw;
            BufferedReader br;
            try
            {
                s = mSocket.accept();
                InputStreamReader isr = new InputStreamReader(s.getInputStream());
                br = new BufferedReader(isr);
                OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());
                bw = new BufferedWriter(osw);

            }
            catch (IOException e)
            {
                e.printStackTrace();
                continue;
            }

            System.out.println("CtrlHandler[I] : got new control connection.");

            while (mIsRunning)
            {
                try
                {
                    String cmdline = br.readLine();
                    if (cmdline == null)
                    {
                        System.out.println("CtrlHandler[ERR]: connection closed exception.");
                        break;
                    }
                    String[] splits = cmdline.split(" ");
                    if (splits.length == 0) continue;

                    ArrayList<String> split_list = new ArrayList<>(Arrays.asList(splits));
                    split_list.removeAll(Arrays.asList("", null));

                    String ret = handleCommand(split_list);
                    if (ret == null)
                        break;
                    else if (!ret.equals(""))
                    {
                        bw.write(ret);
                        bw.flush();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }

            try
            {
                bw.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                br.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                s.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            System.out.println("CtrlHandler[INFO]: Ctrl session closed.");

        }
    }
}
