# Brick Framework – Application Development Guide

This document explains **how to build and run an application using the Brick Framework**.  
It is intended for developers who want to create APIs using Brick’s **contract-first, YAML-driven execution model**.

---

## 1. What You Build with Brick

When using Brick, you **do not write controllers**.

Instead, you define:
- **API contracts** using OpenAPI
- **Execution flow** using YAML controller files
- **Business logic** using annotated Java classes
- **Validation logic** using separate annotated Java classes

Brick takes care of:
- Starting an embedded Tomcat server
- Validating requests and responses
- Executing services and validators
- Managing threading and parallel execution

---

## 2. Creating a Brick Application

### 2.1 Entry Point

To create an application using Brick, you must:

1. Create an instance of `BrickApplication`
2. Pass the **base package name** to its constructor
3. Call `startApplication()` to start the server

### Example

```java
public class Application {

    public static void main(String[] args) {
        BrickApplication app = new BrickApplication("com.example.myapp");
        app.startApplication();
    }
}
```

### Why Base Package Is Required

The base package is used to:
- Scan for `@Service` annotated classes
- Scan for `@Validator` annotated classes
- Discover methods annotated with `@Identifier`

Only classes under this package will be managed by Brick.

---

## 3. Project Structure

```text
src/
 └── main/
     ├── java/
     │   └── com/example/myapp/
     │       ├── service/
     │       ├── validator/
     │       └── Application.java
     └── resources/
         ├── openapi/
         │   └── api.yaml
         ├── controller/
         │   └── process.yaml
         └── application.yaml
```

---

## 4. OpenAPI Definitions

All API contracts must be defined using **OpenAPI YAML files**.

**Location**
```
resources/openapi/
```

OpenAPI is used to validate requests and responses before and after execution.

---

## 5. Controller YAML Files

Controller files define **how a request is executed**.

**Location**
```
resources/controller/
```

They define services, validators, execution order, and response mapping.

---

## 6. Writing Services

Services contain **business logic**.

```java
@Service(name = "Test Service")
public class TestService {

    @Identifier("Test Method 1")
    public String execute(String value) {
        return "Processed: " + value;
    }
}
```

---

## 7. Writing Validators

Validators contain **validation logic only**.

```java
@Validator(name = "Test Validator")
public class TestValidator {

    @Identifier("Validator Method 1")
    public boolean validate(String value) {
        return value != null && !value.isEmpty();
    }
}
```

---

## 8. Parameter Resolution

Brick supports dynamic parameter binding from:
- Request body
- Headers
- Query parameters
- Cookies
- Path variables
- Previous service responses

---

## 9. Parallel and Serial Execution

Service groups support:
- `serial` execution
- `parallel` execution using a dedicated service thread pool

---

## 10. Response Handling

Responses are constructed from controller definitions and validated against OpenAPI before being returned.

---

## 11. Configuration

Configuration is provided via `application.yaml`, including:
- Server port
- Embedded Tomcat settings
- Service thread pool configuration

---

## 12. Embedded Server

Brick runs using an **embedded Tomcat server**, started automatically when the application starts.

---

## 13. Error Handling

Brick provides consistent error handling for:
- Invalid paths or methods
- Validation failures
- Unexpected runtime errors

---

## 14. Summary

To build an application with Brick:

1. Create a `BrickApplication`
2. Provide the base package name
3. Define OpenAPI contracts
4. Create controller YAML files
5. Write services and validators
6. Start the application

Happy building with **Brick** 🧱
