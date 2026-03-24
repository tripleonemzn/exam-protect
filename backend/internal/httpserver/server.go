package httpserver

import (
	"encoding/json"
	"net/http"
	"strings"

	"github.com/go-chi/chi/v5"
	chimw "github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/cors"

	"exam-protect/backend/internal/config"
	"exam-protect/backend/internal/httpserver/handlers"
	"exam-protect/backend/internal/httpserver/middleware"
	"exam-protect/backend/internal/store"
)

func New(cfg config.Config, db store.API) http.Handler {
	r := chi.NewRouter()
	r.Use(cors.Handler(cors.Options{
		AllowedOrigins:   strings.Split(cfg.HTTP.Origins, ","),
		AllowedMethods:   []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
		AllowedHeaders:   []string{"Accept", "Authorization", "Content-Type"},
		AllowCredentials: false,
		MaxAge:           300,
	}))
	r.Use(middleware.RateLimit(20, 40))
	r.Use(middleware.RequestID)
	r.Use(chimw.Logger)
	r.Get("/v1/health", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(map[string]string{"status": "ok"})
	})
	// Mock exam page for local testing
	r.Get("/mock/exam", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "text/html; charset=utf-8")
		w.Write([]byte(`<!doctype html>
<html><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Mock Exam</title>
<style>body{font-family:system-ui;margin:20px} input,button{font-size:16px;padding:8px;margin:4px 0}</style>
</head>
<body>
<h2>Mock Exam</h2>
<p>Isikan jawaban singkat. Link eksternal diblokir oleh secure shell.</p>
<form onsubmit="event.preventDefault(); alert('Jawaban terkirim (dummy).');">
  <label>Nama</label><br><input placeholder="Nama lengkap"/><br>
  <label>Pertanyaan 1: 2 + 2 = ?</label><br><input placeholder="Jawaban"/><br>
  <button type="submit">Submit</button>
</form>
</body></html>`))
	})

	ah := handlers.NewAdminHandler(db, cfg)
	eh := handlers.NewExamHandler(db, cfg)
	sh := handlers.NewSessionHandler(db, cfg)
	ch := handlers.NewClientHandler(cfg)

	r.Post("/v1/admin/login", ah.Login)

	r.Group(func(ar chi.Router) {
		ar.Use(middleware.AdminAuth(cfg.Auth.AdminJWTSecret))
		ar.Post("/v1/admin/exams", eh.Create)
		ar.Put("/v1/admin/exams/{examId}", eh.Update)
		ar.Get("/v1/admin/exams/{examId}", eh.Get)
		ar.Delete("/v1/admin/exams/{examId}", eh.Delete)
		ar.Get("/v1/admin/exams", eh.List)
	})

	r.Get("/v1/exams/{examId}/config", eh.GetSignedConfig)
	r.Get("/v1/client/bootstrap", ch.Bootstrap)
	r.Get("/v1/client/update", ch.Update)
	// static files for downloads (self-hosted OTA)
	r.Handle("/downloads/*", http.StripPrefix("/downloads/", http.FileServer(http.Dir("./public/downloads"))))
	r.Post("/v1/sessions/start", sh.Start)
	r.Post("/v1/sessions/{sessionId}/heartbeat", sh.Heartbeat)
	r.Post("/v1/sessions/{sessionId}/violations", sh.ReportViolation)
	r.Post("/v1/sessions/{sessionId}/complete", sh.Complete)

	return r
}
