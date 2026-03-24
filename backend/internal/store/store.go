package store

import (
	"context"
	"exam-protect/backend/internal/models"
	"strings"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

type API interface {
	// admins
	GetAdminByUsername(u string) (*Admin, error)
	// policies
	CreatePolicy(p *ExamPolicy) error
	GetPolicy(examID string) (*ExamPolicy, error)
	UpdatePolicy(examID string, p *ExamPolicy) error
	DeactivatePolicy(examID string) error
	// sessions
	StartSession(sess *Session) error
	Heartbeat(sessionID string) error
	AddViolation(sessionID string, v *Violation) error
	Complete(sessionID, reason string) error
	Close()
}

// Re-export models types to avoid import cycles
type Admin = models.Admin
type ExamPolicy = models.ExamPolicy
type Session = models.Session
type Violation = models.Violation

type PGStore struct {
	Pool *pgxpool.Pool
}

func Open(dsn string) (API, error) {
	if strings.HasPrefix(dsn, "mem://") || dsn == "" {
		return NewMemStore(), nil
	}
	cfg, err := pgxpool.ParseConfig(dsn)
	if err != nil {
		return nil, err
	}
	pool, err := pgxpool.NewWithConfig(context.Background(), cfg)
	if err != nil {
		return nil, err
	}
	return &PGStore{Pool: pool}, nil
}

func (s *PGStore) Close() {
	if s.Pool != nil {
		s.Pool.Close()
	}
}

func ctx() (context.Context, context.CancelFunc) {
	return context.WithTimeout(context.Background(), 5*time.Second)
}
