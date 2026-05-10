# Agile Project Manager — Server

This project is the backend for a browser-based agile project management tool designed for software teams. It provides a RESTful API that handles authentication, project and task management, sprint planning, and multilingual content translation.
The server acts as the single source of truth for all application data and is consumed by the client application. Its main goal is to support a lightweight, self-hostable alternative to tools like Jira, while enabling smooth collaboration for international teams through automatic translation.

---

## Technologies Used

| Technology | Purpose                              |
|---|--------------------------------------|
| [Spring Boot 4](https://spring.io/projects/spring-boot) | Application framework and embedded server |
| [Java 17](https://openjdk.org/projects/jdk/17/) | Programming language                 |
| [Gradle](https://gradle.org/) | Build and dependency management      |
| [Spring Data JPA / Hibernate](https://spring.io/projects/spring-data-jpa) | ORM and database abstraction         |
| [H2](https://h2database.com/) | In-memory relational database        |
| [MapStruct](https://mapstruct.org/) | DTO ↔ entity mapping                 |
| [HuggingFace Inference API](https://huggingface.co/inference-api) | Machine-translation for multilingual content |
| [Google App Engine](https://cloud.google.com/appengine) | Production deployment                |
| [SonarCloud](https://sonarcloud.io/) | Code quality and test coverage       |
| [Spring WebSocket](https://docs.spring.io/spring-framework/reference/web/websocket.html) | Real-time board update support |
---

## High-Level Components

### 1. User Management - [`UserController`](src/main/java/ch/uzh/ifi/hase/soprafs26/controller/UserController.java) & [`UserService`](src/main/java/ch/uzh/ifi/hase/soprafs26/service/UserService.java)

Handles user registration, login, logout, and profile updates. After a successful login, a UUID authentication token is generated, stored on the `User` entity, and returned to the client. This token must be included in all protected requests. The service also tracks user status (online / offline) and the user's preferred language (stored as an ISO 639-1 code), which drives the translation feature.

### 2. Project & Member Management - [`ProjectController`](src/main/java/ch/uzh/ifi/hase/soprafs26/controller/ProjectController.java) & [`ProjectService`](src/main/java/ch/uzh/ifi/hase/soprafs26/service/ProjectService.java)

Projects are the main organisational unit. Each project has an owner, a list of members, and contains tasks, sprints, and tags. The service enforces access rules and ensures that only authorized users can modify project data. It also handles cascading operations such as deleting all related resources when a project is removed. Endpoints let clients retrieve projects filtered by user (`GET /projects/users/{userId}`) as well as the full project details including nested resources.

### 3. Task Lifecycle - [`TaskController`](src/main/java/ch/uzh/ifi/hase/soprafs26/controller/TaskController.java) & [`TaskService`](src/main/java/ch/uzh/ifi/hase/soprafs26/service/TaskService.java)

Tasks represent individual units of work within a project. Each task belongs to a project, has a status (TODO / IN PROGRESS / DONE), priority, time estimate, due date, and assigned users. The service manages task creation, updates, and state transitions, ensuring that only project members can be assigned and that all constraints are respected.

### 4. Sprint Planning - [`SprintController`](src/main/java/ch/uzh/ifi/hase/soprafs26/controller/SprintController.java) & [`SprintService`](src/main/java/ch/uzh/ifi/hase/soprafs26/service/SprintService.java)

Sprints group tasks into time-boxed work periods. Each sprint has a status (PLANNED / ACTIVE / COMPLETED), start date, and end date. Endpoints `GET /projects/{id}/sprints` allow the client to retrieve and manage sprints, enabling features such as sprint selection and progress tracking in the dashboard.

### 5. Translation Service - [`TranslationService`](src/main/java/ch/uzh/ifi/hase/soprafs26/service/TranslationService.java)

Translates text between languages using the HuggingFace Inference API. Rather than a dedicated translation model, the service sends requests to `Qwen/Qwen2.5-7B-Instruct`, a large language model prompted to act as a professional translator. When a task or project is viewed, the client sends the text along with a source and target language: the service constructs the prompt, calls the API, and returns the translated content. The HuggingFace token is injected at runtime via the `huggingface.api.token` property so it is never committed to source control.

---

## Launch & Deployment

### Prerequisites

- **Java 17** — confirm with `java -version`. On Windows set `JAVA_HOME` to the JDK 17 path.
- **Git**
- Recommended IDE: [IntelliJ IDEA](https://www.jetbrains.com/idea/) (free educational licence available) or VS Code with the Spring Boot Extension Pack.

### Running locally

```bash
# 1. Clone the repository
git clone https://github.com/sopra-fs26-group-37/sopra-fs26-group-37-server
cd sopra-fs26-group-37-server

# 2. (Optional) set the HuggingFace token for the translation feature
export HUGGINGFACE_API_TOKEN=hf_your_token_here

# 3. Build the project
./gradlew build          # macOS / Linux
./gradlew.bat build      # Windows

# 4. Start the server
./gradlew bootRun
```

The server starts on **`http://localhost:8080`**.  
The H2 console is available at **`http://localhost:8080/h2-console`** (JDBC URL: `jdbc:h2:mem:testdb`, user: `sa`, no password).

### Development mode (auto-reload)

Open two terminals:

```bash
# Terminal 1 — watch for file changes and rebuild
./gradlew build --continuous -xtest

# Terminal 2 — run the server
./gradlew bootRun
```

### Running the tests

```bash
./gradlew test
```

Test results are written to `build/reports/tests/test/index.html`.  
Coverage is reported by JaCoCo to `build/reports/jacoco/`.

The test suite contains:

- **Unit tests** for every controller and service (MockMvc + Mockito)
- **Integration tests** for all five repositories and services (embedded H2, full Spring context)

### External dependencies

| Dependency | Required for | How to configure |
|---|---|---|
| HuggingFace Inference API | Translation feature | Set `HUGGINGFACE_API_TOKEN` environment variable |
| H2 in-memory DB | All other features | No setup needed — embedded in the application |

No external database process needs to be running. H2 starts automatically with Spring Boot and is wiped on each restart.

### API testing

Use [Postman](https://www.postman.com/) or any HTTP client to hit the endpoints. All responses are JSON. Authentication is token-based: include the token returned by `POST /login` as an `Authorization` header on subsequent requests.

### Releases & deployment

Every push to `main` triggers the following GitHub Actions workflows:

| Workflow | File | What it does |
|---|---|---|
| Test + SonarCloud + Deploy | [`.github/workflows/main.yml`](.github/workflows/main.yml) | Runs tests, uploads coverage to SonarCloud, deploys to Google App Engine |
| Docker build & push | [`.github/workflows/dockerize.yml`](.github/workflows/dockerize.yml) | Pushes a new image to DockerHub |
| PR checks | [`.github/workflows/pr.yml`](.github/workflows/pr.yml) | Builds and tests every pull request |

The production deployment target is **Google App Engine** (Java 17, F2 instance class). Configuration lives in [`app.yaml`](app.yaml). The required GitHub secrets for deployment are `GCP_SERVICE_CREDENTIALS` and `HUGGINGFACE_API_TOKEN`.

To build and run the server as a Docker container locally:

```bash
docker build -t agile-server .
docker run -p 8080:8080 -e HUGGINGFACE_API_TOKEN=hf_your_token agile-server
```

---

## Roadmap

1. **Email notifications for task assignments and sprint deadlines:** Users currently have no way to be alerted outside the browser. Integrating Spring Mail (e.g. with SendGrid or SMTP) to send notifications when a task is assigned or a sprint is about to end would meaningfully close the feedback loop for async teams.

---

## Authors and Acknowledgements
Marko Milojevic, Joanne Azariah, Sejla Husakovic, Leonardo D'Antonio

---

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE.txt) file for full details.