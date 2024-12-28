

# Java NIO HTTP Server (RegExNio)

This project is a simple HTTP server built using Java NIO (Non-blocking I/O) and the FreeMarker templating engine. The server handles basic GET and POST requests using a custom MVC-style framework.

## Features

- **Non-blocking I/O** using Java NIO (`SocketChannel`, `Selector`) to handle multiple connections simultaneously.
- **Custom MVC Framework** with annotations like `@GETContent` and `@PostContent` to map HTTP routes to controller methods.
- **Dynamic HTML rendering** with FreeMarker templating engine.
- **Custom routing**: Routes are dynamically extracted based on annotations.
- **Request parsing**: HTTP requests are parsed to extract method, headers, and query parameters.
  
---

## Project Structure

```
src
└── org
    └── Server
        ├── Main.java             # The main server class with NIO event loop
        ├── MvcExample.java       # Example MVC controller with routes
        ├── configs               # Configuration-related classes
        ├── markerEngine          # FreeMarker template integration
        └── Utils                 # Utility classes (Response, Modle, etc.)
```

## Annotations Overview

- `@MvcController`: Defines a controller class and specifies the port.
- `@MvcMapping`: Maps a base URL to a controller class.
- `@GETContent`: Maps a method to a specific GET route.
- `@PostContent`: Maps a method to a specific POST route.
- `@ParseHtmlFille`: Marks methods that return HTML files using FreeMarker templates.

---

## Getting Started

### Prerequisites

- **Java 11** or higher
- **Maven** (or another build tool if you prefer)

### Setup Instructions

1. **Clone the Repository**
    ```bash
    git clone https://github.com/your-username/custom-nio-server.git
    cd custom-nio-server
    ```

2. **Build the Project**
    You can use Maven to build the project (or any IDE like IntelliJ IDEA).
    ```bash
    mvn clean install
    ```

3. **Run the Server**
    Run the `Main.java` class which is the entry point of the server:
    ```bash
    java -cp target/custom-nio-server.jar org.Server.Main
    ```

4. **Server Output**
    You should see:
    ```
    Server Started At port 8081
    ```

### Testing the Server

Once the server is running, you can test the endpoints with tools like `curl`, Postman, or simply by using a browser.

#### Example Routes

1. **GET /mvc/hello**
   ```bash
   curl http://localhost:8081/mvc/hello?name=Ayan
   ```
   - This route invokes the `getContent2` method in `MvcExample.java`.
   - It fetches a response from an external API (`https://jsonplaceholder.typicode.com/posts/1`), then returns a FreeMarker-generated HTML page using the `Test.ftl` template.

2. **POST /mvc/post**
   ```bash
   curl -X POST -d "name=example" http://localhost:8081/mvc/post
   ```
   - This route invokes the `getContent` method in `MvcExample.java`, returning a simple string response based on the request.

3. **GET /mvc/h**
   ```bash
   curl http://localhost:8081/mvc/h
   ```
   - This route returns a simple HTML string.

### How It Works

- **Main Class (`Main.java`)**
   - Sets up a non-blocking server using `ServerSocketChannel` and `Selector`.
   - Handles `OP_ACCEPT` and `OP_READ` events, accepting connections and reading HTTP requests.
   - Based on the HTTP method (`GET` or `POST`), it routes requests to methods defined in `MvcExample` using reflection.

- **Routing**
   - Routes are mapped to methods using annotations like `@GETContent` and `@PostContent`.
   - Methods in `MvcExample.java` handle different routes, and based on the annotation, the framework dynamically invokes the appropriate method.

- **Request Parsing**
   - The `parseResponse` method in `Main.java` parses incoming HTTP requests, extracting headers, query parameters, and the request method (GET or POST).

- **Dynamic HTML Rendering**
   - When methods annotated with `@ParseHtmlFille` are invoked, FreeMarker templates are used to render HTML pages with dynamic content.

### FreeMarker Templating

FreeMarker is used to render HTML pages dynamically. To add new templates, create `.ftl` files in your resources folder.

Example FreeMarker file: `Test.ftl`
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hello Page</title>
</head>
<body>
    <h1>Hello, ${name}!</h1>
    <p>Here is the external API response: ${response}</p>
</body>
</html>
```

---

## Contributing

Feel free to contribute to this project by creating pull requests, opening issues, or suggesting improvements.
