package pt.ua.sd.ropegame.common.communication;



import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServerCom {

    private ServerSocket listeningSocket;
    private Socket commSocket;
    private int serverPortNumber;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ServerCom(int serverPortNumber) {
        this.serverPortNumber = serverPortNumber;
    }

    public ServerCom(ServerSocket listeningSocket, int serverPortNumber) {
        this.listeningSocket = listeningSocket;
        this.serverPortNumber = serverPortNumber;
    }

    /**
     * Starting listening socket.
     */
    public void startCom() {

        try {
            listeningSocket = new ServerSocket(serverPortNumber);
        } catch (BindException e) {
            System.out.println (Thread.currentThread ().getName () +
                    " - não foi possível a associação do socket de escuta ao port: " +
                    serverPortNumber + "!");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.out.println (Thread.currentThread ().getName () +
                    " - ocorreu um erro indeterminado na associação do socket de escuta ao port: " +
                    serverPortNumber + "!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Closing listening socket.
     */
    public void endCom() {

        try {
            listeningSocket.close();
        } catch (IOException e) {
            System.out.println (Thread.currentThread ().getName () +
                    " - não foi possível fechar o socket de escuta!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     *  Processo de escuta.
     *  Criação de um canal de comunicação para um pedido pendente.
     *  Instanciação de um socket de comunicação e sua associação ao endereço do cliente.
     *  Abertura dos streams de entrada e de saída do socket.
     *
     *    @return canal de comunicação
     */

    public ServerCom accept ()
    {
        ServerCom serverCom;                                      // canal de comunicação

        serverCom = new ServerCom(listeningSocket, serverPortNumber);
        try
        { serverCom.commSocket = listeningSocket.accept();
        }
        catch (SocketException e)
        { System.out.println (Thread.currentThread ().getName () +
                " - foi fechado o socket de escuta durante o processo de escuta!");
            e.printStackTrace ();
            System.exit (1);
        }
        catch (IOException e)
        { System.out.println (Thread.currentThread ().getName () +
                " - não foi possível abrir um canal de comunicação para um pedido pendente!");
            e.printStackTrace ();
            System.exit (1);
        }

        try
        { serverCom.in = new ObjectInputStream (serverCom.commSocket.getInputStream ());
        }
        catch (IOException e)
        { System.out.println (Thread.currentThread ().getName () +
                " - não foi possível abrir o canal de entrada do socket!");
            e.printStackTrace ();
            System.exit (1);
        }

        try
        { serverCom.out = new ObjectOutputStream (serverCom.commSocket.getOutputStream ());
        }
        catch (IOException e)
        { System.out.println (Thread.currentThread ().getName () +
                " - não foi possível abrir o canal de saída do socket!");
            e.printStackTrace ();
            System.exit (1);
        }

        return serverCom;
    }

    /**
     *  Fecho do canal de comunicação.
     *  Fecho dos streams de entrada e de saída do socket.
     *  Fecho do socket de comunicação.
     */

    public void close ()
    {
        try
        { in.close();
        }
        catch (IOException e)
        { System.out.println (Thread.currentThread ().getName () +
                " - não foi possível fechar o canal de entrada do socket!");
            e.printStackTrace ();
            System.exit (1);
        }

        try
        { out.close();
        }
        catch (IOException e)
        { System.out.println (Thread.currentThread ().getName () +
                " - não foi possível fechar o canal de saída do socket!");
            e.printStackTrace ();
            System.exit (1);
        }

        try
        { commSocket.close();
        }
        catch (IOException e)
        { System.out.println (Thread.currentThread ().getName () +
                " - não foi possível fechar o socket de comunicação!");
            e.printStackTrace ();
            System.exit (1);
        }
    }

    /**
     *  Leitura de um objecto do canal de comunicação.
     *
     *    @return objecto lido
     */

    public Object readObject ()
    {
        Object fromClient = null;                            // objecto

        try
        { fromClient = in.readObject ();
        }
        catch (InvalidClassException e)
        { System.out.println (Thread.currentThread ().getName () +
                " - o objecto lido não é passível de desserialização!");
            e.printStackTrace ();
            System.exit (1);
        }
        catch (IOException e)
        { System.out.println (Thread.currentThread ().getName () +
                " - erro na leitura de um objecto do canal de entrada do socket de comunicação!");
            e.printStackTrace ();
            System.exit (1);
        }
        catch (ClassNotFoundException e)
        { System.out.println (Thread.currentThread ().getName () +
                " - o objecto lido corresponde a um tipo de dados desconhecido!");
            e.printStackTrace ();
            System.exit (1);
        }

        return fromClient;
    }

    /**
     *  Escrita de um objecto no canal de comunicação.
     *
     *    @param toClient objecto a ser escrito
     */

    public void writeObject (Object toClient)
    {
        try
        { out.writeObject (toClient);
        }
        catch (InvalidClassException e)
        { System.out.println (Thread.currentThread ().getName () +
                " - o objecto a ser escrito não é passível de serialização!");
            e.printStackTrace ();
            System.exit (1);
        }
        catch (NotSerializableException e)
        { System.out.println (Thread.currentThread ().getName () +
                " - o objecto a ser escrito pertence a um tipo de dados não serializável!");
            e.printStackTrace ();
            System.exit (1);
        }
        catch (IOException e)
        { System.out.println (Thread.currentThread ().getName () +
                " - erro na escrita de um objecto do canal de saída do socket de comunicação!");
            e.printStackTrace ();
            System.exit (1);
        }
    }
    /*

Possibilidade de Construtor com backog: indica tamanho da fila de espera de atendimento das threads por parte do servidor
- utilidade: jogar com o facto da fila estar cheia (neste exemplo, barbearia cheia)

Server: ciclo infinito a tentar fazer o accept e o start e join consequentes da thread instanciada

Duas opções para condição de corrida relativa à instanciação de um canal de comunicação quando a variável do ciclo foi colocada a false:
- timeout (p.e. 1min)
- lançar SocketExcpexception -> no try catch verificamos se a excepção foi lançada por nós ou não
     */
}
