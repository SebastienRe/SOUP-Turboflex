package com.example.soupturboflex;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import com.example.soupturboflex.SOUP.PlayerPrx;

public class IceService {
    public static PlayerPrx getPlayer() {
        try {
            Communicator communicator = Util.initialize();
            ObjectPrx base = communicator.stringToProxy("Player:default -p 10000");
            return PlayerPrx.checkedCast(base);
        } catch (Exception e) {
            System.err.println("Error retrieving player : " + e.getMessage());
        }
        return null;
    }
}