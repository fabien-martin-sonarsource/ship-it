# Feature Spec: Scenario Administration Page

**Project:** Ship It?
**Type:** New Feature
**Status:** Ready for Implementation

---

## Background

The "Ship It?" application serves scenario-based content to end users through a public-facing interface. As the scenario catalog grows, the team needs a way for administrators to review the full list of active scenarios without accessing the file system or the source code repository.

## Goal

Add a read-only administration page that displays the complete list of sanity check scenarios loaded by the application, protected by HTTP Basic Authentication so that only authorized users can access it.

## Non-Goals

- Editing, creating, or deleting scenarios (read-only)
- Role-based access control
- A dedicated user management system

---

## Functional Requirements

1. A page is available at `/admin/scenarios`.
2. The page lists all scenarios currently loaded in the application, displaying for each:
   - The context
   - The question
   - Option A and Option B labels
3. The page is accessible only to authenticated users with the `ADMIN` role.
4. Unauthenticated requests to `/admin/**` are challenged with HTTP Basic Authentication.
5. All other existing routes (`/`) remain publicly accessible without authentication.

---

## Technical Design

### Security

Use **Spring Security** to configure HTTP Basic Authentication.

Use Spring Security's standard configuration properties to define the admin credentials.

### Controller

Add an `AdminController` in the `infrastructure/controller` package.

- Endpoint: `GET /admin/scenarios`
- Inject `SanityCheckInventory` to retrieve the scenario list
- Pass the list to a Thymeleaf template

### View

Add a Thymeleaf template `admin/scenarios.html` under `src/main/resources/templates/`.

The page should display the scenario list in a readable format (e.g., an HTML table). It does not need to match the styling of the public interface.

### Architecture constraints

- Follow the existing package structure: domain, SPI, infrastructure layers
- Do not modify the domain model or the `SanityCheckInventory` SPI
- The `InMemorySanityCheckInventory` remains the only implementation

---

## Acceptance Criteria

- [ ] `GET /admin/scenarios` returns `401` when no credentials are provided
- [ ] `GET /admin/scenarios` returns `200` with valid admin credentials
- [ ] `GET /` remains accessible without authentication
- [ ] All scenarios from the loaded JSON file are visible on the admin page
- [ ] Unit or integration tests cover the access control rules
