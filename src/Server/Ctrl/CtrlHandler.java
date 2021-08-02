package Server.Ctrl;

public class CtrlHandler extends Thread
{

    private final CtrlCallback mCallback;

    public CtrlHandler(CtrlCallback callback)
    {
        mCallback = callback;
    }

    public void start(int port)
    {
        super.start();
    }

    @Override
    public void run()
    {

    }
}
