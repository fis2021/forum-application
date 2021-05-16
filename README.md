# Forum Application


# General description
This application serves as a virtual discussion space where people can create threads on different subjects and reply to other’s threads.


# Technologies used
Java 15 programming language;


JavaFX 15 GUI;


Nitrite database;


Maven build tool with its dependencies;


JUnit 5 testing plugin;


TestFX plugin;


JaCoCo plugin.

# Setup

Clone the repo:

    git clone https://github.com/fis2021/forum-application.git

Open the project as a Maven project, then use the following commands:

    mvn dependency:resolve
    mvn clean install

Then reload the project and use the command:

    mvn javafx:run

If everything was set up accordingly, the application should be up and running.

# Registration and Log In
Before using the application, every user has to register for an account using a unique username and a password. On this page, there are available two types of accounts:


- one for the normal User;


- another one for Admin/Moderator.


Depending on the account type chosen, users will be granted with specific functionalities.


# The User Account
After logging in, the user can see all the threads that exist at that moment. They can filter the shown threads by date, or "popularity" (number of replies).


Also, after logging in, the user can choose to create a new thread.


A logged-in user can also select any thread and reply to it, with a comment.


Another feature a user benefits from, is that they can check out other profiles. Along with the profile information, there is also displayed a list with the threads created by that user.


# The Admin/Moderator Account:
The moderators of the application can do anything a normal user can.


A moderator can choose to close a thread or even to remove it. A closed thread will notify the user that it no longer accept replies, while a thread which has been removed, will be marked as "DELETED" instead.


Similar to the previous functionality, a moderator can also remove replies. A deleted reply will be marked accordingly.


Last but not least, an administrator can ban a user. This ban will be either temporary (for a period of 24 hours) or permanent. When a user gets banned permanently, they get "blacklisted" and will not be able to create a new account again.