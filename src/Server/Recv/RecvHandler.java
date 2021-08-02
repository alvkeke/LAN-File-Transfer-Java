package Server.Recv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class RecvHandler extends Thread
{

    private ServerSocket mSocket;

    public RecvHandler()
    {

    }

    public void initialize(int port)
    {
        try
        {
            mSocket = new ServerSocket(port);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

    }

    @Override
    public void run()
    {

        if (mSocket == null)
        {
            System.out.println("RecvHandler[ERR] : socket was not initialized.");
            return;
        }

        while(true)
        {
            try
            {
                Socket s = mSocket.accept();
                new Thread(new RecvChildThread(s)).start();

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }


    class RecvChildThread implements Runnable
    {

        private Socket mSocket;

        RecvChildThread(Socket socket)
        {
            this.mSocket = socket;
        }

        @Override
        public void run()
        {

            if (mSocket == null)
            {
                System.out.println("RecvHandler[ERR] : create child thread failed, socket empty.");
                return;
            }

            try
            {
                InputStreamReader isr = new InputStreamReader(mSocket.getInputStream());
                BufferedReader br = new BufferedReader(isr);

                mSocket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            System.out.println("RecvHandler[SUC] : File received successfully.");

        }
    }

}
