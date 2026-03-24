package models

import "time"

type Branding struct {
	AppName      string  `json:"appName"`
	PrimaryColor *string `json:"primaryColor,omitempty"`
	LogoURL      *string `json:"logoUrl,omitempty"`
}

type ExamPolicy struct {
	ExamID                  string         `json:"examId"`
	Title                   string         `json:"title"`
	LaunchURL               string         `json:"launchUrl"`
	MobileLaunchURL         string         `json:"mobileLaunchUrl,omitempty"`
	DesktopLaunchURL        string         `json:"desktopLaunchUrl,omitempty"`
	AutoVariant             bool           `json:"autoVariant"`
	RendererHint            string         `json:"rendererHint,omitempty"`
	AllowedDomains          []string       `json:"allowedDomains"`
	BlockExternalNavigation bool           `json:"blockExternalNavigation"`
	EnableCopyPaste         bool           `json:"enableCopyPaste"`
	EnableDownloads         bool           `json:"enableDownloads"`
	EnableUploads           bool           `json:"enableUploads"`
	HeartbeatSeconds        int            `json:"heartbeatSeconds"`
	TeacherPinHash          string         `json:"teacherPinHash"`
	EntryPinHash            string         `json:"entryPinHash"`
	EntryPinRequired        bool           `json:"entryPinRequired"`
	MaxBackgroundSeconds    int            `json:"maxBackgroundSeconds"`
	ViolationThreshold      int            `json:"violationThreshold"`
	ManagedMode             bool           `json:"managedMode"`
	Branding                *Branding      `json:"branding,omitempty"`
	Active                  bool           `json:"active"`
	CreatedAt               time.Time      `json:"createdAt"`
	UpdatedAt               time.Time      `json:"updatedAt"`
	PlatformOverrides       map[string]any `json:"platformOverrides,omitempty"`
}

type Admin struct {
	ID        int64
	Username  string
	PassHash  string
	Active    bool
	CreatedAt time.Time
}

type Session struct {
	ID         string
	ExamID     string
	DeviceID   string
	Mode       string
	AppVersion string
	StartedAt  time.Time
	EndedAt    *time.Time
	Status     string
	Violations int
}

type Violation struct {
	ID        int64
	SessionID string
	Type      string
	Detail    *string
	TS        time.Time
}
