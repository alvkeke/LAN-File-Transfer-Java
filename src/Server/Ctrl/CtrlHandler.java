package Server.Ctrl;

import Server.Recv.RecvHandler;
import Server.Scan.Device;

import javax.sound.midi.SysexMessage;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class CtrlHandler extends Thread
{

    private ServerSocket mSocket;
    private final CtrlCallback mCallback;

    public CtrlHandler(CtrlCallback callback)
    {
        mCallback = callback;
    }

    public void start(int port) throws Exception
    {
        mSocket = new ServerSocket(port);
        super.start();
    }

    @Override
    public void run()
    {
        if (mSocket == null)
        {
            System.out.println("RecvHandler[ERR] : socket was not initialized.");
            return;
        }

        while (true)
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

            while (true)
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

                    if (Objects.equals(splits[0], "exit"))
                    {
                        break;
                    }
                    else if (Objects.equals(splits[0], "send"))
                    {
                        if (splits.length != 4)
                        {
                            bw.write("failed: wrong params count\n");
                            bw.flush();
                            continue;
                        }

                        try
                        {
                            InetAddress addr = InetAddress.getByName(splits[2]);
                            int port = Integer.parseInt(splits[3]);

                            // device name is useless here.
                            if (!mCallback.sendFile(new File(splits[1]), new Device("", addr, port)))
                            {
                                bw.write("failed: params error\n");
                                bw.flush();
                            }
                            bw.write("success\n");
                            bw.flush();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            bw.write("failed: params error\n");
                            bw.flush();
                        }
                    }
                    else if (Objects.equals(splits[0], "scan"))
                    {
                        ArrayList<Device> array = mCallback.scanDevice();
                        for (Device d : array)
                        {
                            bw.write(d.getName());
                            bw.write(" ");
                            InetSocketAddress so_addr = d.getInetSocketAddress();
                            String sip = so_addr.getHostString();
                            String sport = String.valueOf(so_addr.getPort());
                            bw.write(sip);
                            bw.write(" ");
                            bw.write(sport);
                            bw.write("\n");
                        }
                        bw.flush();
                    }
                    else if (Objects.equals(splits[0], "set"))
                    {
                        if (splits.length != 3)
                        {
                            bw.write("failed: wrong params count\n");
                            bw.flush();
                            continue;
                        }

                        boolean ret;
                        switch (splits[1])
                        {
                            case "save-path":
                                ret = mCallback.setSavePath(new File(splits[2]));
                                break;
                            default:
                                ret = false;
                        }
                        if (ret)
                            bw.write("success\n");
                        else
                            bw.write("failed");

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
