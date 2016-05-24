package pt.ua.sd.ropegame.playground;

import genclass.GenericIO;
import pt.ua.sd.ropegame.common.DOMParser;
import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.interfaces.IPlaygroundGenRep;
import pt.ua.sd.ropegame.common.communication.ServerCom;
import pt.ua.sd.ropegame.common.communication.ClientProxy;

/**
 * Server that accepts to establish communication with clients in order to access {@link Playground}
 * Also instantiates a client to send messages to {@link pt.ua.sd.ropegame.genrepository.GeneralRepositoryServer}
 */
public class PlaygroundServer {

    public static void main(String[] args) {
        if(args.length != 1)
            throw new IllegalArgumentException("Utilização: java -jar refereesite <config.xml>");

        GameOfTheRopeConfigs configs;
        try {
            configs = new GameOfTheRopeConfigs(new DOMParser(args[0]));
        } catch (Exception e) {
            throw new IllegalArgumentException("Ocorreu um erro ao carregar o ficheiro de configuração.");
        }

        int localPortNumber = configs.getPlaygroundPort();
        ServerCom serverCom = new ServerCom(localPortNumber);
        serverCom.startCom();

        // assigning referee site to general repository
        int remotePortNumber = configs.getGenRepPort();
        String serverHostName = configs.getGenRepHostname();
        IPlaygroundGenRep genRep = new PlaygroundClient(serverHostName, remotePortNumber);

        Playground playground = new Playground(genRep, configs);
        PlaygroundRequestHandler playgroundRequestHandler = new PlaygroundRequestHandler(playground);


        ServerCom serverComInterface;
        ClientProxy proxy;
        GenericIO.writelnString("Servidor em escuta!");
        while(true) {

            serverComInterface = serverCom.accept();
            proxy = new ClientProxy(serverComInterface, playgroundRequestHandler);
            proxy.start();
        }
    }


}
