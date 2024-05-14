module Soup
{
    sequence<byte> songdatas;
    
    struct Song{
        int id;
        string title;
        string author;
        string extension;
        string type;
        float accuracy;
    }

    sequence<Song> Songs;

    interface MusicLibrary
    {
        Song addSong(string title, string author, string type, string extension); // returns the id of the song
        void addSongData(Song song, songdatas data, bool finish); // returns the id of the song
        void updateSong(Song song, bool reset);
        void removeSong(Song song);
        Songs searchWithText(string text);

        int playSong(Song song); //returns the port number
        void stopSong(int port); //stops the song playing on the port
    }
}