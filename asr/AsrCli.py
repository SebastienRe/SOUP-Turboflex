import sys
from openai import OpenAI
import yaml

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
    print(transcript.text)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Please provide the path to the audio file as a command-line argument.")
        sys.exit(1)

    audio_file_path = sys.argv[1]
    transcribe_audio(audio_file_path)