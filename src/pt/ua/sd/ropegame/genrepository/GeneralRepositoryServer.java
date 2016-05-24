package pt.ua.sd.ropegame.genrepository;

import genclass.GenericIO;
import pt.ua.sd.ropegame.common.DOMParser;
import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.communication.ServerCom;
import pt.ua.sd.ropegame.common.communication.ClientProxy;

/**
 * Server that accepts to establish communication with clients in order to access {@link GeneralRepository}
 */
public class GeneralRepositoryServer {

    public static void main(String[] args) {
        if(args.length != 1)
            throw new IllegalArgumentException("Utilização: java -jar refereesite <config.xml>");

        GameOfTheRopeConfigs configs;
        try {
            configs = new GameOfTheRopeConfigs(new DOMParser(args[0]));
        } catch (Exception e) {
            throw new IllegalArgumentException("Ocorreu um erro ao carregar o ficheiro de configuração.");
        }

        int localPortNumber = configs.getGenRepPort();
        ServerCom serverCom = new ServerCom(localPortNumber);
        serverCom.startCom();

        String logFile = configs.getLogFileName();

        ServerCom serverComInterface;

        // shared regions and interfaces
        GeneralRepository generalRepository = new GeneralRepository(logFile);
        GeneralRepositoryRequestHandler generalRepositoryInterface = new GeneralRepositoryRequestHandler(generalRepository);

        ClientProxy proxy;
        GenericIO.writelnString("Servidor em escuta!");
        while(true) {

            serverComInterface = serverCom.accept();
            proxy = new ClientProxy(serverComInterface, generalRepositoryInterface);
            proxy.start();
        }
    }



}
