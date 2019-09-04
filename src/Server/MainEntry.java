package Server;

import java.io.File;
import java.net.InetAddress;
import java.util.HashMap;

import static Server.FileRecvThread.*;

public class MainEntry implements BroadcastCallback, CommandCallback, FileRecvCallback{

    private HashMap<String, InetAddress> userList;
    private BroadcastHandler bcHandler;
    private int mBeginPort;
    private String mDeviceName;

    public static void main(String[] args){

        if (!(args.length < 1)){
            if (args[0].equals("-h")){
                printHelp();
                return;
            }
        }

        if (args.length<2){
            printErrorMsg("parameter error!");
            System.out.println("To start server: java -jar packet-name device-name begin-port");
            System.out.println("please make sure begin-port is 10000 if not necessary to change.");
            return;
        }

        try {
            String username = args[0];
            int beginPort = Integer.parseInt(args[1]);

            new MainEntry(username, beginPort);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    private MainEntry(String username, int beginPort){

        // load my device name from the configure file;
        mDeviceName = username;

        userList = new HashMap<>();
        mBeginPort = beginPort;

        bcHandler = new BroadcastHandler(username, this);
        if (!bcHandler.startListen(beginPort)){     // broadcast handler use UDP socket
            printErrorMsg("start broadcast handler failed");
            return;
        }
        bcHandler.broadcast();
        bcHandler.requestBroadcast();
        System.out.println("INFO: start broadcast handler success!");

        // start file receive thread;
        FileRecvHandler frHandler = new FileRecvHandler(this);
        if(!frHandler.init(beginPort)){             // file receiver use TCP socket, so using the same port will be ok
            printErrorMsg("start file receive handler failed");
            return;
        }
        System.out.println("INFO: start file receive handler success!");

        // todo: start command receive thread;


        // todo: test
//        sendFile("alv-rasp3b", "/home/alvis/desktop/test.py");

    }

    @Override
    public void gotClientOnline(String user, InetAddress address) {
        System.out.println(user + " online");
        userList.put(user, address);
    }

    @Override
    public void gotClientOffline(String user) {
        userList.remove(user);
    }

    private static void printErrorMsg(String msg){
        System.out.println();
        System.out.println("ERROR: !!!!!!!! " + msg );
        System.out.println();
    }

    @Override
    public void refreshUserList() {
        userList.clear();
        bcHandler.broadcast();
    }

    @Override
    public void sendFile(String deviceName, String fileFullPath) {
        File file = new File(fileFullPath);
        if (!file.exists()){
            printErrorMsg("file is not exist");
            return;
        }

        //todo:delete
        System.out.println("begin send file");
        InetAddress address = userList.get(deviceName);
        FileSender fs = new FileSender(mDeviceName, address, mBeginPort);
        fs.send(file);

    }

    private static void printHelp(){
        //todo: need to be edited
        System.out.println("help:");
        System.out.println("\tto start the server, you should run command below:");
        System.out.println();
        System.out.println("\t[ java -jar server-packet device-name begin-of-ports ]");
        System.out.println();
        System.out.println("\tdevice-name: the identity of your device on the local net,");
        System.out.println("\tif your name is the same as other, you will kick that device offline");
        System.out.println();
        System.out.println("\tbegin-of-ports: this is the begin of the ports this application will take,");
        System.out.println("\texample, if you input 10000, then 10000, 10001, 10002 thread ports will be take");
        System.out.println("\tif one of three ports is occupied by other application, then this server cannot be start");
        System.out.println("\tplease make sure begin-port is 10000 if not necessary to change.");
    }

    @Override
    public boolean isCredible(String username) {
        // todo : edit this method, read name in an exist configure file
        return username.equals("alv-manjaro") || username.equals("alv-rasp3b");
    }

    @Override
    public void gotFile(String fileLocation) {
        System.out.println("got file: " + fileLocation);
    }

    @Override
    public void recvFileFailed(byte Reason, String param) {
        switch (Reason){
            case RECV_FAILED_DATA_ERROR:
                System.out.println(">>got error file info: "+ param);
                break;
            case RECV_FAILED_INCREDIBLE:
                System.out.println(param + " is incredible, " +
                        "if you want to receive file sent from this device, please add it to the credible conf");
                break;
        }
    }
}
