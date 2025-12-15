# Brick Framework – Tutorial & Reference Application Guide

This document walks you through building a **simple tutorial application using the Brick Framework**.  
It is designed to act as a **long-term reference** that developers can revisit while building real-world applications.

The tutorial explains:
- How Brick components work together
- How a request is processed end-to-end
- How to configure and run an application
- How to structure your project for growth

---

## 1. Purpose of This Tutorial

This tutorial application serves as:
- A **starter template**
- A **mental model** for Brick’s execution flow
- A **reference architecture** for future applications

You can use this document as a baseline when designing new APIs with Brick.

---

## 2. What We Will Build

We will build a simple API that:
- Accepts a JSON request
- Validates input
- Executes business logic
- Returns a JSON response

The API behavior will be defined using:
- OpenAPI
- Controller YAML
- Services
- Validators

---

## 3. High-Level Architecture

```text
Client Request
     ↓
Embedded Tomcat
     ↓
OpenAPI Validation
     ↓
Controller Resolution
     ↓
Validators
     ↓
Services (Serial / Parallel)
     ↓
Response Mapping
     ↓
OpenAPI Response Validation
     ↓
Client Response
```

---

## 4. Project Structure

```text
src/
 └── main/
     ├── java/
     │   └── com/example/tutorial/
     │       ├── Application.java
     │       ├── service/
     │       │   └── GreetingService.java
     │       └── validator/
     │           └── GreetingValidator.java
     └── resources/
         ├── openapi/
         │   └── greeting.yaml
         ├── controller/
         │   └── greeting-controller.yaml
         └── application.yaml
```

---

## 5. Creating the Application Entry Point

Every Brick application starts with `BrickApplication`.

```java
public class Application {

    public static void main(String[] args) {
        BrickApplication app = new BrickApplication("com.example.tutorial");
        app.startApplication();
    }
}
```

### What Happens Here
- Brick scans the base package
- Services and validators are registered
- Embedded Tomcat is started
- The application begins listening for requests

---

## 6. Defining the API Contract (OpenAPI)

OpenAPI defines **what requests are allowed**.

```yaml
openapi: 3.0.0
paths:
  /greet:
    post:
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                name:
                  type: string
      responses:
        200:
          description: Greeting response
          content:
            application/json:
              schema:
                type: object
                properties:
                  message:
                    type: string
```

### Role of OpenAPI
- Validates request structure
- Validates response structure
- Rejects invalid requests early

---

## 7. Writing the Controller File

Controller YAML defines **how the request is executed**.

```yaml
/greet:
  post:
    services:
      - serviceId: Create Greeting
        executionId: greeting
        validator:
          - validatorId: Validate Name
            responseType: visible
            params:
              - request.body.name
            message: Name must not be empty
        params:
          - request.body.name

    response:
      - key: message
        value: greeting.response
```

### Key Points
- Path and method match OpenAPI
- Validator runs before service
- Response is mapped explicitly

---

## 8. Writing the Validator

Validators ensure correctness before execution.

```java
@Validator(name = "Greeting Validator")
public class GreetingValidator {

    @Identifier("Validate Name")
    public boolean validate(String name) {
        return name != null && !name.trim().isEmpty();
    }
}
```

### Validator Rules
- Must return boolean
- Failure stops execution
- Response depends on `responseType`

---

## 9. Writing the Service

Services contain business logic.

```java
@Service(name = "Greeting Service")
public class GreetingService {

    @Identifier("Create Greeting")
    public String greet(String name) {
        return "Hello, " + name;
    }
}
```

---

## 10. How a Request Is Processed

1. Request hits embedded Tomcat
2. Path and method validated via OpenAPI
3. Request body validated against schema
4. Controller YAML is resolved
5. Validators are executed
6. Services are executed
7. Response is constructed
8. Response validated against OpenAPI
9. Response returned to client

---

## 11. Configuration (`application.yaml`)

```yaml
server:
  port: 8080
  service:
    minThreads: 2
    maxThreads: 4
    queueLength: 10
```

### What This Controls
- Server port
- Service execution concurrency
- Parallel execution limits

---

## 12. How Components Work Together

| Component | Responsibility |
|---------|----------------|
| OpenAPI | Contract & schema validation |
| Controller YAML | Execution orchestration |
| Validators | Input validation |
| Services | Business logic |
| Brick Core | Threading, lifecycle, error handling |

---

## 13. Extending the Tutorial App

You can extend this app by:
- Adding more paths in OpenAPI
- Creating new controller YAML files
- Introducing service groups
- Using parallel execution
- Reusing service responses

---

## 14. Best Practices

- Keep OpenAPI authoritative
- Use validators for validation only
- Keep services focused and reusable
- Prefer YAML changes over code changes
- Use executionId consistently

---

## 15. Summary

This tutorial application demonstrates:
- How Brick applications are structured
- How requests flow through the framework
- How to configure and extend an application

Use this as your **base reference** when building applications with Brick.

Happy building with **Brick** 🧱
