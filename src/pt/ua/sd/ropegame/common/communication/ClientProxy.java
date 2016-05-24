package pt.ua.sd.ropegame.common.communication;

import genclass.GenericIO;
import pt.ua.sd.ropegame.common.interfaces.*;


/**
 *   Este tipo de dados define o thread agente prestador de serviço para uma solução do Problema dos Barbeiros
 *   Sonolentos que implementa o modelo cliente-servidor de tipo 2 (replicação do servidor) com lançamento estático dos
 *   threads barbeiro.
 *   A comunicação baseia-se em passagem de mensagens sobre sockets usando o protocolo TCP.
 */

public class ClientProxy extends Thread{
    /**
     *  Contador de threads lançados
     *
     *    @serialField nProxy
     */

    private static int nProxy;

    /**
     *  Canal de comunicação
     *
     *    @serialField sconi
     */

    private ServerCom sconi;

    /**
     *  Interface à barbearia
     *
     *    @serialField bShopInter
     */

    private IRequestHandler requestHandler;

    /**
     *  Instanciação do interface à barbearia.
     *
     *    @param sconi canal de comunicação
     *    @param requestHandler interface ao cliente
     */

    public ClientProxy(ServerCom sconi, IRequestHandler requestHandler)
    {
        super ("Proxy_" + getProxyId ());

        this.sconi = sconi;
        this.requestHandler = requestHandler;
    }

    /**
     *  Ciclo de vida do thread agente prestador de serviço.
     */

    @Override
    public void run ()
    {
        Message inMessage = null,                                      // mensagem de entrada
                outMessage = null;                      // mensagem de saída

        inMessage = (Message) sconi.readObject ();                     // ler pedido do cliente
        outMessage = requestHandler.processAndReply (inMessage);         // processá-lo
        sconi.writeObject (outMessage);                                // enviar resposta ao cliente
        sconi.close ();                                                // fechar canal de comunicação
    }

    /**
     *  Geração do identificador da instanciação.
     *
     *    @return identificador da instanciação
     */

    private static int getProxyId ()
    {
        Class<ClientProxy> cl = null;             // representação do tipo de dados ClientProxy na máquina
                                                        //   virtual de Java
        int proxyId;                                         // identificador da instanciação

        try
        { cl = (Class<ClientProxy>) Class.forName ("pt.ua.sd.ropegame.common.communication.ClientProxy");
        }
        catch (ClassNotFoundException e)
        { GenericIO.writelnString ("O tipo de dados ClientProxy não foi encontrado!");
            e.printStackTrace ();
            System.exit (1);
        }

        synchronized (cl)
        { proxyId = nProxy;
            nProxy += 1;
        }

        return proxyId;
    }

}
