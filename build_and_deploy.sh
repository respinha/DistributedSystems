javac -cp src src/pt/ua/sd/ropegame/common/interfaces/*.java
javac -cp src src/pt/ua/sd/ropegame/common/enums/*.java
javac -cp src src/pt/ua/sd/ropegame/common/communication/*.java
javac -cp src src/pt/ua/sd/ropegame/common/*.java
javac -cp src src/pt/ua/sd/ropegame/genrepository/*.java
javac -cp src src/pt/ua/sd/ropegame/bench/*.java
javac -cp src src/pt/ua/sd/ropegame/playground/*.java
javac -cp src src/pt/ua/sd/ropegame/refereesite/*.java
javac -cp src src/pt/ua/sd/ropegame/referee/*.java
javac -cp src src/pt/ua/sd/ropegame/team/*.java

mkdir deploy/pt/
mkdir deploy/pt/ua/
mkdir deploy/pt/ua/sd/
mkdir deploy/pt/ua/sd/ropegame/
mkdir deploy/pt/ua/sd/ropegame/common/
mkdir deploy/pt/ua/sd/ropegame/common/interfaces
mkdir deploy/pt/ua/sd/ropegame/common/enums
mkdir deploy/pt/ua/sd/ropegame/common/communication
mkdir deploy/pt/ua/sd/ropegame/genrepository/
mkdir deploy/pt/ua/sd/ropegame/bench/
mkdir deploy/pt/ua/sd/ropegame/playground/
mkdir deploy/pt/ua/sd/ropegame/refereesite/
mkdir deploy/pt/ua/sd/ropegame/referee/
mkdir deploy/pt/ua/sd/ropegame/team/

mv src/pt/ua/sd/ropegame/common/interfaces/*.class deploy/pt/ua/sd/ropegame/common/interfaces
mv src/pt/ua/sd/ropegame/common/enums/*.class deploy/pt/ua/sd/ropegame/common/enums
mv src/pt/ua/sd/ropegame/common/communication/*.class deploy/pt/ua/sd/ropegame/common/communication
mv src/pt/ua/sd/ropegame/common/*.class deploy/pt/ua/sd/ropegame/common/
mv src/pt/ua/sd/ropegame/genrepository/*.class deploy/pt/ua/sd/ropegame/genrepository/
mv src/pt/ua/sd/ropegame/bench/*.class deploy/pt/ua/sd/ropegame/bench/
mv src/pt/ua/sd/ropegame/playground/*.class deploy/pt/ua/sd/ropegame/playground/
mv src/pt/ua/sd/ropegame/refereesite/*.class deploy/pt/ua/sd/ropegame/refereesite/
mv src/pt/ua/sd/ropegame/referee/*.class deploy/pt/ua/sd/ropegame/referee/
mv src/pt/ua/sd/ropegame/team/*.class deploy/pt/ua/sd/ropegame/team/