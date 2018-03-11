# Copyplash Web Service

A web service developed in Go (Golang), with the Gin Web Framework.

## Requirements
 - Go
 - Gcc (required by sqlite dep)

## Workspace setup

Manual setup:
 - Create the following folder structure inside your workspace: `github.com/ArtifactGames`.
 - Clone the repository inside the folder structure you just created:
   ```bash
   git clone https://github.com/ArtifactGames/copyplash-web-service.git
   ```

Go setup (alternative):
 - Run `go get github.com/ArtifactGames/copyplash-web-service`

## Running the project
 - cd into the project folder: 
   ```bash
   cd github.com/ArtifactGames/copyplash-web-service
   ``` 
 - run `go get` to install the dependencies
 - run `go run main.go`

*note*: If you get any error during the run command related to dependencies location, 
you need to update your `GOPATH` env variable to let the go compiler know where the 
project dependencies are located:
```bash
  export GOPATH=[YOUR_WORKSPACE_PATH]
```  
