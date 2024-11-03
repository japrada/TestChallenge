## Table of Contents
- [Table of Contents](#table-of-contents)
- [1. Chatting and lunching a test](#1-chatting-and-lunching-a-test)
  - [1.1 Chatting](#11-chatting)
  - [1.2 Launching a test challenge](#12-launching-a-test-challenge)
- [2. Where are the questions stored?](#2-where-are-the-questions-stored)
- [3. What types of questions can be defined?](#3-what-types-of-questions-can-be-defined)
- [4. Is it possible to display questions containing images?](#4-is-it-possible-to-display-questions-containing-images)
- [5. Is it possible to display questions containing audios?](#5-is-it-possible-to-display-questions-containing-audios)
- [6. Is the order of the options shown in the questions always the same?](#6-is-the-order-of-the-options-shown-in-the-questions-always-the-same)
- [7. Is authentication required to log into the application?](#7-is-authentication-required-to-log-into-the-application)
- [8. Is authentication required to log into the application?](#8-is-authentication-required-to-log-into-the-application)
- [9. Is it possible to upload new questions to the server?](#9-is-it-possible-to-upload-new-questions-to-the-server)
- [10. Application GUI executing on different operating systems](#10-application-gui-executing-on-different-operating-systems)
- [11. Pausing and resuming a test execution](#11-pausing-and-resuming-a-test-execution)
- [12. Stopping a test execution](#12-stopping-a-test-execution)
- [13. Extending the time limit to answer a question](#13-extending-the-time-limit-to-answer-a-question)
- [14. Reviewing the results of the test](#14-reviewing-the-results-of-the-test)

## 1. Chatting and lunching a test

The two main functionalities provided by the application are the sending of text messages (chats) and the execution of group tests. 

### 1.1 Chatting

Text messages are sent and received in the "Chat" panel, as it is shown in the following screenshot:

![Chatting](screenshots/Chat_panel.png)

As can be seen in the picture above, the area at the bottom of the "Chat" panel is where the user writes the test messages. The text message is sent to all of the users by clicking on the "Enviar chat" button. In the case the text message contains a specific user (or a group of users) (i.e a @<username> reference), it will be sent only to the referenced users. 

Sent messages are received by the users on the top area of the "Chat" panel. However, this text area is not only used by the application to show the messages interchanged by the users at any moment, but also to show the messages that are sent from the server during a test execution. 

### 1.2 Launching a test challenge

The following screenshot shows the sequence of steps to follow to configure the options with which the test will be launched to the group of connected users. 

  ![Setting up a test execution](screenshots/Steps_to_set_up_a_test_execution.png)

  1. **"Temática"**: this drop-down list contains the name of the subdirectories that are under the root base directory parameter that has been set when starting the TestChallengeServer application. These directories are the topics or areas of knowledge containing the questions of the tests that are to be lunched.
   
  2. **"Nivel"**: this drop-down list contains the level of the questions to be selected within the topic or knowledge area that has been previously selected.
   
  3. **"Tipo"**: this drop-down list contains the type of questions that will be included in the test. It is possible to select one or more type of questions as this component enables the selection of multiple values.

  4. **"Nº Preguntas"**: in this field you set the number of questions of the test. If the number of questions set is greater than the number of questions that are actually stored in the selected directory, only the smaller of the two will be sent.
   
  5. **"Tpo. Límite"**: in this field you set the number of seconds that any of the users have to answer the question. This time limit can be modified during the test execution for the question that has been sent by adding more seconds though the "Ampliar tiempo en" option.
   
  6. **"Iniciar test"**: finally, once all the previous options have been set, the user starts the test by clicking on the "Iniciar test" button and the server notifies the users that a test has been launched with the selected configuration:

     ![Test execution about to start](screenshots/Test_execution_launched_and_about_to_start.png)

The following [video](https://drive.google.com/file/d/1KAnOZBPvDgBjTooLBfB7LboIwm1NTJhk/view?usp=sharing) shows a demo of how the application works.

## 2. Where are the questions stored?

The questions (JSON files) are stored in the server's file system, in one of the subdirectories of the base/root directory that is specified in the corresponding server startup parameter. Each subdirectory represents a subject/topic and its name is displayed in the client application on a drop-down list.

The **“Multimedia”** subfolder stores the images and audio files that are associated with the JSON files that define the questions of the subject/topic **“Inglés”**.

![Questions for the topic "Inglés"](screenshots/Directorio_preguntas.png)

## 3. What types of questions can be defined?

The application allows you to define five types of questions depending on the type of the answer:

* **Unica**: several options are presented and the user can select only one.

    Example. The question definition:

  ![Definition of a question of type "Unica"](screenshots/Question_definition_of_type_Unica.png)

    renders to:

  ![Rendering of a question definition of type "Unica"](screenshots/Rendering_of_question_definition_of_type_Unica.png)

* **Múltiple**: several options are presented and the user can select more than one.

    Example. The question definition:

  ![Definition of a question of type "Múltiple"](screenshots/Question_definition_of_type_Múltiple.png)

    renders to:

  ![Rendering of a question definition of type "Múltiple"](screenshots/Rendering_of_question_definition_of_type_Múltiple.png)

* **Texto**: the user has to write a sentence with the answer to be sent.

    Example. The question definition:

  ![Definition of a question of type "Texto"](screenshots/Question_definition_of_type_Texto.png)

    renders to:

  ![Rendering of a question definition of type "Texto"](screenshots/Rendering_of_question_definition_of_type_Texto.png)

* **Emparejada**: several options are presented and for each option there is a drop-down list with the same list of values.

    Example 1. The question definition:

  ![Definition of a question of type "Texto"](screenshots/Question_definition_of_type_Emparejada_1.png)
   
   renders to:

  ![Rendering of a question definition of type "Texto"](screenshots/Rendering_of_question_definition_of_type_Emparejada_1.png)

    Example 2. The question definition that has associated an image:

  ![Definition of a question of type "Texto"](screenshots/Question_definition_of_type_Emparejada_2.png)

    renders to:

  ![Rendering of a question definition of type "Texto"](screenshots/Rendering_of_question_definition_of_type_Emparejada_2.png)

* **Multivalor**: several options are presented and for each option there is a drop-down list with values that can be different for each option.

    Example 1. The question definition that has associated an mp3 audio:

  ![Definition of a question of type "Texto"](screenshots/Question_definition_of_type_Multivalor_1.png)

    renders to:

  ![Rendering of a question definition of type "Texto"](screenshots/Rendering_of_question_definition_of_type_Multivalor_1.png)

    Example 2. The question definition:

  ![Definition of a question of type "Texto"](screenshots/Question_definition_of_type_Multivalor_2.png)

    renders to:

  ![Rendering of a question definition of type "Texto"](screenshots/Rendering_of_question_definition_of_type_Multivalor_2.png)

## 4. Is it possible to display questions containing images?

Yes, the image is displayed in the upper right corner of the panel containing the question. That is, in this version of the application a single image can be associated with a question, but not with each of the options of the question. 

![Question with an associated image](screenshots/Chapter_3_image_1.png)

The image (.gif, .png, .jpg, .jpeg) can be zoomed in by double-clicking on it and then resized by resizing the pop-up window that is shown (Note: the popup-window showing the image is closed when the focus is lost or when you close it specifically; this has been done this way on purpose).

![Pop up window showing the associated image](screenshots/Chapter_3_image_2.png)

## 5. Is it possible to display questions containing audios?

Yes, when a question has an mp3 audio file associated with it, it can be played by double-clicking on the “Mp3” icon that appears in the upper right corner of the panel displaying the question. 

![Question with an associated audio](screenshots/Chapter_4_image_1.png)

In this version only .mp3 file playback has been implemented. You can pause it or stop it, but you cannot go forward or backward. (Note: the popup-window playing the .mp3 file is closed when the focus is lost or when you close it specifically; this has been done this way on purpose).

![Pop up window showing the associated audio](screenshots/Chapter_4_image_2.png)

## 6. Is the order of the options shown in the questions always the same?

No, by default when a question is sent to the connected users by the test server the options it contains are randomly reordered so that the order in which they are presented may change from one test execution to another. This is done to make it more difficult to memorize the answers. 

This behavior can be modified by including the optional field “desordenar_opciones” in the question definition file with the value “false”. In that case, when the question is submitted by the server, the options are ordered following the order of the definition. This can be useful for example in those questions that have an audio associated with them, so that the options of the question are temporarily ordered according to the listening of the audio.

![Question with "desordenar_opciones" set to "false"](screenshots/Chapter_5_image_1.png)

## 7. Is authentication required to log into the application?

No, the application doesn’t manage users and password credentials, and it is not integrated with an IdP (Identity Provider) or another authentication system. Users log into the application using an alias/nickname that must be unique among the group of users that are connected to the same server.

![User registration window dialog](screenshots/Chapter_6_image_1.png)

## 8. Is authentication required to log into the application?

If you log into the application with a username’s nickname/alias that is already registered, you’ll get this error message:

![User registration window dialog: duplicated nickname](screenshots/Chapter_7_image_1.png)

## 9. Is it possible to upload new questions to the server?

Yes, a functionality has been implemented to enable users to upload new questions to the server together with the multimedia file (image or audio) that is specified in the JSON definition file.

![Uploading a new question](screenshots/Chapter_8_image_1.png)

As can be seen in above screenshot, the button “Subir pregunta” opens a pop-up window from which the user can select the new question that wants to upload to the server, together with the file of the image or audio associated with the question (if any), that must be stored in the “Multimedia” subdirectory. The question (and the multimedia file) is uploaded to the subject/topic folder that is selected in the drop-down list on the server at the moment of confirming the operation. Once the question has been uploaded, a chat is sent from the server to inform all the clients.

## 10. Application GUI executing on different operating systems

The following screenshots show the client application GUI executing on a Windows,  Linux, and Mac system:

* User 'windows' connected from a Windows system participating in a test challenge of one question:

  ![Windows GUI](screenshots/Chapter_9_image_1.png)

* User 'linux' connected from a Linux system participating in the same test challenge as user ‘windows’:

  ![Linux GUI](screenshots/Chapter_9_image_2.png)

* User 'windows' connected from a Windows system being informed of the number of points obtained once the test challenge has finished:

  ![Windows GUI with points](screenshots/Chapter_9_image_3.png)

* User connected from a Linux system being informed of the number of points obtained once the test challenge has finished:

  ![Linux GUI with points](screenshots/Chapter_9_image_4.png)

* Client application GUI running on a Mac OS system:

  ![MacOs GUI](screenshots/Chapter_9_image_5.png)

## 11. Pausing and resuming a test execution

Any user who has not yet sent a response can pause the execution of the test in progress. As soon as the execution is paused, the countdown stops for the rest of the users as well. 

![Pausing a test execution](screenshots/Pausing_a_test_execution.png)

Any user can resume the execution of a test to send the answer to the question; at that moment, the countdown is reactivated also for the rest of the users.

![Resuming a test execution](screenshots/Resuming_a_test_execution.png)

## 12. Stopping a test execution

In the same way, any user who has not yet sent a response can stop the execution of the test in progress. 

![Stopping a test execution](screenshots/Stopping_a_test_execution.png)

At the moment the user confirms the operation, the system penalizes the user who has made the request with as many negative points as questions were still pending to be sent, plus an additional negative point for not having sent the answer to the question in progress. In addition, all users who had not sent the answer at the time of stopping the test are also assigned a negative point.
 
 ![Test execution stopped with negative points](screenshots/Test_execution_stopped_with_negative_points.png)

## 13. Extending the time limit to answer a question

A user can extend the time limit for answering a question by first selecting the number of seconds to be added and then clicking the “Ampliar” button. Similarly to de "Pause" option, the requested extension applies to all users, so this option increases everyone's chances.

![Extending the time limit to answer a question](screenshots/Extending_the_time_limit_to_answer_a_question.png)

## 14. Reviewing the results of the test

Once the test execution has been completed, users can review the results through the **“Ver respuestas”** option. After selecting this option the buttons **"Anterior"** and **"Siguiente"** are enabled to move forwards or backwards through the list of the questions of the test and review the results.

 For each question sent by the server, the options answered correctly are shown in green and those answered incorrectly are shown in red. In the cases where the user has not submitted an answer, the question is shown with the correct options, but without color coding. 
 
 In the following screenshots different examples are presented.

* Example 1 (screenshot taken from a Windows system): 

  In this example, a question of type “Múltiple” has been answered. The correct options that the user should have selected when sending the answer were the third and the fourth options, but the user sent the answer only with the first option selected (and it is marked in red because it is not one of the correct answers).

  ![Example 1](screenshots/Reviewing_the_results_example_1.png)

* Example 2 (screenshot taken from a Windows system):

  In this example, a question of type “Emparejada” has been answered. In this case, all the options are marked in red because none of them has been answered correctly. The correct answer is presented in green in the drop-down list associated with each option and the wrong answer (the one that the user erroneously selected), in red.

  ![Example 2](screenshots/Reviewing_the_results_example_2.png)

* Example 3 (screenshot taken from a Mac system): 

  This is the same question as presented in the example 2, but in this case it can be seen that the user answered correctly the first option and incorrectly the fourth one; in the rest of the options, the default value “-” was sent, i.e the user didn’t select a specific value for the option before sending the answer in that case.

  ![Example 3](screenshots/Reviewing_the_results_example_3.png)

* Example 4 (screenshot taken from a Windows system): 

  In this example, which is also a question of type “Emparejada”,  the user has answered correctly to all of the options and that’s why they are marked in green. 

  ![Example 4](screenshots/Reviewing_the_results_example_4.png)

* Example 5 (screenshot taken from a Mac system): 

   In this example, a question of type "Emparejada" that has not been answered by the user (so a -1 point has been scored in this case) is showing the correct answers with no color scheme associated because the user didn't answer none of the options.

  ![Example 5](screenshots/Question_not_sent_showing_the%20correct_answers.png)