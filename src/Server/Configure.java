package Server;

import java.io.*;

public class Configure
{

    public final String DEFAULT_DEVICE_NAME = "Unknown Device";
    public final int DEFAULT_CTRL_PORT = 10001;
    public final int DEFAULT_RECV_PORT = 10000;
    public final int DEFAULT_SCAN_PORT = 10000;

    public String deviceName;
    public int tcpPortCtrl;
    public int tcpPortRecv;
    public int udpPortScan;

    private void parseConfigure(File conf)
    {

        deviceName = DEFAULT_DEVICE_NAME;
        tcpPortCtrl = DEFAULT_CTRL_PORT;
        tcpPortRecv = DEFAULT_RECV_PORT;
        udpPortScan = DEFAULT_SCAN_PORT;

        if (!conf.exists()) return;
        if (conf.isDirectory()) return;

        try
        {
            FileInputStream fis = new FileInputStream(conf);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while((line = br.readLine()) != null)
            {
                line = line.trim();
                if (line.equals("")) continue;
                if (line.charAt(0) == '#')
                {
                    System.out.print("Configure[I] : comment: ");
                    System.out.println(line);
                    continue;
                }

                String[] params = line.split("=", 2);

                if (params.length != 2)
                    continue;

                params[0] = params[0].trim();
                params[1] = params[1].trim();

                if (params[0].equals("recv port"))
                {
                    try
                    {
                        tcpPortRecv = Integer.parseInt(params[1]);
                        System.out.print("Configure[I] : recv port: ");
                        System.out.println(tcpPortRecv);
                    } catch (NumberFormatException ignored){
                        System.out.println("Configure[E] : receiving port format error.");
                    }
                }
                else if (params[0].equals("scan port"))
                {
                    try
                    {
                        udpPortScan = Integer.parseInt(params[1]);
                        System.out.print("Configure[I] : scan port: ");
                        System.out.println(udpPortScan);
                    } catch (NumberFormatException ignored){
                        System.out.println("Configure[E] : scan port format error.");
                    }
                }
                else if (params[0].equals("ctrl port"))
                {
                    try
                    {
                        tcpPortCtrl = Integer.parseInt(params[1]);
                        System.out.print("Configure[I] : ctrl port: ");
                        System.out.println(tcpPortCtrl);
                    } catch (NumberFormatException ignored){
                        System.out.println("Configure[E] : control port format error.");
                    }
                }
                else if (params[0].equals("device name"))
                {
                    deviceName = params[1];
                    System.out.print("Configure[I] : comment: ");
                    System.out.println(deviceName);
                }

            }

            br.close();
            isr.close();
            fis.close();
        }
        catch (IOException ignored)
        {}

    }

    public Configure(File conf)
    {
        parseConfigure(conf);
    }

    public Configure(String confFileName)
    {
        if (confFileName == null)
            parseConfigure(new File(""));
        else
            parseConfigure(new File(confFileName));
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public int getPortCtrl()
    {
        return tcpPortCtrl;
    }

    public int getPortRecv()
    {
        return tcpPortRecv;
    }

    public int getPortScan()
    {
        return udpPortScan;
    }




}
