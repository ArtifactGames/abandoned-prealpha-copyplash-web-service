package main

import (
	"strconv"
	"github.com/ArtifactGames/copyplash-web-service/database"
	"github.com/ArtifactGames/copyplash-web-service/response"
	"github.com/gin-gonic/gin"
)

const (
	serverPort = ":8081"
)

func createLobby(c *gin.Context) {
	id, password := database.CreateLobby()
	c.JSON(200, gin.H{
		"message":  "Lobby created successfuly",
		"id":       strconv.FormatInt(id, 10),
		"password": strconv.Itoa(password),
	})
}

func enterLobby(c *gin.Context) {
	// THIS IS NOT JSON. IT IS A FORM
	// Send a post form with lobbyID key in it
	clientID, hasClientId := c.GetPostForm("clientID")
	lobbyPassword, hasLobbyPassword := c.GetPostForm("lobbyPassword")

	if !hasClientId || !hasLobbyPassword {
		response.BadRequest(c)
		return
	}

	cleanLobbyPassword, err := strconv.Atoi(lobbyPassword);
	if err != nil {
		response.BadRequest(c);
		return
	}

	if success := database.EnterLobby(clientID, cleanLobbyPassword); !success {
		response.BadRequest(c);
		return
	}

	response.Success(c, "Entered lobby correctly")
}

func destroyLobby(c *gin.Context) {
	// THIS IS NOT JSON. IT IS A FORM
	// Send a post form with lobbyID key in it
	lobbyID, _ := strconv.Atoi(c.PostForm("lobbyID"))

	database.DestroyLobby(lobbyID)

	c.JSON(200, gin.H{
		"message": "Lobby destroyed correctly",
	})
}

func main() {
	// Creates a gin engine
	r := gin.Default()

	database.PrepareDatabase()

	// Subscribes the functions to be executed by POST requests
	r.POST("/createLobby", createLobby)
	r.POST("/enterLobby", enterLobby)
	r.POST("/destroyLobby", destroyLobby)

	r.Run(serverPort) // listen and serve on 0.0.0.0:8080
}
