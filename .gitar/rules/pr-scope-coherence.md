---
title: PR Scope ↔ Title Coherence
description: Flags PRs whose diff touches files outside the scope advertised in the title
when: PR description is created or updated
actions: Read the title and the diff, flag scope mismatches
---

# PR Scope ↔ Title Coherence

A clear PR title is a promise about the diff. When the diff goes beyond what
the title advertises, reviewers lose the ability to scope-check the change at a
glance, and unrelated risk slips into the codebase under cover of an unrelated
review.

## What to check

1. Read the PR **title** — what change does it announce? (e.g. "Add admin
   page", "Refactor auth middleware", "Fix typo in README").
2. Read the **diff**: list the touched files and what each one changes.
3. Decide whether every modification falls under the umbrella of the title.

## Pass criteria

- Every modified file contributes directly to the change advertised in the
  title, **or**
- Any file that does not is explicitly justified in the description (e.g.
  "bumped X because Y is required for the new endpoint").

## Fail criteria

A file change is unrelated to the title and not called out in the description.
Examples:

- Title says "Add admin page" but `pom.xml` adds an unrelated dependency, and
  the description doesn't mention it.
- Title says "Fix login redirect" but the diff also reformats `UserService.java`
  with no rationale.
- Title says "Update README" but the diff modifies production source files.

## What to report

When failing, name **each off-scope file** and propose one of:

- Split the unrelated change into its own PR.
- Update the title to reflect the broader scope (e.g. "Add admin page + security
  configuration").
- Add a sentence in the description explaining why the off-scope change belongs
  in this PR.

## What NOT to flag

- Build/config files implied by the title (e.g. `pom.xml` adding
  `spring-boot-starter-security` for a security-related PR).
- Tests that mirror production code already in scope.
- Generated files (e.g. lockfiles regenerated alongside a dependency bump).
- Trivial collateral edits (a one-line import re-order in a file genuinely
  touched by the feature).

## Why this rule

Scope creep is the most reliable way for unintended changes to land. A PR
review optimised for "Add admin page" will not catch a silent dependency bump
hidden in the same diff. Keeping titles honest, or splitting the diff, restores
the reviewer's ability to do their job.
