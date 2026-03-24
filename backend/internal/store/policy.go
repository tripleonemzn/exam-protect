package store

import (
	"encoding/json"
	"errors"
	"time"

	"exam-protect/backend/internal/models"
)

func (s *PGStore) CreatePolicy(p *models.ExamPolicy) error {
	c, cancel := ctx()
	defer cancel()
	now := time.Now().UTC()
	p.CreatedAt = now
	p.UpdatedAt = now
	p.Active = true
	var overrides []byte
	if p.PlatformOverrides != nil {
		overrides, _ = json.Marshal(p.PlatformOverrides)
	}
	_, err := s.Pool.Exec(c, `
insert into exam_policies (exam_id, title, launch_url, mobile_launch_url, desktop_launch_url, auto_variant, renderer_hint, allowed_domains, block_external_navigation, enable_copy_paste, enable_downloads, enable_uploads, heartbeat_seconds, teacher_pin_hash, entry_pin_hash, entry_pin_required, max_background_seconds, violation_threshold, managed_mode, branding, active, platform_overrides, created_at, updated_at)
values ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13,$14,$15,$16,$17,$18,$19,$20,$21,$22,$23,$24)
`, p.ExamID, p.Title, p.LaunchURL, p.MobileLaunchURL, p.DesktopLaunchURL, p.AutoVariant, p.RendererHint, p.AllowedDomains, p.BlockExternalNavigation, p.EnableCopyPaste, p.EnableDownloads, p.EnableUploads, p.HeartbeatSeconds, p.TeacherPinHash, p.EntryPinHash, p.EntryPinRequired, p.MaxBackgroundSeconds, p.ViolationThreshold, p.ManagedMode, p.Branding, p.Active, overrides, p.CreatedAt, p.UpdatedAt)
	return err
}

func (s *PGStore) GetPolicy(examID string) (*models.ExamPolicy, error) {
	c, cancel := ctx()
	defer cancel()
	row := s.Pool.QueryRow(c, `
select exam_id, title, launch_url, mobile_launch_url, desktop_launch_url, auto_variant, renderer_hint, allowed_domains, block_external_navigation, enable_copy_paste, enable_downloads, enable_uploads, heartbeat_seconds, teacher_pin_hash, entry_pin_hash, entry_pin_required, max_background_seconds, violation_threshold, managed_mode, branding, active, platform_overrides, created_at, updated_at
from exam_policies where exam_id=$1 and active=true`, examID)
	var p models.ExamPolicy
	var overrides []byte
	if err := row.Scan(&p.ExamID, &p.Title, &p.LaunchURL, &p.MobileLaunchURL, &p.DesktopLaunchURL, &p.AutoVariant, &p.RendererHint, &p.AllowedDomains, &p.BlockExternalNavigation, &p.EnableCopyPaste, &p.EnableDownloads, &p.EnableUploads, &p.HeartbeatSeconds, &p.TeacherPinHash, &p.EntryPinHash, &p.EntryPinRequired, &p.MaxBackgroundSeconds, &p.ViolationThreshold, &p.ManagedMode, &p.Branding, &p.Active, &overrides, &p.CreatedAt, &p.UpdatedAt); err != nil {
		return nil, err
	}
	if len(overrides) > 0 {
		_ = json.Unmarshal(overrides, &p.PlatformOverrides)
	}
	return &p, nil
}

func (s *PGStore) UpdatePolicy(examID string, p *models.ExamPolicy) error {
	c, cancel := ctx()
	defer cancel()
	p.UpdatedAt = time.Now().UTC()
	var overrides []byte
	if p.PlatformOverrides != nil {
		overrides, _ = json.Marshal(p.PlatformOverrides)
	}
	cmd, err := s.Pool.Exec(c, `
update exam_policies set title=$2, launch_url=$3, mobile_launch_url=$4, desktop_launch_url=$5, auto_variant=$6, renderer_hint=$7, allowed_domains=$8, block_external_navigation=$9, enable_copy_paste=$10, enable_downloads=$11, enable_uploads=$12, heartbeat_seconds=$13, teacher_pin_hash=$14, entry_pin_hash=$15, entry_pin_required=$16, max_background_seconds=$17, violation_threshold=$18, managed_mode=$19, branding=$20, platform_overrides=$21, updated_at=$22
where exam_id=$1 and active=true
`, examID, p.Title, p.LaunchURL, p.MobileLaunchURL, p.DesktopLaunchURL, p.AutoVariant, p.RendererHint, p.AllowedDomains, p.BlockExternalNavigation, p.EnableCopyPaste, p.EnableDownloads, p.EnableUploads, p.HeartbeatSeconds, p.TeacherPinHash, p.EntryPinHash, p.EntryPinRequired, p.MaxBackgroundSeconds, p.ViolationThreshold, p.ManagedMode, p.Branding, overrides, p.UpdatedAt)
	if err != nil {
		return err
	}
	if cmd.RowsAffected() == 0 {
		return errors.New("not found")
	}
	return nil
}

func (s *PGStore) DeactivatePolicy(examID string) error {
	c, cancel := ctx()
	defer cancel()
	cmd, err := s.Pool.Exec(c, `update exam_policies set active=false, updated_at=now() where exam_id=$1 and active=true`, examID)
	if err != nil {
		return err
	}
	if cmd.RowsAffected() == 0 {
		return errors.New("not found")
	}
	return nil
}
