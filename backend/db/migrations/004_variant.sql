alter table exam_policies
  add column if not exists mobile_launch_url text not null default '',
  add column if not exists desktop_launch_url text not null default '',
  add column if not exists auto_variant boolean not null default false,
  add column if not exists renderer_hint text not null default '';
