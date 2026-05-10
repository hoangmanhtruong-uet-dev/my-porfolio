import requests
import json
import sys

def solve_chemistry(api_key, reactants):
    url = f"https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent?key={api_key}"
    
    prompt = (
        f"You are a Chemistry Expert. The user provides reactants or a chemical equation. "
        f"If only reactants are provided, predict the products and balance the equation. "
        f"If a full equation is provided, balance it. "
        f"Format your response as a JSON object with two fields: "
        f"'balanced': the fully balanced equation using Unicode subscripts (e.g., H₂O, FeCl₂), "
        f"'explanation': a very brief explanation in Vietnamese (1-2 sentences). "
        f"Do not include any other text. Reactants: {reactants}"
    )

    payload = {
        "contents": [
            {
                "parts": [{"text": prompt}]
            }
        ]
    }
    
    headers = {'Content-Type': 'application/json'}
    
    try:
        response = requests.post(url, headers=headers, data=json.dumps(payload))
        if response.status_code == 200:
            data = response.json()
            text = data['candidates'][0]['content']['parts'][0]['text']
            # Clean markdown
            text = text.replace('```json', '').replace('```', '').strip()
            return text
        else:
            return json.dumps({
                "balanced": f"Error {response.status_code}",
                "explanation": response.text
            })
    except Exception as e:
        return json.dumps({
            "balanced": "System Error",
            "explanation": str(e)
        })

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print(json.dumps({"balanced": "Missing Args", "explanation": "Usage: python chem_ai.py <api_key> <reactants>"}))
    else:
        api_key = sys.argv[1]
        reactants = sys.argv[2]
        print(solve_chemistry(api_key, reactants))
