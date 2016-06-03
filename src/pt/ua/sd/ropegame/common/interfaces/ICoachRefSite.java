package pt.ua.sd.ropegame.common.interfaces;

import java.rmi.Remote;

public interface ICoachRefSite extends Remote {

    void informReferee(int teamID);

}
