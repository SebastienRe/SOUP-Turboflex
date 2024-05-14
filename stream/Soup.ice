module Soup
{
    struct Song{
        int id;
        string title;
        string author;
        string extension;
        string type;
        float accuracy;
    }

    sequence<Song> Songs;
    sequence<string> strings;

    interface MusicLibrary
    {
        int runCommand(strings command);
    }
}