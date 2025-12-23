# QParser
This REST API was meant to be useful for quantum software developers on 
the need of parsing between the two main quantum programming languages when developing quantum software products,
being simple, clean in architecture and easy to use.

> **⚠️ Under Active Development** - Working on improving user experience

### Getting Started

#### Option 1: Run with Maven 
```bash
cd qparser
mvn clean compile exec:java -Dexec.mainClass="Main"
```

#### Option 2: Build with Maven and Run with Java
```bash
cd qparser
mvn clean package
java -jar target/qparser-1.0.0.jar
```

App will be running by default at **http://localhost:8080**

### Simple Request

```bash
curl -X POST http://localhost:8080/api/parse \
  -H "Content-Type: application/json" \
  -d '{
    "script": "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg q[2];\ncreg c[2];\nh q[0];\ncx q[0], q[1];\nmeasure q[0] -> c[0];\nmeasure q[1] -> c[1];",
    "scriptType": "QASM"
  }'
```

#### If want to paste code directly:
Use the formatter `_format_script.py` as follows:
```bash
cd qparser/
python3 _format_script.py # Then just follow the instructions
```
It will return the desired type, parsed script.

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

