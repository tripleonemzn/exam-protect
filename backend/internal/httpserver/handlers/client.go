package handlers

import (
	"encoding/json"
	"net/http"
	"strings"

	"exam-protect/backend/internal/config"
)

type ClientHandler struct {
	cfg config.Config
}

func NewClientHandler(cfg config.Config) *ClientHandler {
	return &ClientHandler{cfg: cfg}
}

type BootstrapResp struct {
	AppName         string `json:"appName"`
	FeatureTier     string `json:"featureTier"`
	DefaultEntryPIN bool   `json:"defaultEntryPinRequired"`
	MobileVariant   bool   `json:"mobileVariant"`
	SupportsQR      bool   `json:"supportsQr"`
	SupportsExamID  bool   `json:"supportsExamId"`
	SupportsURL     bool   `json:"supportsUrl"`
}

func (h *ClientHandler) Bootstrap(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	resp := BootstrapResp{
		AppName:         "EXAM-PROTECT",
		FeatureTier:     "free",
		DefaultEntryPIN: true,
		MobileVariant:   true,
		SupportsQR:      true,
		SupportsExamID:  true,
		SupportsURL:     true,
	}
	json.NewEncoder(w).Encode(resp)
}

type UpdateResp struct {
	VersionCode int    `json:"versionCode"`
	VersionName string `json:"versionName"`
	Mandatory   bool   `json:"mandatory"`
	Notes       string `json:"notes"`
	DownloadURL string `json:"downloadUrl"`
}

func (h *ClientHandler) Update(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	type ghAsset struct {
		Name               string `json:"name"`
		BrowserDownloadURL string `json:"browser_download_url"`
	}
	type ghRelease struct {
		TagName string    `json:"tag_name"`
		Assets  []ghAsset `json:"assets"`
	}
	resp := UpdateResp{VersionCode: 1, VersionName: "0.1.0", Mandatory: false, Notes: "Initial", DownloadURL: ""}
	if r, err := http.Get("https://api.github.com/repos/tripleonemzn/exam-protect/releases/latest"); err == nil && r.StatusCode == 200 {
		defer r.Body.Close()
		var gr ghRelease
		if err := json.NewDecoder(r.Body).Decode(&gr); err == nil {
			tag := strings.TrimPrefix(gr.TagName, "v")
			resp.VersionName = tag
			resp.VersionCode = parseSemverToCode(tag)
			for _, a := range gr.Assets {
				if a.Name == "exam-protect.apk" {
					resp.DownloadURL = a.BrowserDownloadURL
					break
				}
			}
			if resp.DownloadURL == "" {
				// fallback to latest/download link
				resp.DownloadURL = "https://github.com/tripleonemzn/exam-protect/releases/latest/download/exam-protect.apk"
			}
			resp.Notes = "Update tersedia"
		}
	}
	json.NewEncoder(w).Encode(resp)
}

func parseSemverToCode(v string) int {
	parts := strings.SplitN(v, "-", 2)
	core := parts[0]
	segs := strings.Split(core, ".")
	mj, mn, p := 0, 0, 0
	if len(segs) > 0 {
		mj = atoiSafe(segs[0])
	}
	if len(segs) > 1 {
		mn = atoiSafe(segs[1])
	}
	if len(segs) > 2 {
		p = atoiSafe(segs[2])
	}
	return mj*10000 + mn*100 + p
}

func atoiSafe(s string) int {
	n := 0
	for _, c := range s {
		if c < '0' || c > '9' {
			break
		}
		n = n*10 + int(c-'0')
	}
	return n
}
