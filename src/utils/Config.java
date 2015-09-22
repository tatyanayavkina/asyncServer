package utils;

/**
 * Created on 22.09.2015.
 */
public class Config {
    private final String host;
    private final int port;
    private final int messageLimit;
    private final int threadCount;

    public Config(String host, String port, String messageLimit, String threadCount){
        this.host = host;
        this.port = Integer.parseInt(port);
        this.messageLimit = Integer.parseInt(messageLimit);
        this.threadCount = Integer.parseInt(threadCount);
    }

    public int getPort(){
        return port;
    }

    public int getMessageLimit(){
        return messageLimit;
    }

    public int getThreadCount(){
        return threadCount;
    }

    public String getHost(){
        return host;
    }
}
