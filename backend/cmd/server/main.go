package main

import (
	"context"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"exam-protect/backend/internal/config"
	"exam-protect/backend/internal/httpserver"
	"exam-protect/backend/internal/store"
)

func main() {
	cfg := config.Load()
	db, err := store.Open(cfg.DB.DSN)
	if err != nil {
		log.Fatalf("db: %v", err)
	}
	defer db.Close()

	srv := httpserver.New(cfg, db)

	server := &http.Server{
		Addr:              cfg.HTTP.Addr,
		Handler:           srv,
		ReadHeaderTimeout: 10 * time.Second,
	}

	go func() {
		if err := server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatalf("http: %v", err)
		}
	}()

	stop := make(chan os.Signal, 1)
	signal.Notify(stop, syscall.SIGINT, syscall.SIGTERM)
	<-stop

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()
	_ = server.Shutdown(ctx)
}
