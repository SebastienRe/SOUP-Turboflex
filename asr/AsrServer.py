import sys
import whisper
from flask import Flask
from flask import request
import os
import uuid

app = Flask(__name__)

def transcribe_audio(file_path):
    model = whisper.load_model("small")
    result = model.transcribe(file_path)

    return result['text']

@app.route('/transcribe', methods=['POST'])
def transcribe():
    print()
    audio_file = request.files['audio']
    
    extension = audio_file.filename.split('.')[-1]
    unique_id = str(uuid.uuid4())
    filename = "./tmp/" + unique_id + "." + extension

    file = open(filename, 'wb')
    try:
        file.write(audio_file.read())
    finally:
        file.close()
    
    transcription = transcribe_audio(filename)

    os.remove(filename)

    return transcription