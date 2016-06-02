package pt.ua.sd.ropegame.bench;


import pt.ua.sd.ropegame.common.DOMParser;
import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.communication.ServerCom;
import pt.ua.sd.ropegame.common.interfaces.IBenchGenRep;
import pt.ua.sd.ropegame.common.communication.ClientProxy;

/**
 * Server that accepts to establish communication with clients in order to access {@link Bench}
 * Also instantiates a client to send messages to {@link pt.ua.sd.ropegame.genrepository.GeneralRepositoryServer}
 */
public class BenchServer {


    public static void main(String[] args) {

        if(args.length != 1)
            throw new IllegalArgumentException("Utilização: java -jar refereesite <config.xml>");

        GameOfTheRopeConfigs configs;
        try {
            configs = new GameOfTheRopeConfigs(new DOMParser(args[0]));
        } catch (Exception e) {
            throw new IllegalArgumentException("Ocorreu um erro ao carregar o ficheiro de configuração.");
        }

        int localPortNumber = configs.getBenchPort();
        ServerCom serverCom = new ServerCom(localPortNumber);
        serverCom.startCom();

        // assigning referee site to general repository
        String serverHostName = configs.getGenRepHostname();
        int remotePortNumber = configs.getGenRepPort();
        IBenchGenRep genRep = new BenchClient(serverHostName, remotePortNumber);

        Bench bench = new Bench(genRep, configs);
        BenchRequestHandler benchRequestHandler = new BenchRequestHandler(bench);

        ServerCom serverComInterface;
        ClientProxy proxy;
        System.out.println("Servidor em escuta!");
        while(true) {

            serverComInterface = serverCom.accept();
            proxy = new ClientProxy(serverComInterface, benchRequestHandler);
            proxy.start();
        }
    }

}
