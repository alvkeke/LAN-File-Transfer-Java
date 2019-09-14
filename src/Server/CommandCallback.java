package Server;

import java.util.ArrayList;
import java.util.Set;

public interface CommandCallback {
    void refreshUserList();
    boolean sendFile(String deviceName, String fileFullPath);
    ArrayList<String> getCredibleUsers();
    Set<String> getOnlineUsers();
}
