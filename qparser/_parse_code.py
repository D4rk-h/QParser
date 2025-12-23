import json
import requests

def get_multiline_input():
    print("\nPaste your code (Ctrl+D when done):")
    lines = []
    try:
        while True:
            lines.append(input())
    except EOFError:
        pass
    return "\n".join(lines)

def send_request(url: str, data_dict: dict):
    headers = {"Content-Type": "application/json"}
    response = requests.post(url, headers=headers, json=data_dict)
    return response.json()

def pretty_print_script_text(response):
    try:
        parsed_script = response.get("parsedScript")
        lines = [line for line in parsed_script.split("\n")]
        pretty_script = "\n".join(lines)
        return pretty_script
    except json.JSONDecodeError:
        return response

code = get_multiline_input()
escaped_code = json.dumps(code)
script_json = escaped_code
script_type = "QASM" if "QASM" in escaped_code else "QISKIT"
desired_type = "QISKIT" if script_type == "QASM" else "QASM"
print(f"\nDetected script type as {script_type}")
port = int(input("Default port at 3000, type any other if is preferred (format: NNNN) else press Enter: ") or 3000)

url = f"http://localhost:{port}/api/parse"
data_dict = {
    "script": json.loads(script_json),
    "scriptType": script_type,
    "desiredType": desired_type
}

curl_command = f'curl -X POST {url} -H "Content-Type: application/json" -d \'{json.dumps(data_dict)}\''

print("\n\nDo you want me to send the request to the server(y)? or prefer a copy/paste curl command(n)?: ", end="")
if input().lower() == 'y':
    try:
        response = send_request(url, data_dict)
        print("\nRequest sent successfully!")
        print(f'\nResponse from server:\n{json.dumps(response, indent=2)}')
        print(f"\nHere is your code: \n\n{pretty_print_script_text(response)}\n")
    except Exception as e:
        print(f"Error - Make sure server is up: {e}")
else:
    print(f'\nCopy/paste curl command:\n{curl_command}')
    print("\n\nDone!")