import os
import subprocess
import sys
import time
import atexit
import socket

class AppController:
    def __init__(self):
        self.api_process = None
        self.qparser_dir = "qparser"
        self.default_port = 3000
        
    def run_command(self, command, cwd=None, shell=True):
        try:
            print(f"\n{'='*60}")
            print(f"Running: {command}")
            print(f"{'='*60}\n")
            result = subprocess.run(
                command,
                shell=shell,
                cwd=cwd,
                check=True,
                text=True,
                capture_output=False
            )
            return result
        except subprocess.CalledProcessError as e:
            print(f"\nError executing command: {command}")
            print(f"Exit code: {e.returncode}")
            sys.exit(1)
    
    def is_port_in_use(self, port):
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            return s.connect_ex(('localhost', port)) == 0
    
    def build_maven_project(self):
        print("\nBuilding Maven project...")
        if not os.path.exists(self.qparser_dir):
            print(f"Error: Directory '{self.qparser_dir}' not found!")
            sys.exit(1)
        self.run_command("mvn clean package", cwd=self.qparser_dir)
        print("\nMaven build completed.")
    
    def start_api_server(self):
        print("\nStarting API server...")
        if self.is_port_in_use(self.default_port):
            print(f"Warning: Port {self.default_port} is already in use!")
            response = input("Do you want to try using the existing server? (y/n): ")
            if response.lower() == 'y':
                print("Using existing server")
                return
            else:
                print("Cannot start server - port is in use")
                sys.exit(1)
        
        target_dir = os.path.join(self.qparser_dir, "target")
        jar_files = [f for f in os.listdir(target_dir) 
                     if f.endswith(".jar") 
                     and not f.endswith("-sources.jar")
                     and not f.startswith("original-")]
        
        if not jar_files:
            print("Error: No JAR file found in target directory!")
            sys.exit(1)
        jar_file = os.path.join(target_dir, jar_files[0])
        env = os.environ.copy()
        env['PORT'] = str(self.default_port)
        try:
            self.api_process = subprocess.Popen(
                ["java", "-jar", jar_file],
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                text=True,
                env=env
            )
            print(f"API server process started (PID: {self.api_process.pid})")
            time.sleep(5)
            atexit.register(self.cleanup)
        except Exception as e:
            print(f"Error starting API server: {e}")
            sys.exit(1)
    
    def run_parse_code(self):
        print("\nRunning parse code script...")
        if not os.path.exists("qparser/_parse_code.py"):
            print("Error: qparser/_parse_code.py not found!")
            sys.exit(1)
        try:
            subprocess.run([sys.executable, "qparser/_parse_code.py"], check=True)
        except subprocess.CalledProcessError as e:
            print(f"Error running qparser/_parse_code.py: {e}")
            self.cleanup()
            sys.exit(1)
    
    def cleanup(self):
        if self.api_process and self.api_process.poll() is None:
            print("\nStopping API server...")
            self.api_process.terminate()
            try:
                self.api_process.wait(timeout=5)
            except subprocess.TimeoutExpired:
                print("Server didn't stop gracefully, forcing shutdown...")
                self.api_process.kill()
            print("API server stopped")
    
    def run(self):
        try:
            print("\n" + "="*60)
            print("WELCOME TO QPARSER")
            print("="*60)
            self.build_maven_project()
            self.start_api_server()
            self.run_parse_code()
            print("\n" + "="*60)
            print("ALL TASKS DONE!")
            print("="*60 + "\n")
        except KeyboardInterrupt:
            print("\n\nInterrupted by user")
        finally:
            self.cleanup()


if __name__ == "__main__":
    controller = AppController()
    controller.run()