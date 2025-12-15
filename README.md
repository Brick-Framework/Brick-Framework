# Brick Framework

**Brick** is a **contract-first, annotation-driven Java framework** for building APIs where **OpenAPI defines the contract**, **YAML defines execution**, and **code contains only business logic**.

Brick is designed to eliminate controller boilerplate, enforce strict API contracts, and provide a clean separation between **validation**, **orchestration**, and **business logic**.

---

## 🚀 Overview

Unlike traditional MVC frameworks, Brick follows a declarative execution model:

- API behavior is defined by **OpenAPI specifications**
- Request execution flow is described in **YAML**
- Java code contains only **services** and **validators**
- Controllers are configuration, not code

---

## ✨ Key Features

- 📜 **Strict OpenAPI enforcement**
  - Path and method validation
  - Request and response schema validation
  - Early rejection of invalid requests

- 🧩 **Annotation-based component discovery**
  - `@Service` for business logic
  - `@Validator` for validation logic
  - Identifier-based method mapping

- 📂 **YAML-driven execution**
  - Define service execution without writing controllers
  - Serial and parallel execution support
  - Clear, readable request workflows

- ⚙️ **Configurable threading model**
  - Built-in Tomcat server
  - Separate service thread pool for parallel execution
  - Fine-grained control over concurrency

- 🔄 **Dynamic parameter resolution**
  - Access request body, headers, query params, cookies, and path variables
  - Reuse outputs from previously executed services

- 🛡 **Predictable error handling**
  - Contract violations handled automatically
  - Controlled validation failure responses
  - Safe handling of unexpected runtime errors

---

## 🏗 High-Level Project Structure

```text
src/
 └── main/
     ├── java/
     │   └── services & validators
     └── resources/
         ├── openapi/        # OpenAPI contracts
         ├── controller/     # Execution YAML files
         └── application.yaml
```

---

## 🔧 Configuration

Brick is configured using application.yaml:
- Server port and runtime settings
- Built-in Tomcat configuration
- Service execution thread pool configuration

Defaults are provided to allow quick startup with minimal configuration.

---

## 🎯 Design Goals

- Enforce API contracts at runtime
- Remove controller boilerplate
- Separate validation from business logic
- Make execution flow explicit and auditable

Support scalable and parallel request processing

---

## 📌 Ideal Use Cases

- Enterprise APIs with strict contracts
- Workflow-driven request processing
- Teams preferring configuration over controllers
- Systems requiring strong validation guarantees