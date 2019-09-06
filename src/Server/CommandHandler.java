package Server;

public class CommandHandler {

    private CommandCallback mCallback;

    CommandHandler(CommandCallback callback){
        mCallback = callback;
    }

}
