package Server;

public interface FileRecvCallback {
    boolean isCredible(String username);
    void gotFile(String fileLocation);
    void recvFileFailed(byte Reason, String param);
}
