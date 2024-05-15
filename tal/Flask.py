from flask import Flask
from TalManager import TalManager

app = Flask(__name__)
talManager = TalManager()


ACTIONS = {
    "jouer" : ["commencer", "jouer", "lancer", "démarrer", "joue", "jou", "lance", "démarre", "commence"],
    "pause" : ["stop","pause", "arrêter", "suspendre", "eteindre", "couper", "arreter", "stopper", "pauser", "eteint"],
    "relance" : ["recommence", "reprendre", "relancer", "rejouer", "relance", "reprend", "continue"],
    "baisser" : ["baisser", "diminuer", "réduire", "abaisser", "baisse", "diminue", "réduit", "abaisse"],
    "augmenter" : ["augmenter", "hausser", "monter", "relever", "augmente", "hausse", "monte", "relève"]
}

MUSIQUES = [
    "Savoir Aimer", "L'aventurier", "Je marche seul", "La Vache"
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
            print([action, music])
            return [action, music]
        else :
            print([action])
            return [action]
    else :
        print([action])
        return [action]

if __name__ == '__main__':
    app.run(debug=True, port=5001)