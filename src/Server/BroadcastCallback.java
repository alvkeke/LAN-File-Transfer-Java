package Server;

import java.net.SocketAddress;

public interface BroadcastCallback {
    void gotClientOnline(String user, SocketAddress address);
    void gotClientOffline(String user);
}
