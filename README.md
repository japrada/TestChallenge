## 1. Test Challenge

The application allows to evaluate the level of knowledge of a group of users or a single user on a subject/topic by performing tests that run simultaneously on all connected systems. Users accumulate points according to the correctness and speed with which they submit their answers. During the execution of the test the system sends notifications of the partial results as users answer the questions. At the end of the test they can review the answers sent with their correction (incorrect answers are shown in red and  correct answers in green), the points obtained and their position in the ranking.

## 2. Description

The project implements a client-server application to chat and perform group tests in a gamified way by selecting the subject/topic, the level of difficulty and the type of questions, the number of questions of the test, and the time limit to answer. On one hand, it consists of a Java server that manages the communication between users, the messages interchanged between them and the server, and the execution of the tests. On the other hand, a Java Swing desktop client application with which users, after registering with an alias or nickname, can chat, launch tests to the group, review the answers sent and the results obtained according to their correctness.

Gamification is implemented by introducing a time limit for submitting the answers and a scoring scheme. The first user to send the correct answer receives 2 points; the rest of the users who answer correctly receive 1 point; those who answer incorrectly receive 0 points and those who do not send the answer receive -1 points. A user can submit only one answer to a question, so once submitted, it will remain on hold (maybe chatting) until the rest of the users answer or until the time limit expires, at which time the system will send the following question to all connected users. As the test runs, users receive notifications of partial results per question and at the end of the test, the total results for each user are summed up and the ranking is updated with the scores obtained.

If a user considers they cannot send the answer within the established time limit, they can request either a time extension or pause the execution of the test. If a user extends the time limit of a question, it is also extended for the rest of the users; similarly, if a user requests to pause a test, it is also paused for the rest of the users, giving them more time to answer. In this version no policies have been implemented to penalize users when they request to extend the number of seconds or pause the test execution (it is proposed as an improvement :-)). The options to pause/resume a test will only be available to those users who have not submitted a response yet. Any user can stop the test at any moment, but  the user who requests that will receive as many negative points as questions are pending to be sent by the server.

## 3. FAQs

### 3.1. Which use cases can this project cover?

The system can be used to evaluate the level of knowledge of a group of users on a subject/topic in different types of scenarios (to prepare a multiple-choice exam, to prepare self-assessments, etc.) establishing, in addition, a classification or ranking based on the speed and correctness of the answers submitted. In addition, as any user can upload new questions to the system, collaboration and teamwork are also encouraged.

In my case, I have used it to prepare English questions and do self-assessments, and also to do group tests with friends and family on this subject.

### 3.2. How does the application work at a high level?

On the one hand, the server application manages user registration/unregistration, test execution and client-server message interchange for each connected client. On the other hand, the client application presents the graphical user interface (GUI) and manages messages that are sent by the server asynchronously.

Messages of different types and content are interchanged between server and clients as serialized objects through a TCP connection.

### 3.3. How are questions created?

Questions are defined from a series of information fields in a JSON format file. Examples of the different types of questions that have been implemented for the subject/topic of English, which is, as we have mentioned, the area of knowledge that has motivated the development of this application, are provided along with the source code of the project.

### 3.4. Where are the questions stored?

In this version of the application the questions (JSON files) are stored in the server's file system, in one of the subdirectories of the base/root directory that is passed as a server startup parameter. Each subdirectory represents a subject/topic and its name is displayed in the client application on a drop-down list.

### 3.5. What types of questions can be defined?

The application allows you to define five types of questions depending on the type of answer:

- **Unica**: several options are presented and the user can select only one.

- **M?ltiple**: several options are presented and the user can select more than one.

- **Texto**: the user has to type the answer to be sent.

- **Emparejada**: several options are presented and for each option a drop-down list with the same list of values.

- **Multivalor**: several options are presented and for each option the drop-down list of values that can be different for each option.

### 3.6. Is it possible to display questions containing images?

Yes, in this version the image is displayed in the upper right corner of the panel containing the question. That is, a single image can be associated with a question, but not with each of the options of the question.

The image (.gif, .png) can be zoomed in by double-clicking on it and then resizing the pop-up window that is shown (Note: the popup-window showing the image is closed once the focus is lost or when you close it specifically).

### 3.7. Is it possible to display questions containing audios?

Yes, when a question has an audio file associated with it, it can be played by double-clicking on the mp3 icon that appears in the upper right corner of the panel displaying the question.

In this version only .mp3 file playback has been implemented. You can pause it or stop it, but you cannot go forward or backward. (Note: the popup-window playing the .mp3 file is closed once the focus is lost or when you close it specifically).

### 3.8. How are the questions of the test selected?

During the preparation of the test, since the definition of the questions and answers are in a JSON format file, the first thing that the test server does is to process all the files for the selected subject/topic and generate the questions from the information they contain. If the question has a multimedia file associated with it (an audio or an image), this must be stored in the subdirectory ?Multimedia? in the corresponding subject/topic folder.

Once the questions have been created, only the ones with the specified level are selected. Then, on the resulting subset, a new filter is applied to select only those that are of one of the selected types. Next, the final number of the questions of the test is set, being the minimum between the number of questions specified as a parameter and the number of questions that are actually stored in the subject/topic folder.

Finally, the set of selected questions is prepared by randomly selecting a question from the set of questions with the specified level and types; this process is repeated until the final number of the questions of the test is obtained.

### 3.9. Is the order of the options shown in the questions always the same?

No, by default when a question is sent to the connected users by the test server the options it contains are randomly reordered so that the order in which they are presented may change from one test execution to another. This is done to make it more difficult to memorize the answers.

This behavior can be modified by including the optional field "desordenar_opciones" in the question definition file with the value "false". In that case, when the question is submitted by the server, the options are not unsorted. This can be useful for example in those questions that have an audio associated with them, so that the options of the question are temporarily ordered according to the listening of the audio.

### 3.8. Why has the client been implemented using Java Swing?

The application has been conceived as a personal project, focused on functionality and not on design. With Java Swing you can develop very sophisticated cross-platform desktop applications, and because of my knowledge and experience this has been the natural choice for me.

### 3.9. Why has the application been implemented in Spanish?

My apologies, but initially I had not considered publishing this development and my native language is Spanish so the user interface and the code of the application are written in this language.

### 3.10. What features does the application include?

1. Send/receive text chats at any time.

2. Set the configuration of a test (subject, level, types of questions, time limit, number of questions) before initiating its execution.

3. Pause/stop the execution of a test.

4. Extend the time limit before sending an answer.

5. Upload new questions to the server.

6. Display an image (.gif, .png) associated with a question ("fichero_multimedia" field, optional).

7. Play an mp3 audio associated with a question ("fichero_multimedia" field, optional).

8. Review the results of the test execution highlighting hits in green and misses in red respecting the correct answer.

9. Maintain the ranking of scores.

10. A question store based on the server file system.

11. Five types of questions depending on the type of the answer:

- Unica

- Múltiple (My apologies for the accent for non Spanish users :-( )

- Texto

- Emparejada

- Multivalor

- Change the order of the options before sending the question (?desordenar_opciones? field of the JSON definition file, optional, defaults to 'true')

### 3.11. What improvements/enhancements could be made to the application?

These are, for example, a bunch of them that could be pretty challenging:

1. Improve the process of generating the client and server .jars.

2. Containerized the server and client application.

3. Be able to use different question stores (a document database, for example).

4. Show an image associated with each of the options in questions of type "Unica" and "Múltiple".

5. Enable scrolling in the panel that shows the question when there are many options. Note: I think that the current size of the question panel is big enough to present a reasonable number of options, and that showing a lot of options doesn't provide a good user experience (in my opinion it is better to split the question).

6. Develop a web or mobile version of the application.

7. Incorporate performance tests.

8. Add more functional tests.

9. Document the application at user level (User's guide)

10. Support different localizations (English, etc).

11. Improve error handling.

12. Enable the user interface to adapt to the resolution and/or size of the main window when it is resized.

13. Allow the user to decide whether to launch a test only for him or for the whole group (this is the default option).

14. Develop a complementary application that makes it easy to create questions and upload them to the server.

15. Incorporate statistics (e.g. % of users who have answered a question correctly).

### 3.12. On which operating systems can the application be run?

In all those in which Java 11 or higher is installed. The development has been done on a Mac with version 11.0.21 of OpenJDK (Open JDK 11).

3.13. What resolution is required to run the application on your desktop computer?

The size of the main window of the GUI has been set to 1410x830 pixels. With a standard resolution of 1920x1080 there should be no problem to display the client application correctly.

### 3.13 How has the application been tested?

The application has been tested on a private network with four computers connected running Mac, Windows and Linux operating systems. It has also been tested over the Internet, exposing the connection port on the home router.

### 3.14 Is authentication required to log into the application?

No, the application doesn?t manage users and password credentials, and is not integrated with an IdP (Identity Provider) or another authentication system. Users log into the application using an alias or nickname that must be unique among the group of users that are connected to the same server.

If you log into the application with a username's nickname or alias that is already registered, you'll get this error message:

### 3.15 Why have I shared this application?

Firstly, I really believe that this application could be useful for those who want to create a knowledge base regarding a particular subject or topic and check their knowledge acquisition on a regular basis; it is really easy to set up (once you have install Java on your computer you only have to put the questions in a folder, then start the server and the clients, and you are done), and it doesn't need any kind of administration or configuration.

Secondly, It could also be interesting for those who are studying together collaboratively and want to share their knowledge among them. In relation to this, I would be really delighted if new questions were provided by those who wanted to contribute to the project.

Thirdly, the application could be used as a prototype for those who want to learn developing a system that combines programming network communications using TCP/IP sockets, Java Swing GUIs, and multithreading techniques, and improve or change its architecture to make it better, more robust and functional. According to the tests I have carried out, this 1.0 version is functional and stable enough to be used for a small group of people, but I'm sure it can be improved in many ways.

Lastly, although I've been working for many years in the IT sector and have participated in the development of many systems in different companies, I've never contributed in any way to the community so let it be this little piece of software, my small and humble contribution to this great profession (software development).

### 3.16 Which dependencies does the application have?

The application has the following dependencies:

- commons-io-2.11.0.jar : Apache Commons IO is a library of utilities to assist with developing IO functionality.

- gson-2.8.0.jar: Google Gson is a Java library that can be used to convert Java Objects into their JSON representation. It can also be used to convert a JSON string to an equivalent Java object.

- jaudiotagger-3.0.11: Jaudiotagger is the Audio Tagging library used by [Jaikoz](https://www.jthink.net/jaikoz/) and [SongKong](https://www.jthink.net/songkong) taggers for tagging data in Audio files. It currently fully supports Mp3, Mp4 (Mp4 audio, M4a and M4p audio) Ogg Vorbis, Flac, Wav, Aif, Dsf and Wma.

- jlayer-1.0.1: JLayer is a library that decodes/plays/converts MPEG 1/2/2.5 Layer 1/2/3 (i.e. MP3) in real time for the JAVA(tm) platform.

### 3.16 Credits

The classes MP3Player.java and PlayingTime.java are based on the [Java Swing audio sample application](https://www.codejava.net/coding/java-audio-player-sample-application-in-swing) developed by [Nam Ha Minh](https://www.codejava.net/nam-ha-minh).

The classes CheckBoxCellRender.java, CheckableItem.java and CheckedComboBox.java, have been implemented by @aterai (TERAI Atsuhiro); see the example project at [Java Swing Tips](https://java-swing-tips.blogspot.com/2016/07/select-multiple-jcheckbox-in-jcombobox.html) and his GitHub [repository](https://github.com/aterai/java-swing-tips/tree/master/CheckedComboBox).

## 4. Packaging and creation of the client and server .jars of the application

Here are the steps to package the project with all the dependencies from the **NetBeans 15 IDE** and create the client and server .jar to run both applications from a terminal:

### 4.1. Set the `skipTests` property to ?true? in the `pom.xml` file.

We will make sure that the 'skipTests' property is set to true, to avoid that the failed tests do not prevent us from performing the packaging process. To do this, in the '<properties>' section of the 'pom.xml' file we will add '<skipTests>true</skipTests>'.

### 4.2. Select the pom.xml file and in the context menu, the "Run Maven >", "Goals ..." option.

We will type the Maven goal we want to run in the dialog box that appears (it can be either "install" or "package", either one will do):

- "install" does a "build" and then creates the project's .jar file in both the target directory and the local Maven repository.

- "package" does a "build" and then creates the .jar file of the project only in the target directory.

### 4.3. Change the file extension of 'TestChallenge-1.0-jar-with-dependencies.jar' to .zip.

### 4.4. Unzip the .zip file to create the 'TestChallenge-1.0-jar-with-dependencies' folder.

The manifest file 'META-INF/MANIFEST.mf' includes the main class that is set in the maven-assembly-plugin configuration (see 'pom.xml' file) which can be either 'com.testchallenge.server.TestChallengeServer', for the server .jar, or 'com.testchallenge.client.gui.TestChallengeClient', for the client .jar.

### 
### 4.5. Create the .jar of the server and the client app.

#### 4.5.1. Server app

From the 'TestChallenge-1.0-jar-with-dependencies' folder created after unzipping the 'TestChallenge-1.0-jar-with-dependencies.zip' archive, execute the following command:

```
jar -cfmv TestChallengeServer.jar META-INF/Manifest.mf org/apache/* org/jaudiotagger/* com/google/* com/testchallenge/server/* com/testchallenge/model/* images/* javazoom/*
```
#### 4.5.2. Client app

From the  'TestChallenge-1.0-jar-with-dependencies' folder created after unzipping the 'TestChallenge-1.0-jar-with-dependencies.zip'  archive, execute the following command:

```
jar -cfmv TestChallengeClient.jar META-INF/Manifest.mf org/apache/* org/jaudiotagger/* com/google/* com/testchallenge/client/* com/testchallenge/model/* images/* javazoom/*
```
IMPORTANT: in the 'META-INF/MANIFEST.mf' file you must put the corresponding main client or server class before executing the .jar creation. 

## 5. Execution of the server and client applications

To start the server, from the directory that contains the server .jar execute the following command:

```
java -jar TestChallengeServer.jar <port> <questions_base_directory>
```
- The '<port>' parameter sets the port on which the server receives connection requests from the clients.

- The '<questions_base_directory>' parameter sets the base or root directory where the subdirectories of the subjects/topics with the questions are located.

To start the client, from the directory that contains the client .jar execute the following command:

```
java -jar TestChallengeClient.jar [<nick_name>] [<server_IP_or_server_DNS>] [<server_port>]
```
- The '<nick_name>' parameter sets the unique nickname or alias with which the user signs up into the application.

- The '<server_IP_or_server_DNS>' parameter sets the IP or DNS of the server that is providing the service.

- The '<server_port>' parameter sets the port that is actually providing the service on the server.

As indicated by '[ ]', these parameters are optional: if you set them from the command line, they will be filled out automatically in the corresponding fields of the registration window that is shown before the GUI of the application is started. In the case they are not set before, you can set them in the registration window.

## 6. License

This project is licensed under the terms of the [GNU General Public License version 3 (GPLv3)](https://www.gnu.org/licenses/gpl-3.0.html). See the LICENSE.txt file for more details.
