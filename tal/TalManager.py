import os
from gensim.models import KeyedVectors
import unicodedata
import re
debug = False

class TalManager:
    def __init__(self, debug_param=False):
        debug = debug_param
        path = os.path.join(os.path.dirname(__file__), "models", "frWac_non_lem_no_postag_no_phrase_200_skip_cut100.bin")
        if not os.path.exists(path):
            raise FileNotFoundError(f"Le fichier {path} est introuvable.")
        self.model_gensim = KeyedVectors.load_word2vec_format(path, binary=True)
    
    def get_the_most_similar_action(self, text, dict_of_possibilities):
        similarities = { action : 0 for action in dict_of_possibilities }
        the_word = None
        plus_haut_taux = 0

        for word in text.split(" "): # On découpe le texte en mots
            for action, possibilities in dict_of_possibilities.items(): # On parcourt les actions et leurs synonymes
                for possibility in possibilities: # On parcourt les synonymes
                    try:
                        taux_de_similitude = self.model_gensim.similarity(self.normalize_text(word), self.normalize_text(possibility)) 
                            # On calcule la similarité entre le mot et le synonyme
                        if taux_de_similitude > plus_haut_taux: # Si le taux de similarité est plus élevé que le précédent
                            plus_haut_taux = taux_de_similitude # On met à jour le taux de similarité le plus élevé
                            the_word = word # On met à jour le mot
                        similarities[action] = max(similarities[action], taux_de_similitude) # On met à jour le taux de similarité
                    except KeyError:
                        pass

        return max(similarities, key=similarities.get), the_word

    def get_the_most_similar_music(self, text, list_of_possibilities):
        similarities = { music : 1 for music in list_of_possibilities }
        
        for music in list_of_possibilities:
            debut_musique = music.split(" ")[0]
            words = text.split(" ")
            i = self.get_index_most_similar(debut_musique, words)
            if i != -1:
                sentence = " ".join(words[i:i+len(music.split(" "))])
                print(f"'{sentence}', '{music}', '{self.model_gensim.wmdistance(self.normalize_text(sentence), self.normalize_text(music))}'") if debug else None
                similarities[music] = self.model_gensim.wmdistance(self.normalize_text(sentence), self.normalize_text(music))                

        if min(similarities.values()) > 0.3:
            return None
        return min(similarities, key=similarities.get)
    
    def get_index_most_similar(self, word, words):
        meilleure_distance = 1
        index = -1
        for i in range(len(words)):
            try:
                distance = self.model_gensim.wmdistance(self.normalize_text(words[i]), self.normalize_text(word))
                if distance < meilleure_distance:
                    meilleure_distance = distance
                    index = i
            except KeyError:
                pass
        print(f"'{word}', '{words[index]}', '{meilleure_distance}'") if debug else None
        return index
        
        
    def normalize_text(self, text):
        text = text.lower() # On met le texte en minuscule
        text = unicodedata.normalize('NFD', text).encode('ascii', 'ignore').decode("utf-8") # On retire les accents
        text = re.sub(r'\W+', ' ', text) # On retire les caractères spéciaux
        return text