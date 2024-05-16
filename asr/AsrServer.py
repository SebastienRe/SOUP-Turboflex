import sys
import whisper
from openai import OpenAI
from flask import Flask
from flask import request
import os
import uuid
import yaml

app = Flask(__name__)

def transcribe_audio(file_path):
    api_key = ""
    with open('config.yaml', 'r') as config_file:
        config = yaml.safe_load(config_file)

        api_key = config['api_key']
    
    client = OpenAI(api_key=api_key)

    audio_file = open(file_path, "rb")
    transcript = client.audio.transcriptions.create(
        model="whisper-1",
        file=audio_file,
        language="fr"
    )

    audio_file.close()
    
    return transcript.text

@app.route('/transcribe', methods=['POST'])
def transcribe():
    audio_file = request.files['audio']
    
    extension = audio_file.filename.split('.')[-1]
    unique_id = str(uuid.uuid4())
    filename = "./tmp/" + unique_id + "." + extension

    file = open(filename, 'wb')
    try:
        file.write(audio_file.read())
    finally:
        print("File closed")
        file.close()
    
    transcription = transcribe_audio(filename)

    print("Removing audio file")
    os.remove(filename)

    print(transcription)
    
    return transcription

if __name__ == '__main__':
    app.run(debug=True, port=5000)