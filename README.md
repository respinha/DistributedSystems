# Game of the Rope #

This is the RMI implementation of a client-server solution of the classic Game of the Rope problem. In this game, there are two temas, each with 5 contestants and 1 coach, and a referee. Each contestant has its own strength, and the strongest team is able to move the rope and win. All game members are implemented as threads which access three distributed shared memory regions.   Communication between clients and servers is now implemented by the RMI abstraction, registring servers in the RMI registry and accessing all required methods by clients with stub interfaces.
This project is the third of three projects from the Distributed Systems course, from the 4th year of the Integrated Masters in Computers and Telematics Engineering (University of Aveiro).

### Main keywords ###
* Java
* Concurrency
* Explicit monitors
* Sockets
* Parallelism
* RMI
* Client-server programming

### Running ###

* Just run the deployment script run.sh

### Owners ###

The entire solution was developped by Rui Espinha Ribeiro ([Espinha](https://github.com/responha)) and David Silva ([dmpasilva](https://bitbucket.org/dmpasilva)).
