package middleware

import (
	"context"
	"net/http"
	"time"
)

type ctxKey string

const RequestIDKey ctxKey = "req_id"

func RequestID(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		id := time.Now().UTC().Format("20060102T150405.000000000Z07:00")
		ctx := context.WithValue(r.Context(), RequestIDKey, id)
		next.ServeHTTP(w, r.WithContext(ctx))
	})
}
