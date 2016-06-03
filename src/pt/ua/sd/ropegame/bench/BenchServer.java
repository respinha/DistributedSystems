package pt.ua.sd.ropegame.bench;


import pt.ua.sd.ropegame.common.DOMParser;
import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.communication.ServerCom;
import pt.ua.sd.ropegame.common.interfaces.IBenchGenRep;
import pt.ua.sd.ropegame.common.communication.ClientProxy;

import java.rmi.server.UnicastRemoteObject;

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

        String rmiRegHostName = configs.getRmiHost();
        int rmiRegPortNumb = configs.getRmiPort();

        int localPortNumber = configs.getBenchPort();

        String genRepHostname = configs.getGenRepHostname();
        int genRepPort = configs.getGenRepPort();

        if(System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
            System.out.println("Security Manager was installed.");
        }

        Bench bench = new Bench(genRep, configs);
        BenchRequestHandler benchRequestHandler = null;

        try {
            benchRequestHandler = (BenchRequestHandler) UnicastRemoteObject.exportObject(bench, localPortNumber);
        }


        ServerCom serverCom = new ServerCom(localPortNumber);
        serverCom.startCom();

        // assigning referee site to general repository

        IBenchGenRep genRep = new BenchClient(genRepHostname, genRepPort);

        Bench bench =
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
