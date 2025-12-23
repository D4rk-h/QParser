import json

def get_multiline_input():
    print("\nPaste your code (Ctrl+D when done):")
    lines = []
    try:
        while True:
            lines.append(input())
    except EOFError:
        pass
    return "\n".join(lines)

code = get_multiline_input()
escaped_code = json.dumps(code)
print(f'\n\nPaste this into your curl command:\n{escaped_code}')

print("\n Do you want the full request formatted? (y/n): ", end="")
if input().lower() == 'y':
    script_json = escaped_code
    script_type = "\"QASM\"" if "QASM" in escaped_code else "\"QISKIT\""
    port = int(input("Introduce a port if other is preferred (default 8080) else press Enter: ") or 8080)
    full_json_request = "\n\n" + "curl -X POST http://localhost:%d/api/parse \ "+"""\n  -H "Content-Type: application/json" \ """+"""\n  -d '{"""+"""\n      "script:" %s,"""+"""\n      "scriptType": %s"""+"""\n   }'"""
    print(f'\nFull request:\n{full_json_request % (port, script_json, script_type)}')
    print(f"\n\nPaste this into your terminal to run the request. Ensure your server is running on port {port}.")
else:
    print("\n   Exiting without full request.")