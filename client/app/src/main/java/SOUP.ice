module SOUP
{
    interface Player {
        string getMusicFileNameForMusicName(string musicName);
        string setMusic(string musicFileName);
        void play();
        void pause();
        void stop();
        void volumeUp();
        void volumeDown();
    }
}