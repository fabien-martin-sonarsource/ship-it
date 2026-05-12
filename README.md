# Ship It?

[![AI Code Assurance](https://sonar.beenotice.com/api/project_badges/ai_code_assurance?project=beeNotice_ship-it_c906d80a-4d85-4318-81db-ec8563e8c48b&token=sqb_744df8094eb2eabb89a23582bbc651be347ee2ab)](https://sonar.beenotice.com/dashboard?id=beeNotice_ship-it_c906d80a-4d85-4318-81db-ec8563e8c48b)

An interactive demo app that puts you in the shoes of a developer making real-world decisions about code quality, security, and engineering practices.

Each screen presents a realistic scenario with two choices — and shows you the consequences. Should you ship it?

## Tech Stack

- Java 21 / Spring Boot 4
- Thymeleaf
- Maven

## Run Locally

```bash
./mvnw spring-boot:run
```

Then open [http://localhost:8080](http://localhost:8080).

## How It Works

The app cycles through a set of scenarios loaded from `src/main/resources/data/sanity-checks.json`. Each scenario has a context, a question, and two decisions with their respective consequences. The cycle is infinite — click through as many times as you like.

To add or edit scenarios, just update the JSON file.

## Sonar Integration

### Analysis

SonarCloud's automatic analysis does not run tests, so code coverage won't be reported. To enable it, disable automatic analysis and set up a CI pipeline that runs `mvn verify sonar:sonar`.

The `pom.xml` already includes JaCoCo and the required Sonar properties. See the [SonarCloud CI guide](https://docs.sonarsource.com/sonarcloud/advanced-setup/ci-based-analysis/sonarscanner-for-maven/) for setup instructions.

### GitHub Actions setup

The workflow (`.github/workflows/main.yml`) requires one **secret** and three **variables** configured in your GitHub repository (*Settings > Secrets and variables > Actions*).

**Secret** (`Settings > Secrets and variables > Actions > Secrets`):

| Name | Description |
|------|-------------|
| `SONAR_TOKEN` | SonarQube/SonarCloud authentication token |

**Variables** (`Settings > Secrets and variables > Actions > Variables`):

| Name | Example value                       | Description |
|------|-------------------------------------|-------------|
| `SONAR_HOST_URL` | `https://sonarcloud.io`             | URL of your SonarQube instance |
| `SONAR_ORGANIZATION` | `fabien-martin-sonarsource`         | SonarCloud organization key |
| `SONAR_PROJECT_KEY` | `fabien-martin-sonarsource_ship-it` | Project key shown in SonarQube |

### MCP to connect SonarQube Server

The MCP server is configured at the project level via `.mcp.json` — no global installation needed.

**1. Set your credentials** in `private/.env` (gitignored):

```env
SONARQUBE_TOKEN=your_sonar_token_here
SONARQUBE_URL=https://sonar.beenotice.com
```

**2. Load the env file before starting Claude:**

PowerShell:
```powershell
Get-Content private/.env | ForEach-Object {
    $parts = $_ -split '=', 2
    [System.Environment]::SetEnvironmentVariable($parts[0], $parts[1])
}
claude

What are the sonarqube issues on the PR ?

```


The `.mcp.json` at the project root registers the SonarQube MCP server automatically for this project. The `SONARQUBE_TOKEN` and `SONARQUBE_URL` variables are passed through to the Docker container at runtime.

Reference: https://docs.sonarsource.com/sonarqube-mcp-server/quickstart-guide

