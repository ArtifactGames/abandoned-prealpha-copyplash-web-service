# (Abandoned) Copyplash Web Service

A web service developed in Kotlin, with the Spring Boot Framework.

## Requirements
  - Gradle (Optional)

## Running the project

 - Clone the repository:
   ```bash
   git clone https://github.com/ArtifactGames/copyplash-web-service.git
   ```
 - cd into the project folder:
   ```bash
   cd copyplash-web-service
   ```
 - launch:
 
   ```bash
   # with Gradle cli
   gradle bootRun
   ```
   ```
   # with Gradle wrapper
   ./gradlew bootRun
   ```
   ```
   # On windows
   ./gradlew.bat bootRun
   ```
   ```
   # Use a different port
    SERVER_PORT=8081 gradle bootRun
   ```

### Running with configuration profile
```bash
SPRING_PROFILES_ACTIVE=[prod|dev] gradle bootRun
```

The default profile is `dev`, you can check the profile configuration inside the `src/main/resources/application-*.yaml` files
