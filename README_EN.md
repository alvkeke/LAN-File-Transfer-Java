# File-Transfer-Java

## Introduce

This is a new version of File Transfer in LAN, and it is consisted of frontend and backend.
This is the repository for the backend of the File Transfer.

If you need a frontend tool, please move to 
* [CLI tool in Linux](https://github.com/alvkeke/LAN-File-Transfer-Frontend)
* [General UI tool](https://github.com/alvkeke/LAN-File-Transfer-Frontend-UI)

## Usage

Some property(ports, device name) can be change by a configuration file, 
if configuration file was not assign, program will find the default configuration named `config` and located in
the same directory as where the program located in.

you can assign the configuration file by pass the command parameter `-c <config>`

The configuration file is composed of some lines with format `key = value`, 
and it allows user write blank lines or comments. 
Lines start with `#` are comments.

Here are valid `key` at present:
* `recv port` : the port(TCP) that receiving module listen.
* `scan port` : the port(UDP) that scanning module listen.
* `ctrl port` : the port(TCP) that controlling module listen.
* `device name` : the name will be sent to other devices.
* `recv path` : the path to save received files.

## Program Structure

This program has 4 modules:

* Send Module
* Recv Module
* Scan Module
* Ctrl Module

Each of these modules was an individual `Thread` , and it performs its 
specific function, and these functions consist of the Program.

### Send Module

1. This module handle the file sending function, it uses a `BlockingQueue` to 
make sure Thread will be pending while there is no files need to be sent.
2. `BlockingQueue` contain the sending tasks, and tasks need contain some info:
    1. path to file need to be sent
    2. device information like ip/port.
3. Module uses short connection for data transfer, it will create a connection
for each task;
   
### Recv Module

This module listen a TCP port continuously, this port can be assigned by configuration
file. This module leverages a simple multiple-thread TCP server structure.

### Scan Module

This part perform a function to discover valid device in LAN, this port can
get information:

* Device name
* IP address
* Receiving Port

**This part leverages UDP to accomplish data broadcast.**

**The UDP port need to be unified in LAN.**

### Ctrl Module

This part handle the data sent from frontend, and return data to frontend.

## Protocol

### File Transfer Protocol

This protocol basis on TCP, so it is no need to ensure the reliability again.

Here is the data format, all integers are order in little-end. 

| field | length | comment |
|:---:|:---:|:---|
| datalen | 8 bytes | length of file |
| namelen | 4 bytes | length of file name |
| name | n1 | file name, length of it was given by `namelen` |
| data | n2 | file data, length of it was given by `datalen` |

### Control Protocol

This Protocol was consisted of commands, and all of these
commands are in text format. Here are available commands:

* `scan`
    * scan available devices in LAN, this command will not return devices' information.
    * has return value: `success` or `failed`
* `get <data>`
    * fetch data from LFTP backend. available `data` items:
        * `dev-ol`: get online devices store in LFTP backend.
            * format: `name1 ip1 port1|name2 ip2 port2|...\n`
            * each item contain `name`/`ip`/`port` three value, each value was divided by space(`" "`)
            * data end with a return `"\n"`
        * `save-path`: path for saving files.
* `set <conf> <value>`
    * set <conf> to <value>, available `conf` items:
        * `save-path` path for save recv files.
    * return `success` or `failed`
* `send <dev-name> <file-path>`
    * send file `<file-path>` to device named `<dev-name>`
    * return `success` or `failed`
* `send-addr <ip> <port> <file-path>`
    * send file `<file-path>` to `<ip>:<port>`
    * return `success` or `failed`
* `exit`
    * close the connection, no return.
    
### Scan Protocol

This part describe the communication flow to discover valid devices.

1. requester broadcast a package has data `"SCAN"`
2. valid devices return package to requester, with format:
    1. recv port[4 bytes], little end
    2. device name, max 36 bytes.
3. all devices in LAN finish step (2)

    


