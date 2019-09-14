package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class CommandHandler {

    private static final String CMD_GET_ONLINE_USERS =      "_GET_ONLINE_USERS_LIST________";
    private static final String CMD_SEND_FILE =             "_SEND_FILE_TO_________________";
    private static final String CMD_GET_CREDIBLE_USERS =    "_GET_CREDIBLE_USERS_LIST______";
//    private static final String CMD_

    private CommandCallback mCallback;
    private ServerSocket serverSocket;
    private boolean inLoop;

    CommandHandler(CommandCallback callback) {
        mCallback = callback;
    }

    boolean startListen(int port) {

        inLoop = true;
        try {
            serverSocket = new ServerSocket(port);
            new Thread(new ListenThread()).start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    class ListenThread implements Runnable {
        @Override
        public void run() {

            while (inLoop) {
                try {
                    Socket socketAccept = serverSocket.accept();
                    System.out.println("admin enter.");

                    char[] buf = new char[1024];

                    BufferedReader br = new BufferedReader(new InputStreamReader(socketAccept.getInputStream()));
                    Arrays.fill(buf, (char)0);
                    int len = br.read(buf);
                    String dataIn = new String(buf).trim();

                    if (dataIn.length() < 30){
                        continue;
                    }

                    String cmd = dataIn.substring(0, 30);
                    String data = dataIn.substring(30);

                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socketAccept.getOutputStream()));

                    StringBuilder sb = new StringBuilder();
                    String dataOut = null;

                    switch (cmd){
                        case CMD_GET_CREDIBLE_USERS:
                            ArrayList<String> cl = mCallback.getCredibleUsers();
                            for(String s: cl){
                                sb.append(s);
                                sb.append('|');
                            }

                            dataOut = sb.toString();

                            break;
                        case CMD_GET_ONLINE_USERS:
                            Set<String> olUsers = mCallback.getOnlineUsers();
                            for (String s : olUsers){
                                sb.append(s);
                                sb.append('|');
                            }

                            dataOut = sb.toString();
                            break;
                        case CMD_SEND_FILE:
                            String[] spl = data.split("\\|");
                            if (spl.length < 2){
                                break;
                            }
                            String deviceName = spl[1];
                            String filePath = spl[0];

                            mCallback.sendFile(deviceName, filePath);

                            break;
                    }

                    if (dataOut != null) {
                        bw.write(dataOut);
                        bw.flush();
                    }

                    br.close();
                    bw.close();
                    socketAccept.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void exit() {
        inLoop = false;
    }

}
