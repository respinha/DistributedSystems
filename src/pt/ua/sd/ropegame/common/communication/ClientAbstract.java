package pt.ua.sd.ropegame.common.communication;

/**
 * An abstract class for a client instantiation in servers for communication with {@link pt.ua.sd.ropegame.genrepository.GeneralRepositoryServer}
 */
public class ClientAbstract {

    protected ClientCom com;
    protected String serverHostName;
    protected int serverPortNumber;

    /**
     * Constructor.
     * @param serverHostName
     * @param serverPortNumber
     */
    public ClientAbstract(String serverHostName, int serverPortNumber) {

        this.serverHostName = serverHostName;
        this.serverPortNumber = serverPortNumber;
    }
}
