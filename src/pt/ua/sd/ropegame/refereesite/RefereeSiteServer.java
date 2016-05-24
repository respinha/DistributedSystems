package pt.ua.sd.ropegame.refereesite;

import genclass.GenericIO;
import pt.ua.sd.ropegame.common.DOMParser;
import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.communication.ServerCom;
import pt.ua.sd.ropegame.common.interfaces.IRefSiteGenRep;
import pt.ua.sd.ropegame.common.communication.ClientProxy;

/**
 * RefereeSite server module that connects to a GeneralRepository instance.
 */
public class RefereeSiteServer {


    public static void main(String[] args) {
        if(args.length != 1)
            throw new IllegalArgumentException("Utilização: java -jar refereesite <config.xml>");

        GameOfTheRopeConfigs configs;
        try {
            configs = new GameOfTheRopeConfigs(new DOMParser(args[0]));
        } catch (Exception e) {
            throw new IllegalArgumentException("Ocorreu um erro ao carregar o ficheiro de configuração.");
        }

        int localPortNumber = configs.getRefSitePort();
        ServerCom serverCom = new ServerCom(localPortNumber);
        serverCom.startCom();

        // assigning referee site to general repository
        int remotePortNumber = configs.getGenRepPort();
        String serverHostName = configs.getGenRepHostname();
        IRefSiteGenRep genRep = new RefereeSiteClient(serverHostName, remotePortNumber);

        RefereeSite refereeSite = new RefereeSite(genRep, configs);
        RefereeSiteRequestHandler refereeSiteRequestHandler = new RefereeSiteRequestHandler(refereeSite);


        ServerCom serverComInterface;
        ClientProxy proxy;
        GenericIO.writelnString("Servidor em escuta!");
        while(true) {

            serverComInterface = serverCom.accept();
            proxy = new ClientProxy(serverComInterface, refereeSiteRequestHandler);
            proxy.start();
        }


    }

}
