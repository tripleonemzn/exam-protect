package store

import (
	"exam-protect/backend/internal/models"
)

func (s *PGStore) GetAdminByUsername(u string) (*models.Admin, error) {
	c, cancel := ctx()
	defer cancel()
	row := s.Pool.QueryRow(c, `select id, username, pass_hash, active, created_at from admins where username=$1`, u)
	var a models.Admin
	if err := row.Scan(&a.ID, &a.Username, &a.PassHash, &a.Active, &a.CreatedAt); err != nil {
		return nil, err
	}
	return &a, nil
}
