import vlc
import Ice
import sys
Ice.loadSlice('SOUP.ice')
import SOUP
import uuid

class PlayerI(SOUP.Player):
    def __init__(self, current=None):
        self.instance = vlc.Instance('--input-repeat=-1')
        self.player = None
        self.currentMusicFilePath = ''
        self.musicData = b''
        print('init')
    
    def getMusicFileNameForMusicName(self, musicName, current=None):
        if musicName == 'Savoir Aimer':
            return 'savoir_aimer.mp3'
        elif musicName == 'L\'aventurier':
            return 'l_aventurier.mp3'
        elif musicName == 'Je marche seul':
            return 'je_marche_seul.mp3'
        elif musicName == 'La Vache':
            return 'la_vache.mp3'
        else:
            return ''

    def setMusic(self, musicFileName, current=None):
        if self.player:
            self.stop()
        unique_id = str(uuid.uuid4())
        musicFileName = "./musiques/all/"+musicFileName
        output = 'sout=#transcode{vcodec=none,acodec=mp3,ab=128,channels=2,samplerate=44100}:http{mux=raw,dst=:12345/'+unique_id+'.mp3}'
        media = self.instance.media_new(musicFileName, output)
        player = self.instance.media_player_new()
        player.set_media(media)
        self.currentMusicFilePath = musicFileName
        print('music set : '+musicFileName)

        # Add player to dictionary of active players
        self.player = player

        return 'http://127.0.0.1:12345/'+unique_id+'.mp3'
    
    def play(self, current=None):
        self.player.play()
        print('play')

    def pause(self, current=None):
        self.player.pause()
        print('pause')

    def stop(self, current=None):
        if(self.player):
            self.player.stop()
        print('stop')
    
    def volumeUp(self, current=None):
        volume = self.player.audio_get_volume()
        self.player.audio_set_volume(volume+10)
        print('volume up')
    
    def volumeDown(self, current=None):
        volume = self.player.audio_get_volume()
        self.player.audio_set_volume(volume-10)
        print('volume down')

with Ice.initialize(sys.argv) as communicator:
    print('starting...')
    adapter = communicator.createObjectAdapterWithEndpoints("PlayerAdapter", "default -p 10000")
    player = PlayerI()
    adapter.add(player, communicator.stringToIdentity("Player"))
    adapter.activate()
    print('running...')
    communicator.waitForShutdown()
