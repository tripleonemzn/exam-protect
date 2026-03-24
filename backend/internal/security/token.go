package security

import (
	"encoding/json"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

func SignLaunchPayload(secret string, payload any, ttl time.Duration) (string, error) {
	m := map[string]any{}
	b, _ := json.Marshal(payload)
	_ = json.Unmarshal(b, &m)
	claims := jwt.MapClaims{
		"policy": m,
		"iat": time.Now().Unix(),
		"exp": time.Now().Add(ttl).Unix(),
	}
	t := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return t.SignedString([]byte(secret))
}
