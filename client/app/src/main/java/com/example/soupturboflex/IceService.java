package com.example.soupturboflex;

import com.zeroc.Ice.Communicator;
import Soup.*;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class IceService {

    private static Communicator communicator = null;
    private static MusicLibraryPrx player = null;
    private static Server server;
    private static int cacheport = 0;

    public static void init() {
        server = new Server("all", "127.0.0.1", 15000, "ws");

        communicator = Util.initialize();
        System.out.println("Communicator initialized : " + communicator);
        ObjectPrx base = communicator.stringToProxy("MusicLibrary:"
                + server.getProtocol() + " -h "
                + server.getHost() + " -p "
                + server.getPort());
        System.out.println("Base initialized : " + base);

        player = MusicLibraryPrx.checkedCast(base);
        System.out.println("Player initialized : " + player);

    }

    public static List<Song> getSongs() {
        Song[] songs = player.searchWithText("");
        List<Song> songList = new ArrayList<>();
        for (Song song : songs) {
            songList.add(song);
        }
        return songList;
    }

    public static int play(ActionCouple actionCouple, Song song) {
        if (actionCouple.action.equals("jouer")) {
            if (cacheport != 0) {
                player.stopSong(cacheport);
            }
            if (song == null) {
                return -1;
            }
            cacheport = player.playSong(song);
            return cacheport;
        }
        return -1;
    }

}