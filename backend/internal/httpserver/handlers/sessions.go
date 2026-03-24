package handlers

import (
	"encoding/json"
	"net/http"
	"time"

	"github.com/go-chi/chi/v5"
	"github.com/google/uuid"

	"exam-protect/backend/internal/config"
	"exam-protect/backend/internal/models"
	"exam-protect/backend/internal/security"
	"exam-protect/backend/internal/store"
)

type SessionHandler struct {
	db  store.API
	cfg config.Config
}

func NewSessionHandler(db store.API, cfg config.Config) *SessionHandler {
	return &SessionHandler{db: db, cfg: cfg}
}

type startReq struct {
	DeviceID     string         `json:"deviceId"`
	ExamID       string         `json:"examId"`
	Mode         string         `json:"mode"`
	AppVersion   string         `json:"appVersion"`
	Capabilities map[string]any `json:"capabilities"`
	PolicyHash   string         `json:"policyHash"`
	EntryPin     string         `json:"entryPin"`
}
type startResp struct {
	SessionID        string `json:"sessionId"`
	HeartbeatSeconds int    `json:"heartbeatSeconds"`
	ServerTime       string `json:"serverTime"`
	LaunchURL        string `json:"launchUrl"`
}

func (h *SessionHandler) Start(w http.ResponseWriter, r *http.Request) {
	var req startReq
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "bad_request", http.StatusBadRequest)
		return
	}
	p, err := h.db.GetPolicy(req.ExamID)
	if err != nil {
		http.Error(w, "not_found", http.StatusNotFound)
		return
	}
	if p.EntryPinRequired {
		if p.EntryPinHash == "" || !security.CheckPassword(p.EntryPinHash, req.EntryPin) {
			http.Error(w, "unauthorized", http.StatusUnauthorized)
			return
		}
	}
	// decide variant launch URL
	chosenURL := p.LaunchURL
	if p.AutoVariant {
		if p.MobileLaunchURL != "" {
			chosenURL = p.MobileLaunchURL
		} else if p.DesktopLaunchURL != "" {
			chosenURL = p.DesktopLaunchURL
		}
	}
	id := uuid.New().String()
	sess := models.Session{
		ID:         id,
		ExamID:     req.ExamID,
		DeviceID:   req.DeviceID,
		Mode:       req.Mode,
		AppVersion: req.AppVersion,
	}
	if err := h.db.StartSession(&sess); err != nil {
		http.Error(w, "server_error", http.StatusInternalServerError)
		return
	}
	resp := startResp{
		SessionID:        id,
		HeartbeatSeconds: p.HeartbeatSeconds,
		ServerTime:       time.Now().UTC().Format(time.RFC3339),
		LaunchURL:        chosenURL,
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(resp)
}

type hbReq struct {
	Timestamp  string           `json:"timestamp"`
	Violations int              `json:"violations"`
	Events     []map[string]any `json:"events"`
	Metrics    map[string]any   `json:"metrics"`
	Nonce      string           `json:"nonce"`
}
type hbResp struct {
	Status     string `json:"status"`
	Action     string `json:"action"`
	ServerTime string `json:"serverTime"`
}

func (h *SessionHandler) Heartbeat(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "sessionId")
	var req hbReq
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "bad_request", http.StatusBadRequest)
		return
	}
	_ = h.db.Heartbeat(id)
	resp := hbResp{Status: "ok", Action: "none", ServerTime: time.Now().UTC().Format(time.RFC3339)}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(resp)
}

type violationReq struct {
	Type   string  `json:"type"`
	Detail *string `json:"detail"`
	TS     string  `json:"ts"`
}

func (h *SessionHandler) ReportViolation(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "sessionId")
	var req violationReq
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "bad_request", http.StatusBadRequest)
		return
	}
	v := models.Violation{SessionID: id, Type: req.Type, Detail: req.Detail}
	if err := h.db.AddViolation(id, &v); err != nil {
		http.Error(w, "server_error", http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusAccepted)
	json.NewEncoder(w).Encode(map[string]bool{"accepted": true})
}

type completeReq struct {
	EndedReason string         `json:"endedReason"`
	Violations  int            `json:"violations"`
	Summary     map[string]any `json:"summary"`
}
type completeResp struct {
	Status      string `json:"status"`
	Certificate string `json:"certificate"`
}

func (h *SessionHandler) Complete(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "sessionId")
	var req completeReq
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "bad_request", http.StatusBadRequest)
		return
	}
	if err := h.db.Complete(id, req.EndedReason); err != nil {
		http.Error(w, "server_error", http.StatusInternalServerError)
		return
	}
	resp := completeResp{Status: "completed", Certificate: ""}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(resp)
}
