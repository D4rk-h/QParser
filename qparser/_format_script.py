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

def send_parse_request(code: str, script_type:str, desired_type: str, port=8080):
    url = f'http://localhost:{port}/api/parse'
    headers = {'Content-Type': 'application/json'}
    data = {
        "script": f"{code}",
        "scriptType": f"{script_type}",
        "desiredType": f"{desired_type}"
    }
    response = requests.post(url, headers=headers, json=data)
    return response.json()

code = get_multiline_input()
escaped_code = json.dumps(code)
print(f'\n\nPaste this into your curl command:\n{escaped_code}')

print("\nDo you want the fully formatted json request? (y/n): ", end="")
if input().lower() == 'y':
    script_json = escaped_code
    script_type = "QASM" if "QASM" in escaped_code else "QISKIT"
    desired_type = "QISKIT" if script_type == "QASM" else "QASM"
    print(f"\nDetected script type as {script_type}")
    port = int(input("Introduce a port if other is preferred (default 8080) else press Enter: ") or 8080)
    full_json_request = "\n\n" + "curl -X POST http://localhost:%d/api/parse \ "+"""\n  -H "Content-Type: application/json" \ """+"""\n  -d '{"""+"""\n      "script": %s,"""+"""\n      "scriptType": %s,"""+"""\n      "desiredType": %s"""+"""\n   }'"""
    print(full_json_request % (port, script_json, script_type, desired_type))
    print("\n\nDo you want me to sent the request to the server? (y/n): ", end="")
    if input().lower() == 'y':
        try:
            script_type_request_format = f"{script_type}"
            desired_type_request_format = f"{desired_type}"
            response = send_parse_request(script_json, script_type_request_format, desired_type_request_format, port)
            print(f'\nResponse from server:\n{json.dumps(response, indent=2)}')
        except Exception as e:
            print(f"Error - Make sure server is up: {e}")
    else:
        print(f'\nFull request:\n{full_json_request % (port, script_json, script_type, desired_type)}')
        print(f"\n\nPaste this into your terminal to run the request. Ensure your server is running on port {port}.")
else:
    print("\n   Exiting without full request.")