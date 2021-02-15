# Program Organization
You should have your context, container, and component (c4model.com) diagrams in this section, along with a description and explanation of each diagram and a table that relates each block to one or more user stories.

See Code Complete, Chapter 3 and https://c4model.com/

## Context Diagram:
- The context diagram shows the context of the use of our software system.
- The relationship shown is between the user and the system.

![Graphical Diagrams](https://github.com/fbarry/Antidote-Mobile/blob/master/artifacts/Context%20Diagram.PNG?raw=true)

## Container Diagram:
- brother

![Graphical Diagrams](https://github.com/fbarry/Antidote-Mobile/blob/master/artifacts/Container%20Diagram.PNG?raw=true)

# Code Design

## Activity Diagram:
- The activity diagram shows how both ends of the system work together and gives a rough walkthrough of how the user may choose to interact with the app at any given time.
- The circles represent who is actively using the system, green ovals represent actions/classes, and rhombuses represent active checks that grab information from the Back End server.
- The user must first sign in after opening the app before accessing the main menu, showing how both the Front and Back Ends interact with each other. 

![Graphical Diagrams](https://github.com/fbarry/Antidote-Mobile/blob/master/artifacts/Activity%20Diagram.png?raw=true)

## Class Diagram:
- The class diagram shows how specific classes interact with each other and the variables they use regularly as the app is in use.
- The chosen classes were chosen as they are the most frequently accessed classes by the application.

![Graphical Diagrams](https://github.com/fbarry/Antidote-Mobile/blob/master/artifacts/Class%20Diagram.jpg?raw=true)

## Sequence Diagram:
- This diagram showcases the most common interaction (MCI) in our game, a player object interacting with a card object.
- The player interacts with the card and depending on their action, onTouchEvent(), the card does a certain action, update().

![Graphical Diagrams](https://github.com/fbarry/Antidote-Mobile/blob/master/artifacts/Sequence%20Diagram.png)

## Data Design:
- This diagram shows the relationship between the different entities in the database.

![Graphical Diagrams](https://github.com/fbarry/Antidote-Mobile/blob/master/artifacts/EntityRelationshipDiagram.png?raw=true)

# Business Rules

A general business rule to follow in a multiplayer game is to ensure that players are shown all game updates as soon as they occur. As a result, when designing the architecture for the in-game experience, the database must notify the application when a turn takes place, allowing the player's game to update in response. If a player's device cannot update for more than 30 seconds, an error should be shown, and the player is kicked out of the game.

# User Interface Design

## Home Screen:
- The home screen contains only a few items: The logo in the center near the top, the info (i) button in the top-right, the menu button in the top-left, the "Create Game" button below the logo, and the "Join Game" beneath that.
- The info button opens the Info Screen
- The menu button opens a drawer from the left side of the screen containing the profile actions (login, sign up, view stats, logout)
- The "Create Game" button only works if the user is logged-in, and takes the player to a Lobby screen
- The "Join Game" has an attached text-entry, where the user enters the alphabetic code given to them by their friend. It takes them to a Lobby screen.

## Info Screen:
- The Info Screen contains a back button in the top-left, and the rest of the page is dominated by a scroll view, which contains the rules of the game.
- The back button takes the user to whichever screen directed them to the Info screen.

## Lobby screen:
- The lobby screen displays a list of the users who are in the lobby, the info button in the top-right, the "Leave Game" button, and the "Start Game" button
- The info button opens the Info Screen
- The "Leave Game" button directs the user back to the Home Screen
- The "Start Game" button is only visible to the user who created the lobby using "Create Game", and will direct *all* users to a Game Screen

## Game Screen:
- The Game Screen displays the user's cards on the bottom edge of the screen, a graphical list of the other players, an indicator of whose turn it is, "workstation" buttons in front of each player, the Info button, and buttons for making one's turn.
- The Info button opens the Info Screen
- The turn-choice buttons are only visible when it is the user's turn, and will graphically indicate which of the allowable actions are about to be taken
- The Workstation button opens a pop-up, which displays the workstation of the selected user.

![Graphical Diagrams](https://github.com/fbarry/Antidote-Mobile/blob/master/artifacts/UImockup.png?raw=true)

| Screen        | User Stories                                      |
|---------------|---------------------------------------------------|
| Home Screen   | 7, 17, 18, 20, 28, 9, 10, 13, 11, 12, 14, 15, 16  |
| Info Screen   | 24, 23, 7, 25                                     |
| Lobby Screen  | 5, 7, 17, 11, 26, 30, 1, 25                       |
| Game Screen   | 3, 7, 17, 13, 11, 27, 25, 4, 2                    |

# Resource Management

Database resources are the biggest concern. The limit on requests/second and total requests/month described in the "Scalability" section of this document puts a strain on the number of concurrent and total number of games that can be played per month. Because we are not planning to launch this app currently, this should not be a long-term issue as we should have enough resources to sufficiently test and demo the app. The number of requests/second should not exceed about the number of players in a game (3-9). Database storage should not be an issue as only current games are stored and updated in the database while older games are deleted. User statistics are not data heavy, being mostly integers (number of games, number of wins, etc.). We can manage these issues on a case-by-case basis while in development.

# Security

Most, if not all, security concerns are being handled by Parse, the REST API handling all database calls. Parse encrypts password information, handles the level of detail shown in alert messages, and protects secret data. 

# Performance

The main performance goal is in-game performance. There are two main areas where this becomes especially relevant:
- Database calls. When other players make decisions during their turns, the application must listen for database changes, retrieve those changes, and display them. While we can't do much to improve database call speed, we can ensure that while the user is waiting for the game to update, they are shown a loading feature.
- AI decisions. When programming the AI bots to play Antidote, we must ensure that their algorithm for determining what move they will take does not take longer than the average real player. That estimate will be determined at a later date, but will provide a bound for the algorithm's runtime.

# Scalability

The scalability of our app is limited by the database limit of our chosen database server (back4app). The limits are shown below:

| Type                  | Available |
|-----------------------|-----------|
| Requests/second       | 10 req/s  |
| Total requests/month  | 10 K      |
| File Storage          | 1 GB      |
| Database Storage      | 0.25 GB   |
| Cloud Code Jobs       | 1         |

# Interoperability

The app is not expected to share data or resources with other software or hardware.

# Internationalization/Localization

While the app is not going to be used commercially, it does use Java String objects, and all strings are stored in a reosurces file called strings.xml. Using this resource file, we can change the occurance of all uses of a string without touching the code values. This is especially important when updating string values, including when translating into other languages. In addition, Android Studio has a lot of built-in functionality for this strings resource.

# Input/Output

Currently, there are three places input is planned from the user: sign up, login, and game code input. In all three places, the user enters information (for login and sign up, text boxes accept all characters; for game code, the text box accepts only numbers) and the database call checks that information is valid and complete (i.e. is unique or exists). Output to the user comes mostly in alerts when something goes wrong (i.e. database call comes back with an exception). 

# Error Processing

Error processing is:
- Corrective. Users are prompted with an alert to 'Try Again' or 'Cancel'. Most, if not all, errors will be caused by a database or network issue, which can be resolved by simply trying again immediately or changing a detail then trying again.
- Passive with the database, active with game play. If the user enters incorrect login or signup information or a game code that does not exist, the database will respond accordingly. When this happens, the user is notified of the issue and allowed to try again. If a user tries to do an invalid action in game, however, the app will actively prevent them from taking that action, as it would mess up the flow of the game.
- Immediately dealt with. If an error is detected, the process in which it occured is stopped so it can be immediately handled. For example, if a user is trying to join a game (which involves finding the game code then adding them to the game - two database calls) and there is an invalid game code, then the user would be immediately notified of this issue before the system tried to add them to a non-existent game, causing yet another error.
- Handled through alerts. The standard format of an error alert is a popup with an informational message explaining to the user what went wrong. There are then two buttons: 'Try Again' or 'Cancel'.
- Caught in database calls through exceptions. Exception handling in Parse database calls in explicitly supported (the call returns an exception which, if not null, must be handled). Moreover, null pointer exceptions must be prevented, not handled, by making checks to see if an object is null before using it.
- Passed off to an error-handling class. This class will contain at least one function that allows the app to present a standard alert to the user containing an error message and options to 'Try Again' or 'Cancel', specifying what action to try again as a parameter.
- Prevented through verifying data at a class level. Any class that takes input from the user is responsible for verifying that data. If the class is being passed data from another location in the app, it can assume that data is clean.
- Handled through a customized built-in-exception-handling. This means that while exceptions will be used to monitor the status of a process and caught using built-in-exception-handling, further action will be taken using our own design of an alert message to the user with options we present to them. The user would not benefit from the stack trace as we would.

# Fault Tolerance

Our main strategy to handle fault tolerance is to catch exceptions and present an alert to the user, allowing them to try the process again or cancel it. If a problem cannot be solved by trying again (such as a game code or user not existing), the user would be notified. The REST API makes error handling simple in an area (database interaction) that could potentially have many faults, which eases our strain. Errors that cannot be resolved by the user (like an in-game glitch which causes the flow of the game to disrupted, i.e. skipping someone's turn) should be double checked by a second function.

# Architectural Feasibility

We don't have any explicit concerns over feasibility, as many of our app ideas and features are inspired by apps we have seen with similar features. For example, the idea of a join code allowing players to access a lobby with a list of other players and an in-game chat are both inspired by Among Us. Sharing game results, having a profile, and friending other players are all common features of mobile games.

# Overengineering

A few principles will be in place to ensure overengineering is not over- or under-done:
- All calls to the database will have error handling that gracefully shows an alert to the user when an error occurs, allowing them to 'Try Again' or 'Cancel' their call to the database.
- All returned objects will be checked for null value, as this would cause the app to crash. This null value will be handled on a case-by-case basis.

# Build-vs-Buy Decisions

As development continues, we expect to build rather than "buy" most UI features, but for the basic functionality of the app, we are building a custom view for the cards in a player's hand first.
- Custom Card Handler: We decided to use a custom card handler for two reasons: it is one of the main programming components of the app with touch features and layout challenges and we want to graphically design our own cards. If this was less of a learning project, we would have probably done more research into a library that would suit our needs, but programming custom views is a good tool to implement!

# Reuse

As development continues, we may need to add other libraries for features we wish to implement - examples of which include a messaging view for an in-game chat, a sharing SDK to share results to social media platforms, and, perhaps, an animation library to liven the UX. The library needed for the main functionality of the app, however, is below.
- Parse SDK Android: A REST API which is used to connect to a database stored on back4app.com. Parse has objects and function calls which allow for synchronous and asynchronous requests to POST, DELETE, and GET, along with more specific requests to update, sign-in/-out, and anything with an HTTP request. We use Parse because it provides a complete framework for backend development, allowing us to focus on implementing and developing other aspects of our app.

# Change Strategy

We have many planned enhancements to the basic version of our app listed below:
- Accessible mini-help popups
- AI players
- Enhanced graphics
- Profile accessibility
- Color themes
- Privacy settings
- Interactive tutorials
- Landscape support
- Customizable game modes
- Sharing results on social media
- Separate lobbies
- Friends lists

Because we have these improvements in mind, we can prepare the architecture of the app to account for them. Moreover, our database is very accessible, and we have local copies of each database object with the fields each object has. This allows us to change database platforms without worrying about losing database structure, ensuring a more flexible database architecture. The same idea goes for HTTP requests; we are trying to implement as many code handlers as possible to centralize our function calls in case we need to make any drastic changes. This will help to ensure the ripple of changes isn't too drastic.
