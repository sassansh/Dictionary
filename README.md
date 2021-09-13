<!-- PROJECT LOGO -->
<br />
<p align="center">
 <a href="https://github.com/sassansh/Dictionary">
    <img src="/images/logo.svg" alt="Logo" width="80" height="80">
  </a>
  <h2 align="center">A Dictionary Client</h2>

  <p align="center">
     A GUI for subset of the client side of the DICT protocol, described in RFC 2229. Built as a group programming assignment for UBC CPSC 317 (Internet Computing).
    <br />
    <br />
  </p>
</p>

![Assignment Question](/images/interface.png)

## Table of Contents

- [Technology Stack 🛠️](#technology-stack-)
- [Prerequisites 🍪](#prerequisites-)
- [Setup 🔧](#setup-)
- [Assignment Description 📚](#assignment-description-)
- [Team ‎😃](#team-)

## Technology Stack 🛠️

[Java](https://www.java.com/en/)

## Prerequisites 🍪

You should have [JDK 10](https://www.oracle.com/ca-en/java/technologies/java-archive-javase10-downloads.html), [IntelliJ IDEA](https://www.jetbrains.com/idea/) and [Git](https://git-scm.com/) installed on your PC.

## Setup 🔧

1. Clone the repo using:

   ```bash
     git clone https://github.com/sassansh/Dictionary.git
   ```

2. Open the project in IntelliJ.

3. Top open the GUI, Run: `ca.ubc.cs317.dict.ui.DictionaryMain`

## Assignment Description 📚

### Special Note

Although this assignment's autograder tests the most common input issues and produces an initial score, this score is going to be overwritten by a manual review by the course staff. This manual review will ensure your code works not only based on the simple cases listed in the resulting tests, but also for other considerations listed in the RFC and for additional tests. TAs will also review your code quality in terms of clarity and use of comments.

### Assignment Overview

In this assignment you will use the Java socket related classes to create a dictionary client. Your program is to provide some basic dictionary functionality through a simple graphical interface. You will implement a subset of the client side of the DICT protocol, described in RFC 2229, which can be found here: <https://tools.ietf.org/html/rfc2229>.

To start your assignment, download the file [Dictionary.zip](https://ca.prairielearn.com/pl/course_instance/2347/instance_question/9066718/clientFilesQuestion/Dictionary.zip). This file contains a directory called `Dictionary`which can be imported into IDEs like IntelliJ or Eclipse to develop your code.

The file above contains a skeleton code that provides a user-interface for the functionality you are to implement. The interface, however, does not actually establish any connection or transfer any data. Your job is to implement the connection establishment and data transfer for this application. More specifically, you will need to implement the code that performs each of the following tasks:

- Establish a connection with a DICT server, and receive the initial welcome message.
- Finish a connection with a DICT server by sending a final QUIT message, receiving its reply, and closing the socket connection.
- Requesting, receiving, parsing and returning a list of databases used in the server. Each database corresponds to one dictionary that can be used to retrieve definitions from. Examples include one or more regular English dictionaries, a Thesaurus, an English-French dictionary, a dictionary of technical terms, a dictionary of acronyms, etc. In the interface, the user will have the option of selecting a specific database, or all databases.
- Requesting, receiving, parsing and returning a list of matching strategies supported by the server. The protocol allows a client to retrieve a list of matches (suggestions) based on a keyword, and the strategy is used to indicate how these keywords are used to present actual dictionary entries. Examples include prefix matches (all words that start with a prefix), regular expressions, entries with similar sounding words, etc. In the interface, the user will have the option of selecting a specific strategy for matching, with prefix match being the default.
- Requesting, receiving, parsing and returning a list of matches based on a keyword, a matching strategy and a database. This list of matches will be used in the interface to suggest entries as they type a word.
- Requesting, receiving, parsing and returning a list of definitions for a word, based on a database. Each definition will correspond to the word, a database, and the definition itself, which may contain several lines. All definitions are listed in the interface in the order in which they are received.

Remember, you are only required to implement a subset of the protocol, so some of the material in the references goes beyond what you need. Keep in mind that the RFC describes the data (protocol) exchanges between the DICT client (i.e., what you are writing) and a DICT server.

All the functionality listed above is based on the implementation of the constructor and methods of the class `ca.ubc.cs317.dict.net.DictionaryConnection`, available in the provided code. This is the only file you are allowed to change.

### Implementation Constraints and Suggestions

You are strongly encouraged to test the connectivity and message formats of the DICT protocol using tools like netcat or telnet. Tutorial 1 discusses some of these tools.

Don't try to implement this assignment all at once. Instead, incrementally build and simultaneously test the solution. You are strongly encouraged to use the order listed above. Doing so will allow you to test your implementation in a progressive manner, since some of the items depend on each other (e.g., you need to obtain the list of matching strategies before you can request for matches). A suggested strategy is to:

- Read the RFC to understand how the protocol works. Use netcat or telnet to test it and see it in action.
- Establish a connection with the server.
- Implement the features in the order listed above.

You are not expected to implement any DICT command that is not listed, but you may do so if you want. Note that, since the user interface does not provide support for most of the additional commands available in the protocol, you may be required to change the UI to see these new features in action. Should you do so, it is your responsibility to ensure that, should the UI be changed back to its original version, your code continues to work as intended originally.

Some commands and replies in DICT may contain more than one word. Your code must support such words both when reading replies (e.g., when retrieving matches) and when sending commands (e.g., when requesting a definition). Note that these words are presented with quotes by the assignment; these quotes are not part of the keyword, and must not be included when returning their values. Note that a few classes are provided to help you handle status messages (class `Status`) and parsing lines into tokens (class `DictStringParser`). You are encouraged to use these classes as you see fit.

Some replies in DICT may contain a sequence of lines terminated by a line containing only a period (`.`) symbol. Note that this period is not part of the data, and must not be included in your data processing. For example, a definition that ends with such a line should not include that line as part of the definition itself.

For testing purposes you can connect to any DICT server, like dict.org. On some machines you might be able to run your own copy of a server with software like [JDictD](http://www.informatik.uni-leipzig.de/~duc/Java/JDictd/) for additional testing, but this is not required.

You may discuss this assignment with your classmates, but you must do your work only with your partner. You are not allowed to show your code, even for discussion or explanatory purposes, to other classmates.

Style and comments are part of the evaluation, so keep your code clear, clean and well-documented.

You may include, at the top of your `DictionaryConnection.java` file, comments containing any information you would like to convey to the TAs about your assignment. For example maybe you didn't get some part of the assignment working, or it works in some circumstances but not others.

## Team ‎😃

Sassan Shokoohi - [GitHub](https://github.com/sassansh) - [LinkedIn](https://www.linkedin.com/in/sassanshokoohi/) - [Personal Website](https://sassanshokoohi.ca)

Lana Kashino - [GitHub](https://github.com/lanakashino) - [LinkedIn](https://www.linkedin.com/in/lanakashino/) - [Personal Website](lanakashino.com)

Project Link: [https://github.com/sassansh/Dictionary](https://github.com/sassansh/Dictionary)

[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/sassanshokoohi/
