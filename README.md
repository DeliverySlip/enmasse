# EnMasse
EnMasse allows clients to make bulk searches of their emails on the client side - minimizing the activity needed
on the server side. Users can input a query, and EnMasse will download a copy of all of their emails on the secure
messaging platform and then perform a search against all the data

# Prerequisites
* To compile the project you will need Java 8 installed

# Setup
Build the project by calling the following command from the project root
```$xslt
./gradlew mainJar     # *nix
./gradlew.bat mainJar # windows
```
This will build a fat jar with all dependencies within the `build/libs` folder.

# Usage
Note currently terminal mode has more features than GUI mode. Storing output in JSON
or CSV formats for example is only supported currently in terminal mode. The intention
of the GUI is to give a clean display of the output for the user and a non technical audience,
terminal mode is the preferred usage of EnMasse for technical and power users.

## GUI Mode
To start EnMass cd to the `build/libs` folder and then execute the following command
```$xslt
java -jar ./enmasse-1.0.0.jar
```
The GUI will then launch with the application. From here, fillout the form in the GUI
to use the application

## Terminal Mode
To use EnMasse in terminal mode make sure to use the `--NOGUI` flag with any and all commands
passed. This is the only flag that stops the GUI from rendering. navigate to the `build/libs`
folder and execute the following command
```$xslt
java -jar ./enmasse-1.0.0.jar --NOGUI --HELP
```
This will print out the help information on how to use the search

### Parameters
The main required parameters allow the client to login and apply a search. The option `-o` parameter allows the client
to specify the output format which can be either CONSOLE, JSON or CSV. By default this value is CONSOLE

| Parameter         | Required | Description                                                                   |
| ----------------- | -------- | ----------------------------------------------------------------------------- |
| -s                | Y        | Specify the service code to search                                            |
| -u                | Y        | Specify the username to login for the search                                  |
| -p                | Y        | Specify the password to login for the search                                  |
| -q                | Y        | Specify the search query string                                               |
| -o                | N        | Specify the output format of the data. Default is CONSOLE which prints to console. Otherwise JSON will print to a .json file and CSV will output to a .csv |


### Flags
Various flags control how EnMasse operates, whetherto load the GUI, handle caching and configure its searching
method:
 
| Flag              | Description                                                                              |
| ----------------- | ---------------------------------------------------------------------------------------- |
| --CACHERESULT     | Locally cache the searched messages to speed up future searches                          |
| --USECACHE        | Use cache data instead of the API to make search                                         |
| --CASESENSITIVE   | Search query is case sensitive                                                           |
| --ABSOLUTESEARCH  | Word matching to query must be absolute. partial matches will be skipped                 |
| --SEARCHRECIPIENTS| Check the search query for matches in the To, Cc, Bcc, and From fields                   |
| --SEARCHBODY      | Check the query for matches in the body of the message                                   |
| --SEARCHSUBJECT   | Check the query for matches in the subject of the message                                |
| --NOGUI           | Load EnMasse in terminal mode. Without this flag the GUI will load                       |
| --HELP            | Print this help information                                                              |

# Cache Usage
EnMasse has a simple caching mechanism where it serializes all of the emails it has fetched
during its last search into a local file named `enmasse.cache.dat`. This file is generated
or overwritten everytime the `--CACHEDATA` flag is passed in terminal or checked in the GUI.

By passing `--USECACHE` or checking the box in the GUI, EnMasse will read and deserialize
this file and use it to make its searches. With the `--USECACHE` flag or checkbox selected,
EnMasse will **only query the api if the following issues occur**:
* The service code does not match the service the cached emails belong to
* EnMasse fails to deserialize the `enmasse.cache.dat` file
* The `enmasse.cache.dat` file does not exist

In these cases, EnMasse runs as if the flag was not provided, as it cannot run without the
cache file while in this mode.

**Note that the cached data is not encrypted and thus stores your email information on your
local machine in serialized plaintext**. To mitigate unwanted access to these emails, either
do not use the `--CACHEDATA` parameter or delete the cache data when done with searches.

If you are having issues with the cache, one of the easiest solutions is to simply delete
the `enmasse.cache.dat` file and have EnMasse regnerate it. This can be done at anytime as
long as there is not an ongoing search with the `enmasse.cache.dat` file or the API

# Developer Notes

# TODO
* ~~Implement ability to store messages locally - allowing multiple searches to occur without re-downloading all
messages~~
* ~~Add search to include To, From, CC, BCC~~
* Add options to have exact match instead of only partial match
* ~~Add a GUI using TornadoFX so that this tool can be used by anyone and print outs can be well formatted~~
* ~~Add ability to direct output to JSON and CSV~~
