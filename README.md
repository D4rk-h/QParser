# QParser
This REST API was meant to be useful for quantum software developers on 
the need of parsing between the two main quantum programming languages when developing quantum software products,
being fast, simple, clean in architecture and easy to use.

### Getting Started

#### Option 1: Run with a script
Use the controller `_app_controller.py` as follows (this is the most fast and easy way to use the tool):
```bash
git clone https://github.com/D4rk-h/QParser.git
cd QParser

python3 _app_controller.py # And follow the instructions
```

#### Option 2: Run with Maven 
```bash
cd qparser
mvn clean compile exec:java -Dexec.mainClass="Main"
```

#### Option 3: Build with Maven and Run with Java
```bash
cd qparser
mvn clean package
java -jar target/qparser-1.0.0.jar
```

App will be running by default at **http://localhost:3000**

### Request format

#### Manually
If manual form is preferred then `_parse_code.py` script will be needed to be executed manually with:
```bash
cd qparser
python3 _parse_code.py # And follow the instructions to let the script format the pasted code
```

After format the pasted code and run the main java class you can run manually this (with this format) curl request:
```bash
curl -X POST http://localhost:3000/api/parse \
  -H "Content-Type: application/json" \
  -d '{
    "script": "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg q[2];\ncreg c[2];\nh q[0];\ncx q[0], q[1];\nmeasure q[0] -> c[0];\nmeasure q[1] -> c[1];",
    "scriptType": "QASM",
    "desiredType:" "QISKIT"
  }'
```

# Endpoints

| Método | Endpoint | Description           |
|--------|----------|-----------------------|
| GET | `/health` | Verifies API state    |
| GET | `/api/version` | Version info          |
| GET | `/api/supported-types` | Lists supported types |
| POST | `/api/parse` | ---                   |

## Requirements

- Java 21+
- Maven 3.6+
- Python 3.9+ (libs: `requests`, `json`)

## Unsupported features

- Barrier
- Reset
- Conditional operations
- Phase disks

## Contributing
Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

## License

GPL-3.0 License - See [LICENSE](LICENSE) for more details.

