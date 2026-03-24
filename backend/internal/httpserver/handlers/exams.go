package handlers

import (
	"encoding/json"
	"net/http"
	"strings"
	"time"

	"github.com/go-chi/chi/v5"

	"exam-protect/backend/internal/config"
	"exam-protect/backend/internal/models"
	"exam-protect/backend/internal/security"
	"exam-protect/backend/internal/store"
)

type ExamHandler struct {
	db  store.API
	cfg config.Config
}

func NewExamHandler(db store.API, cfg config.Config) *ExamHandler {
	return &ExamHandler{db: db, cfg: cfg}
}

func (h *ExamHandler) Create(w http.ResponseWriter, r *http.Request) {
	var p models.ExamPolicy
	if err := json.NewDecoder(r.Body).Decode(&p); err != nil {
		http.Error(w, "bad_request", http.StatusBadRequest)
		return
	}
	if p.ExamID == "" || p.Title == "" || p.LaunchURL == "" || len(p.AllowedDomains) == 0 {
		http.Error(w, "invalid_policy", http.StatusBadRequest)
		return
	}
	if !(strings.HasPrefix(p.LaunchURL, "https://") || strings.HasPrefix(p.LaunchURL, "http://")) {
		http.Error(w, "invalid_launch_url", http.StatusBadRequest)
		return
	}
	if p.TeacherPinHash == "" {
		p.TeacherPinHash = ""
	}
	if err := h.db.CreatePolicy(&p); err != nil {
		http.Error(w, "conflict", http.StatusConflict)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(p)
}

func (h *ExamHandler) Update(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "examId")
	var p models.ExamPolicy
	if err := json.NewDecoder(r.Body).Decode(&p); err != nil {
		http.Error(w, "bad_request", http.StatusBadRequest)
		return
	}
	if p.Title == "" || p.LaunchURL == "" || len(p.AllowedDomains) == 0 {
		http.Error(w, "invalid_policy", http.StatusBadRequest)
		return
	}
	if !(strings.HasPrefix(p.LaunchURL, "https://") || strings.HasPrefix(p.LaunchURL, "http://")) {
		http.Error(w, "invalid_launch_url", http.StatusBadRequest)
		return
	}
	if err := h.db.UpdatePolicy(id, &p); err != nil {
		http.Error(w, "not_found", http.StatusNotFound)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(p)
}

func (h *ExamHandler) Get(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "examId")
	p, err := h.db.GetPolicy(id)
	if err != nil {
		http.Error(w, "not_found", http.StatusNotFound)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(p)
}

func (h *ExamHandler) Delete(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "examId")
	if err := h.db.DeactivatePolicy(id); err != nil {
		http.Error(w, "not_found", http.StatusNotFound)
		return
	}
	w.WriteHeader(http.StatusNoContent)
}

func (h *ExamHandler) List(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode([]any{})
}

type signedResp struct {
	ExamID string `json:"examId"`
	JWS    string `json:"jws"`
}

func (h *ExamHandler) GetSignedConfig(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "examId")
	p, err := h.db.GetPolicy(id)
	if err != nil {
		http.Error(w, "not_found", http.StatusNotFound)
		return
	}
	var token string
	if h.cfg.Auth.LaunchSigned {
		token, _ = security.SignLaunchPayload(h.cfg.Auth.LaunchJWTSecret, p, 15*time.Minute)
	} else {
		b, _ := json.Marshal(p)
		token = string(b)
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(signedResp{ExamID: id, JWS: token})
}
