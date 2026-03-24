package config

import (
	"os"
)

type HTTPConfig struct {
	Addr    string
	Origins string
}

type DBConfig struct {
	DSN string
}

type AuthConfig struct {
	AdminJWTSecret  string
	LaunchJWTSecret string
	LaunchSigned    bool
}

type Config struct {
	HTTP HTTPConfig
	DB   DBConfig
	Auth AuthConfig
}

func get(k, def string) string {
	if v := os.Getenv(k); v != "" {
		return v
	}
	return def
}

func Load() Config {
	return Config{
		HTTP: HTTPConfig{
			Addr:    get("HTTP_ADDR", ":8080"),
			Origins: get("CORS_ALLOWED_ORIGINS", "*"),
		},
		DB: DBConfig{
			DSN: get("DB_DSN", "postgres://exam:exam@db:5432/exam?sslmode=disable"),
		},
		Auth: AuthConfig{
			AdminJWTSecret:  get("ADMIN_JWT_SECRET", "dev-admin-secret"),
			LaunchJWTSecret: get("LAUNCH_JWT_SECRET", "dev-launch-secret"),
			LaunchSigned:    get("LAUNCH_JWT_SIGNED", "false") == "true",
		},
	}
}
