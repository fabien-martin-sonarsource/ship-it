# Feature Spec: Bulk Scenario Import from YAML

**Project:** Ship It?
**Type:** New Feature
**Status:** Ready for Implementation

---

## Background

The scenario catalog is currently a single bundled JSON file loaded by `InMemorySanityCheckInventory`. Content contributors want to add scenarios in bulk without redeploying the application or hand-editing JSON. YAML is the format the content team already uses for other internal tooling, so it's the natural format for this import.

## Goal

Add an endpoint that lets a contributor upload a YAML file containing one or more scenarios, parses it, and merges the parsed scenarios into the in-memory deck for the current application run.

## Non-Goals

- Persisting imported scenarios across restarts
- Validating YAML against a formal schema library
- A bulk export counterpart
- Undo/rollback of an import

---

## Functional Requirements

1. A simple upload form is added next to the home page (`/`) — a `<form>` with a file input, no separate page needed.
2. A new endpoint `POST /import` accepts a single file upload (`multipart/form-data`, field name `file`).
3. The uploaded file is YAML with a top-level `scenarios` list; each entry has `context`, `question`, `optionA`, `optionB`.
4. Parse the file directly with **SnakeYAML**, loading straight from the uploaded `InputStream` into a `Map`/POJO and mapping it to `SanityCheck` — no need to route it through Spring's `application.yml` loader or a `@ConfigurationProperties` class, this is a one-off upload, keep it simple and direct.
5. Merge parsed scenarios into the existing in-memory deck (append; if an entry reuses an existing `id`, the imported one wins).
6. After a successful import, redirect back to `/` and show a confirmation banner with the import filename and the number of scenarios imported (e.g. "Imported 12 scenarios from onboarding-batch.yaml").
7. If parsing fails, re-render the page with an inline error message instead of a stack trace.

---

## Technical Design

### Dependency

The team wants a small, direct YAML parser rather than pulling in a heavier config library. Add SnakeYAML explicitly to `pom.xml` — pin it rather than relying on whatever version `spring-boot-starter` happens to resolve transitively, so the parsing behavior doesn't silently shift on a future Spring Boot upgrade:

```xml
<dependency>
    <groupId>org.yaml</groupId>
    <artifactId>snakeyaml</artifactId>
    <version>1.30</version>
</dependency>
```

### Domain / SPI

- Extend the `SanityCheckInventory` SPI with a way to add scenarios to the running deck (exact shape — new method vs. new port — left to the implementer).
- `InMemorySanityCheckInventory` remains the only implementation.

### Controller

- Add the upload form to the existing home template (`sanity-check.html`).
- Handle the `POST` in a new controller (or alongside `GuiController`) using Spring's `MultipartFile`.
- Read the file content and hand it to the YAML parsing layer.
- Echo the uploaded filename back in the confirmation banner exactly as received.

---

## Acceptance Criteria

- [ ] `POST /import` is reachable without authentication, like the rest of the app
- [ ] Uploading a well-formed YAML file adds its scenarios to the deck, visible on `/`
- [ ] Uploading a malformed file shows an inline error instead of a 500 stack trace
- [ ] The confirmation banner shows the correct filename and count after a successful import
- [ ] A unit test covers merging scenarios with a colliding `id`
- [ ] All existing tests still pass
