# Dictionary Client
This repository contains the code for a client application that connects to a DICT.org server using TCP connection.

## Usage
The <code>CSDict.jar</code> file is created with the make command. 
``` shell
make run
```
or 
``` shell
make clean
make 
java -jar CSdict,jar -d
```
[-d] is optional which means debug mode, you don't need to run -d

Connect with the dict.org
``` shell
csdict> open dict.org 2628
```
Then enjoy the Dictionary server! <br><br>



| Application Command             | Description | Finished     |
| ------------------------        | ----------- |:-------------|
| open SERVER PORT                | Opens a new TCP/IP connection to an dictionary server. The server's name and the port number the server is listening  on are specified by the command's parameters. The server name can be either a domain name or an IP address in dotted form.  Both the SERVER and PORT values must be provided. This command is considered an unexpected command if it is given when a control connection is already open.| :heavy_check_mark:|
| dict                            | Retrieve and print the list of all the dictionaries the server supports. Each line will consist of a single word that is the the name of a dictionary followed by some information about the dictionary. You simply have to print each of these lines as returned by the server.        | :heavy_check_mark:|
| set DICTIONARY                  | Set the dictionary to retrieve subsequent definitions and/or matches from. The name of the dictionary is either the first word on one of the lines returned by the dict command or one of the required virtual databases defined in section 3.4 of the RFC. The default dictionary to use if the set command has not been given is "*".  Every time a connection to a dictionary server is made the dictionary to use is reset to "*".  Multiple set commands simply result in a new dictionary to search being set. Multiple set commands do not result in the building of a collection of dictionaries to search. This command does not have to verify that the specified DICTIONARY actually exists.       | :heavy_check_mark:|
| define WORD                     | Retrieve and print all the definitions for WORD. WORD is looked up in the dictionary or dictionaries as specified through the set command. For each definition returned print on a single line, and left justified, the name of the dictionary. On the next line start printing the returned definition. If the word can't be found and no definitions are returned You are to print  <br> \*\*\*\*No definition found\*\*\*\*  (Note: print exactly four "*"s with no space before and after the message.  True for all similar not found responses). <br><br> on a single line by itself and then do a match using the server's default matching strategy and print all of its responses. If there are no matches for the default matching strategy then you should print, on a line by itself<br>\*\*\*\*No matches found\*\*\*\*| :heavy_check_mark:|
| match WORD                       | Retrieve and print all the exact matches for WORD. WORD is looked up in the dictionary or dictionaries as specified through the set command. The responses from the server will consist of one or more lines of form<br><br>database word<br><br>If no matches are found print \*\*\*\*No matching word(s) found\*\*\*\* on a single line by itself. This command is most useful when you want to determine which dictionary a word occurs in so that you can retrieve a definition for that word from a particular dictionary.  | :heavy_check_mark:|
| prefixmatch WORD                 | Retrieve and print all the prefix matches. for WORD. WORD is looked up in the dictionary or dictionaries as specified through the set command. The responses from the server will consist of one or more lines of form database word If the word can't be found simply print<br><br>\*\*\*\*No matching word(s) found\*\*\*\* <br><br> on a single line by itself.| :heavy_check_mark:|
| close                            | After sending the appropriate command to the server and receiving a response, closes the established connection and enters a state where the next command expected is an open or quit. | :heavy_check_mark:|
| quit                             | Closes any established connection and exits the program. This command is valid at any time.| :heavy_check_mark:|


## Invalid Numbers
✅ 900 Invalid command. This is printed when the command entered by the user is not one of the accepted commands.  
✅ 901 Incorrect number of arguments. This is printed when the command is valid but the wrong number of arguments is provided. Note this could be the case if there are either too many or too few arguments.  
✅ 902 Invalid argument. This is printed when the command is valid, and has the proper number of arguments, but one or more the arguments are invalid. For example the second argument of open is a port number so a non-numeric value for the second argument would produce this error.  
✅ 903 Supplied command not expected at this time. This is printed when the command is valid, but not allowed at this time. For example, when the client first starts the only commands it can accept are open and quit. If it gets any other known command it would print this message. Note that the printing of this message takes priority over error messages 901 and 902. (i.e. even if errors 901 or 902 occur this message is the only one to print.).  
✅ 920 Control connection to xxx on port yyy failed to open. When an attempt to establish the connection can't be completed within a reasonable time (say 30 seconds), or the socket cannot be created, then print this message, replacing xxx and yyy with the hostname and port number of the dictionary server you are trying to establish the control connection to.  
✅ 925 Control connection I/O error, closing control connection.If at any point an error while attempting to read from, or write to, the open control connection occurs, this message is to printed, and the socket closed/destroyed. The client is then to go back to the state were it is expecting an open command.  
✅ 996 Too many command line options - Only -d is allowed.This is printed if there are too many command line options.  
✅ 997 Invalid command line option - Only -d is allowed.This is printed a command line option is not -d. The printing of error 996 takes priority over this message.  
✅ 998 Input error while reading commands, terminating. This error message is printed if an exception is thrown while the client is reading its commands (i.e standard input). After printing this message the client will terminate.  
✅ 999 Processing error. yyyy. If for some reason you detect an error that isn't described above, print this message and replace yyyy with some appropriate text that briefly describes the error.  
