# Brick Framework – Controller File Guide

This document explains **how to write controller YAML files in the Brick Framework**.  
Controller files define the **execution flow** for API requests after OpenAPI validation succeeds.

---

## 1. What Is a Controller File?

A controller file:
- Is written in **YAML**
- Lives under `resources/controller/`
- Maps an OpenAPI path + HTTP method to:
  - Services
  - Validators
  - Execution order
  - Response construction

Controller files **do not contain business logic**.  
They only describe **how logic is executed**.

---

## 2. Controller File Location

```text
src/main/resources/controller/
```

Each file may define one or more paths.

---

## 3. Basic Structure

```yaml
/path:
  post:
    services: []
    response: []
```

### Rules
- Root-level keys are **API paths**
- Path must exist in OpenAPI
- HTTP method must match OpenAPI
- Supported methods depend on OpenAPI definition

---

## 4. Services Section

The `services` section defines **what gets executed**.

```yaml
services:
  - serviceId: Example Service
    executionId: example
    params: []
```

### Fields

| Field | Description |
|------|-------------|
| `serviceId` | Identifier of service method |
| `executionId` | Unique ID used to store service output |
| `params` | Parameters passed to service |

---

## 5. Validators

Validators run **before** the service.

```yaml
validator:
  - validatorId: Example Validator
    responseType: visible
    params:
      - request.body.value
    message: Invalid input
```

### Validation Behavior
- All validators must pass
- On failure:
  - `hidden` → generic message
  - `visible` → custom message

---

## 6. Service Groups

Service groups allow **serial or parallel execution**.

```yaml
- service-group:
    execution-type: parallel
    service-list:
      - serviceId: Service A
        executionId: a
        params: []
      - serviceId: Service B
        executionId: b
        params: []
```

### Execution Types
- `serial` – executes sequentially
- `parallel` – executes concurrently using service thread pool

---

## 7. Parameter Resolution

Parameters can reference:

### Request Data
- `request.body.key`
- `request.header.key`
- `request.query.key`
- `request.cookie.key`
- `request.path.key`

### Service Responses
- `executionId.response`
- `executionId.response.field`

---

## 8. Using Service Outputs

```yaml
params:
  - firstService.response
```

You can reuse outputs from previously executed services.

---

## 9. Response Section

The `response` section defines the final API response.

### Key-Value Response

```yaml
response:
  - key: message
    value: serviceA.response
```

### Params-Only Response

```yaml
response:
  - serviceA.response
  - serviceB.response.result
```

### Key Resolution Rules
- `executionId.response` → key = `executionId`
- `executionId.response.field` → key = `field`

---

## 10. Full Example

```yaml
/process:
  post:
    services:
      - serviceId: Test Method 1
        executionId: abc
        validator:
          - validatorId: Validator Method 1
            responseType: visible
            params:
              - request.body.value
            message: Validation failed
        params:
          - request.body.value

      - service-group:
          execution-type: parallel
          service-list:
            - serviceId: Test Method 2
              executionId: tm2
              params: []
            - serviceId: Test Method 3
              executionId: tm3
              params: []

    response:
      - key: message
        value: abc.response
      - tm2.response
```

---

## 11. Validation and Errors

Brick automatically:
- Verifies path and method existence
- Ensures serviceId and validatorId exist
- Validates response against OpenAPI schema

Errors are returned if definitions are invalid.

---

## 12. Best Practices

- Keep services small and focused
- Use validators only for validation
- Prefer serial execution unless parallelism is required
- Ensure executionId values are unique
- Keep controller YAML readable and simple

---

## 13. Summary

Controller files are the **heart of request orchestration in Brick**.

They allow you to:
- Define execution flow declaratively
- Control validation and parallelism
- Construct responses without code changes

Happy building with **Brick** 🧱
