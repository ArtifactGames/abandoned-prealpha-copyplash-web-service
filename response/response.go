package response

import (
	"github.com/gin-gonic/gin"
)

func BadRequest(c *gin.Context) {
	c.JSON(400, gin.H{
		"message": "Bad request",
	})
}

func Success(c *gin.Context, message string) {
	c.JSON(200, gin.H{
		"message": message,
	})
}