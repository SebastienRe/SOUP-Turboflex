from flask import Flask
from TalManager import TalManager

app = Flask(__name__)
talManager = TalManager()


ACTIONS = {
    "jouer" : ["commencer", "jouer", "lancer", "démarrer", "joue", "lance", "démarre", "commence"],
    "pause" : ["stop","pause", "arrêter", "suspendre", "eteindre", "couper", "arreter", "stopper", "pauser", "eteint"],
    "relance" : ["recommence", "reprendre", "relancer", "rejouer", "relance", "reprend", "continue"],
    "baisser" : ["baisser", "diminuer", "réduire", "abaisser", "baisse", "diminue", "réduit", "abaisse"],
    "augmenter" : ["augmenter", "hausser", "monter", "relever", "augmente", "hausse", "monte", "relève"]
}

MUSIQUES = [
    "Bohemian Rhapsody", "Hotel California", "Stairway to Heaven", "Imagine", 
    "Smells Like Teen Spirit", "Like a Rolling Stone", "Hey Jude", "Sweet Child O'Mine", 
    "Billie Jean", "Light My Fire", "Comfortably Numb", "Purple Haze", "A Day in the Life", 
    "Whole Lotta Love", "Layla", "Sympathy for the Devil", "London Calling", "Heart of Gold", 
    "Wish You Were Here", "Free Bird", "All Along the Watchtower", "Born to Run", "Superstition", 
    "Purple Rain", "Let's Stay Together"
]

@app.route('/')
def formulaire():
    return "Veuillez saisir une action dans l'URL."

@app.route('/<text>')
def text_to_action(text):
    action, word = talManager.get_the_most_similar_action(text, ACTIONS)
    if action == "jouer" and word is not None:
        text = text.replace(word, "").strip() # On retire le mot trouvé de la phrase
        music = talManager.get_the_most_similar_music(text, MUSIQUES)
        if music is not None:
            return [action, music]
        else :
            return [action]
    else :
        return [action]

if __name__ == '__main__':
    app.run(debug=True, port=5000)