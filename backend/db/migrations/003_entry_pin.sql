alter table exam_policies
  add column if not exists entry_pin_hash text not null default '',
  add column if not exists entry_pin_required boolean not null default false;
