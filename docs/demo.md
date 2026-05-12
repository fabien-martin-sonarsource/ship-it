# Demo

## AI Code generation

### Code generation

```
Create a new branch starting from demo named feature/read-only-administration 
On this branch implement the feature defined in docs/prompt.md
Once it's done, create a commit and push your branch.
```

### Make a PR

```powershell

git checkout feature/read-only-administration

gh pr create --base demo --title "Add read-only administration page for scenarios" --body @"
## Summary
- Add ``GET /admin/scenarios`` protected by HTTP Basic Authentication (role ``ADMIN``)
- Add Spring Security configuration restricting ``/admin/**`` to authenticated admins
- Add Thymeleaf template displaying the full scenario list in a table
- Public route ``/`` remains accessible without authentication

## Test plan
- [ ] ``GET /admin/scenarios`` returns ``401`` without credentials
- [ ] ``GET /admin/scenarios`` returns ``200`` with admin credentials
- [ ] ``GET /admin/scenarios`` returns ``403`` with non-admin user
- [ ] ``GET /`` remains publicly accessible
"@
```

## MCP Server

Claude Code supports **MCP (Model Context Protocol)** servers, which expose tools that Claude can use directly within the conversation.

### The `/mcp` command

The `/mcp` command lists connected servers and their available tools.

```
> /mcp
```

**Output:**

```
● sonarqube  (17 tools)

   change_sonar_issue_status        open-world
   search_my_sonarqube_projects     read-only, open-world
   search_sonar_issues_in_projects  read-only, open-world
   ...
```

### Discuss directly with Sonar

```
What are the Sonarqube issues of the PR related to the feature/read-only-administration branch?
What is the Sonar project status? Present it in a way that I can share with my CISO.
What's your opinion regarding the Opened Security Hotspot you found?
```

### Clean Up

- Close PR on Github
- Delete PR branch on Sonarqube
