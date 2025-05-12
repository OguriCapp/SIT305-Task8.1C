from flask import Flask, request, Response
import requests
import argparse

app = Flask(__name__)

# Ollama API endpoint and model configuration
OLLAMA_API_URL = "http://localhost:11434/api/generate"
MODEL = "llama3.2:latest"

def check_ollama_server():
    """Check if the Ollama server is running."""
    try:
        response = requests.get("http://localhost:11434")
        if response.status_code == 200:
            print("Ollama server is running.")
            return True
        else:
            print("Ollama server is not responding correctly.")
            return False
    except requests.ConnectionError:
        print("Error: Could not connect to Ollama server. Ensure it is running with 'ollama serve'.")
        return False

@app.route('/')
def index():
    return "Welcome to the Ollama Chatbot API!"

@app.route('/chat', methods=['POST'])
def chat():
    # Get userMessage from form data or raw body
    user_message = request.form.get('userMessage') or request.get_data(as_text=True).strip()

    # Validate userMessage
    if not user_message:
        return Response("Error: userMessage cannot be empty", status=400, mimetype='text/plain')

    # Print received request
    print("\nReceived Request:")
    print(f"userMessage: {user_message}")

    # Prepare payload for Ollama API
    payload = {
        "model": MODEL,
        "prompt": user_message,
        "stream": False,
        "options": {
            "temperature": 0.6,
            "top_p": 0.85,
            "num_predict": 200
        }
    }

    # Send request to Ollama API
    try:
        response = requests.post(OLLAMA_API_URL, json=payload)
        print(f"Ollama Response Status: {response.status_code}")
        print(f"Ollama Response Text: {response.text}")
        response.raise_for_status()
        result = response.json()
        raw_output = result.get("response", "").strip()
    except requests.RequestException as e:
        print(f"Error during Ollama API call: {str(e)}")
        raw_output = ""

    # Print raw output
    print(f"Raw Model Output: {raw_output}")

    # Use raw output as response
    response = raw_output

    # Simplified validation: only check for empty or whitespace responses
    if not response or response.isspace():
        response = f"Sorry, I couldn't provide a relevant answer to: '{user_message}'. Please rephrase."

    # Print generated response
    print(f"Generated Response: {response}\n")

    # Return plain text response;
    return Response(response, mimetype='text/plain')

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--port', type=int, default=5000, help='Specify the port number')
    args = parser.parse_args()

    port_num = args.port
    if check_ollama_server():
        print(f"App running on port {port_num}")
        app.run(host='0.0.0.0', port=port_num)
    else:
        print("Exiting due to Ollama server unavailability.")