Populate each section with information as it applies to your project. If a section does not apply, explain why. Include diagrams (or links to diagrams) in each section, as appropriate. For example, sketches of the user interfaces along with an explanation of how the interface components will work; ERD diagrams of the database; rough class diagrams; context diagrams showing the system boundary; etc. Do not link to your diagrams, embed them directly in this document by uploading the images to your GitHub and linking to them. Do not leave any section blank.

# Program Organization
You should have your context, container, and component (c4model.com) diagrams in this section, along with a description and explanation of each diagram and a table that relates each block to one or more user stories.

See Code Complete, Chapter 3 and https://c4model.com/

# Code Design
You should have your UML Class diagram and any other useful UML diagrams in this section. Each diagram should be accompanied by a brief description explaining what the elements are and why they are in the diagram. For your class diagram, you must also include a table that relates each class to one or more user stories.

See Code Complete, Chapter 3 and https://c4model.com/

# Data Design
If you are using a database, you should have a basic Entity Relationship Diagram (ERD) in this section. This diagram should describe the tables in your database and their relationship to one another (especially primary/foreign keys), including the columns within each table.

See Code Complete, Chapter 3

# Business Rules
You should list the assumptions, rules, and guidelines from external sources that are impacting your program design.

See Code Complete, Chapter 3

# User Interface Design
You should have one or more user interface screens in this section. Each screen should be accompanied by an explaination of the screens purpose and how the user will interact with it. You should relate each screen to one another as the user transitions through the states of your application. You should also have a table that relates each window or component to the support using stories.

See Code Complete, Chapter 3

# Resource Management
See Code Complete, Chapter 3

# Security
See Code Complete, Chapter 3

# Performance
See Code Complete, Chapter 3

# Scalability
See Code Complete, Chapter 3

# Interoperability
See Code Complete, Chapter 3

# Internationalization/Localization
See Code Complete, Chapter 3

# Input/Output
See Code Complete, Chapter 3

# Error Processing
See Code Complete, Chapter 3

# Fault Tolerance
See Code Complete, Chapter 3

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
