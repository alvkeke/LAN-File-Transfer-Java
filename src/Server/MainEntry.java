package Server;

import java.io.File;
import java.net.SocketAddress;
import java.util.HashMap;

public class MainEntry implements BroadcastCallback, CommandCallback{

    private HashMap<String, SocketAddress> userList;
    private BroadcastHandler bcHandler;

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

        // todo: delete the codes below
        System.out.println();
        System.out.println("this line is for reachable test, please delete this line when the application finished");
    }

    private MainEntry(String username, int beginPort){

        userList = new HashMap<>();

        bcHandler = new BroadcastHandler(username, this);
        if (!bcHandler.startListen(beginPort)){
            printErrorMsg("start broadcast handler failed");
            return;
        }
        System.out.println("INFO: start broadcast handler success!");

        // todo: start file receive thread;
        FileRecvHandler rfHandler = new FileRecvHandler();
        if(!rfHandler.init(beginPort+1)){
            printErrorMsg("start file receive handler failed");
            return;
        }
        System.out.println("INFO: start file receive handler success!");

        // todo: start command receive thread;

    }

    @Override
    public void gotClientOnline(String user, SocketAddress address) {
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
    public void sendFile(String fileFullPath) {

    }

    private static void printHelp(){
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


}
