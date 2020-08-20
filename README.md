## Miro Widget service

Spring Boot app to add/remove and modify Widgets along axises 

### Build and run

#### Prerequisites

- Java 11
- Maven

#### Running the app

Go on the project's root folder, then type:

    $ mvn spring-boot:run

To run Tests 

	$ mvn test
	
To generate JavaDocs
	
	$ mvn javadoc:javadoc

### Usage

- Launch the application and go on http://localhost:8080/api/widgets
- OpenAPI spec is exposed under http://localhost:8080/swagger-ui.html

### Complexity

- Widgets are stored in a Concurrent-hashMap with widgetId as Key. Adding a new widget will add a new Widget to HashMap and zIndexTracker is modified accordingly.
This combined complexity gives us O(logN) for add,update and delete operations. 
  
- ZIndexTracker stores zIndex and corresponding widgetId's in a skip-list. This is a sorted with zIndex and insert,deletes and updates to widgests are saved accordingly.
Getting a list of widgets gives us O(N) as widgets are already sorted.

### Concurrency Handling

WidgetStore and zIndexTracker is using java.util.concurrent DataStructures. 
