# Feature Spec: Random Sanity Check Pick

**Project:** Ship It?
**Type:** New Feature
**Status:** Ready for Implementation

---

## Background

The "Ship It?" application serves sanity checks one at a time through the public home page (`/`). Each visit cycles to the next check using a session-scoped counter, so users browse the deck sequentially. Some users want to jump to a random check to rediscover scenarios they may have forgotten, without disrupting their sequential progression.

## Goal

Add a "Surprise me" endpoint that returns a random sanity check from the deck, with a small banner indicating that the check was picked at random and where it sits in the full deck.

## Non-Goals

- Avoiding repeats across calls (each pick is independent and uniform)
- Returning several checks at once
- Weighted random or category-based filtering
- A separate visual layout — the existing template is reused

---

## Functional Requirements

1. A page is available at `GET /random`.
2. The page reuses the same visual layout as the home page (`sanity-check.html`).
3. On each request, a check is picked **uniformly at random** from the full deck.
4. Above the question, a banner is displayed:
   > **🎲 Random pick — check X of Y**

   where `X` is the 1-based position of the picked check in the deck and `Y` is the total number of checks.
5. The session counter used by `/` is not affected by hitting `/random`.
6. Two consecutive calls to `/random` should (with high probability) return different checks.
7. All other existing routes (`/`, `/admin/**`) remain unchanged.

---

## Technical Design

### Use Case

Add a new use case `PickRandomSanityCheck` in the `application` package.

- Inject a `RandomGenerator` so that tests can drive the random source with a fixed seed.
- To keep the controller as a thin pass-through, the use case must return a **fully populated, view-ready result** containing both the sanity check ready to be displayed and the banner text.
- For consistency with the existing home page, **reuse the `SanityCheckView` already used by `GuiController`** rather than introducing yet another mapping class.

### Controller

Add a `@Controller` in `infrastructure/controller`:

- `GET /random` invokes the random pick use case.
- The controller forwards the use case result to the Thymeleaf template without additional mapping.

### View

Reuse `templates/sanity-check.html`. Add a conditional block (Thymeleaf `th:if`) above the question:

- If the banner attribute is present in the model, display it.
- The home page (`/`) does not set this attribute, so the banner stays hidden there.

### Architecture constraints

- Follow the existing package structure: `domain`, `application`, `infrastructure`.
- Do not modify the domain model or the `SanityCheckInventory` SPI.
- All Sonar architecture rules must remain green.

---

## Acceptance Criteria

- [ ] `GET /random` returns `200` and renders the sanity-check template
- [ ] The banner `🎲 Random pick — check X of Y` is visible on `/random` with correct `X` and `Y` values
- [ ] The banner is **not** visible on `/`
- [ ] The session counter on `/` is unaffected by visits to `/random`
- [ ] A unit test on the use case uses a seeded `RandomGenerator` to verify a deterministic pick
- [ ] A `@WebMvcTest` covers the new endpoint
- [ ] All existing tests still pass
- [ ] Sonar quality gate is green, including architecture rules
