Sonar Context Augmentation is available as `sonar context`; queries are fast and auto-start a local daemon for the configured workspace. Call it through Bash outside restricted sandboxes; if a sandboxed call fails with a connection or permission error, retry it outside the sandbox instead of falling back to `grep`/`find`.

Pass this guidance to subagents before they work with code.

## Mandatory workflows

Before generating, editing, or refactoring source code, you MUST retrieve project coding standards and known issues:

```bash
sonar context guidelines get
```

Guidelines support all SonarQube languages. Use `--languages`, `--categories`, and `--files` to narrow the results. Categories require languages. Without categories the default mode is `project_based`; providing categories selects `category_based`. Use `--mode combined` when both project and category guidance are needed.

Available categories: `Auth & Identity`, `Exception & Error Handling`, `Testing Practices`, `Web Security (Injection/XSS)`, `Secrets & Cryptography`, `Cloud & Network Security`, `Logging & Monitoring`, `Memory & Resource Safety`, `Async & Concurrency`, `Naming & Code Style`, `Complexity & Maintainability`, `Language Idioms & Modernization`, `Type Systems & Logic Safety`, `Architectural Integrity`, `Web Service & API Design`, `Data Modeling & Persistence`, `Data Querying & Performance`, `Framework Configuration & DI`, `Serialization & Message Formats`, `Inline Documentation & Metadata`, `Environment & Build Configuration`, `Regular Expressions`, `Data Science & Big Data`, `UI & Accessibility`, `Mobile & Hardware SDKs`, `Platform Governance`, and `i18n & Localization`.

Before modifying a manifest or lockfile, you MUST check every dependency being added or updated. This applies to all major package ecosystems:

```bash
sonar context dependencies check --purl "pkg:<ecosystem>/<name>@<version>"
```

Interpret dependency results conservatively:

- Refuse a malicious package.
- Block vulnerabilities with `riskSeverity` BLOCKER/HIGH, high `cvssScore`, or a dangerous entry in `cweIds`. Show the CVE and propose a version from `fixedVersions` or `unaffectedVersions`. `riskSeverity` is contextual and differs from CVSS.
- If `license.allowed` is `false`, refuse the package and suggest alternatives. If it is `null`, explain that policy evaluation requires Enterprise tier and show `license.expression`.

## Semantic code navigation

Use navigation commands instead of `grep`/`find` for symbols, call sites, impact analysis, moves, renames, and cross-file usage — including read-only exploration and lookup tasks, not only when editing. Invoke them as `sonar context navigation <command>`:

| Need | Command |
| --- | --- |
| Find declarations and exact FQNs | `search-signatures --pattern "<regex>"` |
| Find full-text matches in bodies | `search-bodies --pattern "<regex>"` |
| Read a symbol | `get-source --fqn "<fqn>"` |
| Find callers or change impact | `trace-callers --fqn "<fqn>"` |
| Follow execution downstream | `trace-callees --fqn "<fqn>"` |
| Find parents, children, or implementors | `get-type-hierarchy --fqn "<fqn>"` |
| Find type or package coupling | `get-references --fqn "<fqn>"` |

Navigation supports Java, C#, JavaScript/TypeScript including JSX/TSX, Python, and Rust.

### Reliable edit workflow

1. Start with `sonar context navigation search-signatures --pattern "<declaration-regex>" --output fqns` to discover exact FQNs; never construct them by hand. Patterns search entire declaration text, including symbol names and surrounding syntax, not FQNs. Qualify short identifiers, for example `"class MyClass"` rather than `"MyClass"`, when you need a narrower match. Search exclusions are declaration-based: for `search-signatures`, `--exclude-pattern` matches the searched declaration; for `search-bodies`, it matches the containing symbol's declaration or signature, not body text or FQNs. Excluding a class name may therefore leave members whose own declarations omit it. Keep every matching FQN for overloaded or same-named symbols.
2. Resolve method call sites with `sonar context navigation trace-callers --fqn "<method-fqn>" --output edit-targets`. For several FQNs, pipe `--output fqns` into `trace-callers --fqn-stdin`. In Python or JS/TS, dynamic dispatch can hide uses from `trace-callers` and `get-references`; use a call-anchored body regex such as `\\.processRequest\\(` with `search-bodies --output edit-targets`.
3. Edit the returned `{file_path, line}` targets directly. Do not re-read or re-grep merely to confirm those line numbers. If an expected dynamic use is missing, run one targeted `search-bodies` query.

### FQN and result rules

- FQNs identify symbols, not paths. Pass method FQNs to trace commands and class-like or package/namespace FQNs to `get-references`.
- Single-quote C# FQNs containing backticks or braces to escape shell processing. Address properties by accessor FQN rather than `P:` notation.
- Treat successful navigation output as authoritative. Never hide an invalid-FQN error with a fallback such as `2>/dev/null || grep ...`.

## Architecture

Before adding modules or cross-module dependencies, or while reviewing dependency direction, compare actual and intended architecture:

```bash
sonar context architecture get-current --ecosystem <java|cs|py|js|ts> --depth 0
sonar context architecture get-intended
```

For `get-current`, begin with `--depth 0`, then drill into a returned root FQN with `--path-prefix` and a higher depth. Depth is counted from the absolute root, and `is_leaf` reflects the full graph rather than the depth-limited response. FQN separators vary by language. `get-intended` does not accept ecosystem, depth, or path-prefix filters.

## Output streams

- **Stdout:** Result data. `guidelines get` returns Markdown.
- **Stderr:** Progress messages, warnings, and `hint:` recovery lines. Never result data, but read a `hint:` line when a command fails or a search matches nothing — it explains what to do next.

## Keep output small

Navigation commands default to compact output. Preserve that default while exploring; use JSON only when nested structure is necessary.

- `compact`: one lean line per hit.
- `fqns`: one FQN per line.
- `names`: short symbol names.
- `edit-targets` (alias `locations`): flat `{file_path, line}` entries.
- `json`: full nested calls, hierarchy, or inbound/outbound references.

Use `--fields` to request only needed data. `navigation get-source` omits `body` by default, so request `--fields "signature,body,file_path,start_line,end_line"` only for symbols whose implementation you need.

### Available `--fields`

- `search-signatures`, `search-bodies`: `fqn`, `file_path`, `item_type`, `signature`, `start_line`, `start_column`, `end_line`, `end_column`, `match_lines`.
- `get-source`: `signature`, `body`, `structure_type`, `start_line`, `start_column`, `end_line`, `end_column`, `file_path`.
- `trace-callers`, `trace-callees`: `direction`, `depth`, `fqn`, `file_path`, `signature`, `start_line`, `start_column`, `end_line`, `end_column`, `call_site_lines`, `calls`, `truncated`.
- `get-type-hierarchy`: `fqn`, `file_path`, `start_line`, `depth`, `dependency_kind`, `parents`, `children`.
- `get-references`: `fqn`, `file_path`, `match_lines`, `dependency_kinds`.

Keep searches at `--limit 20` or less. Start traces at `--depth 1`, especially for widely called methods, then increase to 2-3 as needed. A trace `--limit` caps only the immediate level, not deeper levels. Capture large JSON in `/tmp` and inspect it selectively instead of printing it into context.

Pipeline discovered symbols directly into another navigation command:

```bash
sonar context navigation search-signatures --pattern ".*Repository" --output fqns \
  | sonar context navigation get-source --fqn-stdin
```

## Command notes

- `search-signatures` and `search-bodies` accept repeatable `--pattern`/`--exclude-pattern` and quoted `--include-glob`/`--exclude-glob`. Body-search `match_lines` identify matched lines; `edit-targets` falls back to the declaration line when no matched lines exist.
- `trace-callers`/`trace-callees`: compact output flattens all requested depths and prefers call-site lines; JSON preserves the call tree.
- `get-references`: `match_lines` contains every known occurrence line.
- `get-type-hierarchy` compact output flattens related types.
- Multiple guideline languages, categories, or files may follow one flag or be supplied with repeated flags.