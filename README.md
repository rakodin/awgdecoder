# AmneziaWG decoder/encoder
Encode and decode AmneziaWG config file (from/to vpn:// to *.conf format).<br>
Create JSON dumps from configs.
## Usage
### Build instructions
You need a JDK version >= 17 and maven.
1. Checkout source from GitHub
2. Execute
```shell
mvn clean package
```
3. Executable jar is located in ```target/``` folder

### Running
```shell
java -jar target/awgdecoder-1.0-SNAPSHOT-run.jar
```
```text
AmneziaWG config file (en)(de)coder
Usage: awgdecoder <decode|encode> <options>
Options:
    decode:
      -i input file   : required (file in 'vpn://' format)
      -of <conf|dump> : optional output format:
               conf: awg config (default)
               dump: full AmneziaWG json dump
    Example:
        to get AmneziaWG config in awg conf format execute
        awgdecoder decode -i ./myConfig.vpn -f conf > myawg0.conf
    encode:
        -i input file   : required (AmneziaWG awg config)
        -d dns1:dns2    : optional (dns1 dns2 for vpn://). default: 1.1.1.1:1.0.0.1
        -of <conf|dump> : optional output format:
                 conf: AmneziaWG full config (vpn://...) (default)
                 dump: full AmneziaWG json dump
     Example:
        to convert AmneziaWG awg config into vpn:// link execute
        awgdecoder encode -f ./myawg0.conf -d 8.8.8.8:4.4.4.4 > myNewConfig.vpn
```