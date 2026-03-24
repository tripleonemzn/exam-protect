package handlers

import (
	"encoding/json"
	"io"
	"log"
	"net/http"
	"time"

	"github.com/golang-jwt/jwt/v5"

	"exam-protect/backend/internal/config"
	"exam-protect/backend/internal/security"
	"exam-protect/backend/internal/store"
)

type AdminHandler struct {
	db  store.API
	cfg config.Config
}

func NewAdminHandler(db store.API, cfg config.Config) *AdminHandler {
	return &AdminHandler{db: db, cfg: cfg}
}

type loginReq struct {
	Username string `json:"username"`
	Password string `json:"password"`
}
type loginResp struct {
	Token string `json:"token"`
}

func (h *AdminHandler) Login(w http.ResponseWriter, r *http.Request) {
	var req loginReq
	b, err := io.ReadAll(r.Body)
	if err != nil {
		http.Error(w, "bad_request", http.StatusBadRequest)
		return
	}
	if err := json.Unmarshal(b, &req); err != nil {
		log.Printf("admin login decode failed: %v body=%q", err, string(b))
		http.Error(w, "bad_request", http.StatusBadRequest)
		return
	}
	a, err := h.db.GetAdminByUsername(req.Username)
	if err != nil || !a.Active {
		http.Error(w, "unauthorized", http.StatusUnauthorized)
		return
	}
	if !security.CheckPassword(a.PassHash, req.Password) {
		http.Error(w, "unauthorized", http.StatusUnauthorized)
		return
	}
	claims := jwt.MapClaims{
		"sub":  a.Username,
		"iat":  time.Now().Unix(),
		"exp":  time.Now().Add(2 * time.Hour).Unix(),
		"role": "admin",
	}
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	s, _ := token.SignedString([]byte(h.cfg.Auth.AdminJWTSecret))
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(loginResp{Token: s})
}
