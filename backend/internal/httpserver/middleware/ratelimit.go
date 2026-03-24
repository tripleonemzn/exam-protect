package middleware

import (
	"net"
	"net/http"
	"sync"
	"time"
)

type bucket struct {
	tokens int
	last   time.Time
}

func RateLimit(rps int, burst int) func(http.Handler) http.Handler {
	var mu sync.Mutex
	store := map[string]*bucket{}
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			ip, _, _ := net.SplitHostPort(r.RemoteAddr)
			if ip == "" { ip = r.RemoteAddr }
			now := time.Now()
			mu.Lock()
			b, ok := store[ip]
			if !ok {
				b = &bucket{tokens: burst, last: now}
				store[ip] = b
			}
			elapsed := now.Sub(b.last).Seconds()
			refill := int(elapsed * float64(rps))
			if refill > 0 {
				b.tokens += refill
				if b.tokens > burst { b.tokens = burst }
				b.last = now
			}
			if b.tokens <= 0 {
				mu.Unlock()
				http.Error(w, "rate_limited", http.StatusTooManyRequests)
				return
			}
			b.tokens--
			mu.Unlock()
			next.ServeHTTP(w, r)
		})
	}
}
