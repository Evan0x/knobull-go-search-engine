package main

import (
	"context"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/joho/godotenv"

	"github.com/icecoldsprite1/knobull-go-search-engine/internal/api"
	"github.com/icecoldsprite1/knobull-go-search-engine/internal/store"
)

// This package provides the entrypoint for the HTTP server.
// It implements graceful shutdown to ensure zero-downtime deployments
// by draining active requests and background workers upon receiving SIGTERM/SIGINT.

func main() {
	
	if err := godotenv.Load(); err != nil {
		log.Println("No .env file found, using system environment variables.")
	}

	connStr := os.Getenv("DATABASE_URL")
	if connStr == "" {
		log.Fatal("CRITICAL: DATABASE_URL is not set in the environment")
	}


	postgresStore, err := store.NewPostgresStore(connStr)
	if err != nil {
		log.Fatal("Could not connect to database: ", err)
	}

	engineServer := api.NewEngineServer(postgresStore)

	mux := http.NewServeMux()
	mux.HandleFunc("GET /api/resources", engineServer.HandleGetResources)
	mux.HandleFunc("POST /api/recommend", engineServer.HandleRecommend)
	mux.Handle("/", http.FileServer(http.Dir("public")))

	httpServer := &http.Server{
		Addr:    ":8080",
		Handler: mux,

		ReadTimeout:  10 * time.Second,
		WriteTimeout: 30 * time.Second,
	}

	go func() {
		log.Println("Knobull Engine started on :8080")
		if err := httpServer.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatalf("HTTP server error: %v", err)
		}
	}()

	quit := make(chan os.Signal, 1)

	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)

	sig := <-quit
	log.Printf("Received signal: %s. Beginning graceful shutdown...", sig)

	ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()

	
	if err := httpServer.Shutdown(ctx); err != nil {
		log.Printf("HTTP server forced to shut down: %v", err)
	} else {
		log.Println("HTTP server shut down cleanly.")
	}

	
	engineServer.Shutdown()

	log.Println("All systems shut down. Goodbye.")
}

