package store

import (
	"time"

	"exam-protect/backend/internal/models"
)

func (s *PGStore) StartSession(sess *models.Session) error {
	c, cancel := ctx()
	defer cancel()
	sess.StartedAt = time.Now().UTC()
	sess.Status = "active"
	_, err := s.Pool.Exec(c, `
insert into exam_sessions (id, exam_id, device_id, mode, app_version, started_at, status, violations)
values ($1,$2,$3,$4,$5,$6,$7,$8)
`, sess.ID, sess.ExamID, sess.DeviceID, sess.Mode, sess.AppVersion, sess.StartedAt, sess.Status, 0)
	return err
}

func (s *PGStore) AddViolation(sessionID string, v *models.Violation) error {
	c, cancel := ctx()
	defer cancel()
	v.TS = time.Now().UTC()
	_, err := s.Pool.Exec(c, `
insert into violation_logs (session_id, type, detail, ts)
values ($1,$2,$3,$4)
`, sessionID, v.Type, v.Detail, v.TS)
	if err != nil {
		return err
	}
	_, _ = s.Pool.Exec(c, `update exam_sessions set violations = violations + 1 where id=$1`, sessionID)
	return nil
}

func (s *PGStore) Heartbeat(sessionID string) error {
	c, cancel := ctx()
	defer cancel()
	_, err := s.Pool.Exec(c, `update exam_sessions set last_heartbeat = now() where id=$1`, sessionID)
	return err
}

func (s *PGStore) Complete(sessionID, reason string) error {
	c, cancel := ctx()
	defer cancel()
	now := time.Now().UTC()
	_, err := s.Pool.Exec(c, `update exam_sessions set status='completed', ended_at=$2, end_reason=$3 where id=$1`, sessionID, now, reason)
	return err
}
