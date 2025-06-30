# UnderstandAI - Repository Analyzer

A Spring Boot application that analyzes GitHub repositories and provides AI-powered explanations of their structure and purpose.

## Features

- **Repository Analysis**: Clone and analyze any public GitHub repository
- **AI-Powered Explanations**: Get detailed explanations of repository structure and purpose
- **Modern Web Interface**: Beautiful, responsive frontend for easy interaction
- **GitHub Authentication**: Optional GitHub OAuth login to access your own repositories
- **Real-time Processing**: See analysis results as they're generated

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Git (for cloning repositories)

### GitHub OAuth Setup (Optional)

To enable GitHub authentication and access your own repositories:

1. **Create a GitHub OAuth App:**
   - Go to [GitHub Developer Settings](https://github.com/settings/developers)
   - Click "New OAuth App"
   - Fill in the details:
     - **Application name**: UnderstandAI
     - **Homepage URL**: `http://localhost:8080`
     - **Authorization callback URL**: `http://localhost:8080/callback`
   - Click "Register application"

2. **Create a `.env` file in the project root:**
   ```bash
   # GitHub OAuth Configuration
   GITHUBID=your_github_oauth_client_id_here
   GITHUBSECRET=your_github_oauth_client_secret_here
   ```

3. **Replace the placeholder values** with your actual GitHub OAuth App credentials

### Running the Application

1. **Clone and navigate to the project directory:**
   ```bash
   cd UnderstandAI
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the frontend:**
   Open your browser and go to: `http://localhost:8080`

## Usage

### Web Interface

1. Open `http://localhost:8080` in your browser
2. **Optional**: Click "Login with GitHub" to access your repositories
3. Enter a GitHub repository URL (e.g., `https://github.com/spring-projects/spring-boot`)
4. Click "Analyze Repository"
5. Wait for the analysis to complete
6. View the detailed explanation of the repository

### GitHub Authentication Features

- **Login with GitHub**: Click the "Login with GitHub" button in the top-right corner
- **View Your Repositories**: After authentication, click "View My Repositories" to see your GitHub repos
- **Quick Analysis**: Click on any of your repositories to automatically fill the analysis form
- **Logout**: Click the "Logout" button to sign out

### API Endpoint

You can also use the API directly:

```bash
curl -X POST http://localhost:8080/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"repoUrl": "https://github.com/spring-projects/spring-boot"}'
```

## Project Structure

```
src/
├── main/
│   ├── java/com/understandai/understandai/
│   │   ├── controller/
│   │   │   ├── RepoController.java      # REST API endpoints
│   │   │   ├── WebController.java       # Frontend controller
│   │   │   └── GitHubController.java    # GitHub OAuth & user repos
│   │   ├── Service/
│   │   │   ├── GitCloneService.java     # Git repository cloning
│   │   │   ├── RepoScannerService.java  # Repository scanning
│   │   │   └── ExplanationService.java  # AI explanations
│   │   ├── model/
│   │   ├── dto/
│   │   └── config/
│   │       └── WebConfig.java           # CORS configuration
│   └── resources/
│       ├── templates/
│       │   ├── frontend.html            # Main web interface
│       │   └── repos.html               # User repositories page
│       └── application.properties       # Application configuration
```

## Configuration

The application runs on port 8080 by default. You can modify this in `src/main/resources/application.properties`:

```properties
server.port=8080
spring.thymeleaf.cache=false
```

## Example Repositories to Try

- Spring Boot: `https://github.com/spring-projects/spring-boot`
- React: `https://github.com/facebook/react`
- VS Code: `https://github.com/microsoft/vscode`
- TensorFlow: `https://github.com/tensorflow/tensorflow`

## Troubleshooting

- **Port already in use**: Change the port in `application.properties`
- **Repository not found**: Ensure the GitHub URL is correct and the repository is public
- **Analysis fails**: Check that Git is installed and accessible from the command line
- **GitHub authentication fails**: Verify your OAuth App credentials in the `.env` file
- **Missing .env file**: Create a `.env` file with your GitHub OAuth credentials

## Development

To run in development mode with auto-reload:

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"
```

## License

This project is licensed under the MIT License. 