import json

def get_multiline_input():
    print("Paste your code (Ctrl+D when done):")
    lines = []
    try:
        while True:
            lines.append(input())
    except EOFError:
        pass
    return "\n".join(lines)

code = get_multiline_input()
escaped_code = json.dumps(code)
print(f'\nPaste this into your curl command:\n{escaped_code}')