package security

import (
	"testing"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

func TestSignLaunchPayload(t *testing.T) {
	secret := "test"
	payload := map[string]any{"examId":"x","launchUrl":"https://example.com/exam"}
	s, err := SignLaunchPayload(secret, payload, time.Minute)
	if err != nil { t.Fatalf("err: %v", err) }
	parsed, err := jwt.Parse(s, func(tk *jwt.Token) (any, error) { return []byte(secret), nil })
	if err != nil || !parsed.Valid { t.Fatalf("invalid token") }
}
