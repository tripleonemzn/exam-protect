package store

import (
	"errors"
	"sync"
	"time"

	"exam-protect/backend/internal/security"
)

type MemStore struct {
	mu         sync.RWMutex
	admins     map[string]*Admin
	policies   map[string]*ExamPolicy
	sessions   map[string]*Session
	violations map[string][]*Violation
}

func NewMemStore() *MemStore {
	ms := &MemStore{
		admins:     map[string]*Admin{},
		policies:   map[string]*ExamPolicy{},
		sessions:   map[string]*Session{},
		violations: map[string][]*Violation{},
	}
	// seed admin: admin / password
	hash, _ := security.HashPassword("password")
	ms.admins["admin"] = &Admin{
		ID:        1,
		Username:  "admin",
		PassHash:  hash,
		Active:    true,
		CreatedAt: time.Now().UTC(),
	}
	return ms
}

func (m *MemStore) Close() {}

func (m *MemStore) GetAdminByUsername(u string) (*Admin, error) {
	m.mu.RLock()
	defer m.mu.RUnlock()
	a, ok := m.admins[u]
	if !ok {
		return nil, errors.New("not found")
	}
	return a, nil
}

func (m *MemStore) CreatePolicy(p *ExamPolicy) error {
	m.mu.Lock()
	defer m.mu.Unlock()
	if _, ok := m.policies[p.ExamID]; ok {
		return errors.New("conflict")
	}
	now := time.Now().UTC()
	p.CreatedAt = now
	p.UpdatedAt = now
	p.Active = true
	m.policies[p.ExamID] = p
	return nil
}

func (m *MemStore) GetPolicy(examID string) (*ExamPolicy, error) {
	m.mu.RLock()
	defer m.mu.RUnlock()
	p, ok := m.policies[examID]
	if !ok || !p.Active {
		return nil, errors.New("not found")
	}
	return p, nil
}

func (m *MemStore) UpdatePolicy(examID string, p *ExamPolicy) error {
	m.mu.Lock()
	defer m.mu.Unlock()
	old, ok := m.policies[examID]
	if !ok || !old.Active {
		return errors.New("not found")
	}
	p.UpdatedAt = time.Now().UTC()
	p.Active = old.Active
	m.policies[examID] = p
	return nil
}

func (m *MemStore) DeactivatePolicy(examID string) error {
	m.mu.Lock()
	defer m.mu.Unlock()
	p, ok := m.policies[examID]
	if !ok || !p.Active {
		return errors.New("not found")
	}
	p.Active = false
	p.UpdatedAt = time.Now().UTC()
	return nil
}

func (m *MemStore) StartSession(sess *Session) error {
	m.mu.Lock()
	defer m.mu.Unlock()
	sess.StartedAt = time.Now().UTC()
	sess.Status = "active"
	m.sessions[sess.ID] = sess
	return nil
}

func (m *MemStore) Heartbeat(sessionID string) error {
	// no-op for mem
	return nil
}

func (m *MemStore) AddViolation(sessionID string, v *Violation) error {
	m.mu.Lock()
	defer m.mu.Unlock()
	v.TS = time.Now().UTC()
	m.violations[sessionID] = append(m.violations[sessionID], v)
	if s, ok := m.sessions[sessionID]; ok {
		s.Violations++
	}
	return nil
}

func (m *MemStore) Complete(sessionID, reason string) error {
	m.mu.Lock()
	defer m.mu.Unlock()
	s, ok := m.sessions[sessionID]
	if !ok {
		return errors.New("not found")
	}
	now := time.Now().UTC()
	s.Status = "completed"
	s.EndedAt = &now
	return nil
}
