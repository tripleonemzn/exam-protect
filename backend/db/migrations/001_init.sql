create table if not exists admins (
  id bigserial primary key,
  username text unique not null,
  pass_hash text not null,
  active boolean not null default true,
  created_at timestamptz not null default now()
);

create table if not exists exam_policies (
  exam_id text primary key,
  title text not null,
  launch_url text not null,
  allowed_domains text[] not null,
  block_external_navigation boolean not null default true,
  enable_copy_paste boolean not null default false,
  enable_downloads boolean not null default false,
  enable_uploads boolean not null default false,
  heartbeat_seconds int not null default 15,
  teacher_pin_hash text not null default '',
  max_background_seconds int not null default 0,
  violation_threshold int not null default 3,
  managed_mode boolean not null default false,
  branding jsonb,
  active boolean not null default true,
  platform_overrides jsonb,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists exam_sessions (
  id text primary key,
  exam_id text not null references exam_policies(exam_id),
  device_id text not null,
  mode text not null,
  app_version text,
  started_at timestamptz not null,
  ended_at timestamptz,
  end_reason text,
  status text not null,
  violations int not null default 0,
  last_heartbeat timestamptz
);

create table if not exists violation_logs (
  id bigserial primary key,
  session_id text not null references exam_sessions(id),
  type text not null,
  detail text,
  ts timestamptz not null default now()
);

create table if not exists audit_logs (
  id bigserial primary key,
  actor text not null,
  action text not null,
  entity text not null,
  entity_id text not null,
  data jsonb,
  ts timestamptz not null default now()
);
