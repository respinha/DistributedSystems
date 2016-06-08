#!/bin/bash

#TODO: check how to deploy register {RMI}
export SSHPASS=sddeploy08

USERNAME=sd0408

TEAM1="l040101-ws01.ua.pt"
TEAM2="l040101-ws02.ua.pt"

BENCH="l040101-ws03.ua.pt"
PLAYGROUND="l040101-ws04.ua.pt"
REFSITE="l040101-ws05.ua.pt"
GENREP="l040101-ws06.ua.pt"

REFEREE="l040101-ws08.ua.pt"

RMI="l040101-ws07.ua.pt"

compileAll() {

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
    javac -cp src src/pt/ua/sd/ropegame/registry/*.java
}

compileAll


zipAndDeploy () {

    mkdir $1/

    mkdir $1/pt/
    mkdir $1/pt/ua/
    mkdir $1/pt/ua/sd/
    mkdir $1/pt/ua/sd/ropegame/
    mkdir $1/pt/ua/sd/ropegame/common/
    mkdir $1/pt/ua/sd/ropegame/common/interfaces
    mkdir $1/pt/ua/sd/ropegame/common/enums
    mkdir $1/pt/ua/sd/ropegame/common/communication
    mkdir $1/pt/ua/sd/ropegame/$2/

    cp src/pt/ua/sd/ropegame/common/interfaces/*.class $1/pt/ua/sd/ropegame/common/interfaces
    cp src/pt/ua/sd/ropegame/common/enums/*.class $1/pt/ua/sd/ropegame/common/enums
    cp src/pt/ua/sd/ropegame/common/communication/*.class $1/pt/ua/sd/ropegame/common/communication
    cp src/pt/ua/sd/ropegame/common/*.class $1/pt/ua/sd/ropegame/common/
    cp src/pt/ua/sd/ropegame/$2/*.class $1/pt/ua/sd/ropegame/$2/
cp configs.xml $1/
cp java.policy $1/
cp $1.sh $1/
    zip -r $1.zip $1

    sshpass -e sftp -oBatchMode=no -b - ${USERNAME}@$3 << !
        put $1.zip
        bye
!
	sshpass -e ssh -oBatchMode=no ${USERNAME}@$3 << !
	mkdir deploy
	unzip $1.zip -d deploy/
!
	
}

zipAndDeploy registry registry ${RMI}
zipAndDeploy genrep genrepository ${GENREP}
zipAndDeploy bench bench ${BENCH}
zipAndDeploy playground playground ${PLAYGROUND}
zipAndDeploy refereesite refereesite ${REFSITE}
zipAndDeploy team team ${TEAM1}
zipAndDeploy team team ${TEAM2}
zipAndDeploy referee referee ${REFEREE}

