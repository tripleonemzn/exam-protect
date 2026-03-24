-- ganti hash ini dengan hasil bcrypt sesuai kebutuhan
insert into admins (username, pass_hash, active) values ('admin', '$2a$10$2H0qfNQGSc3QmW0x3Jx0Uu6zUN1D0DlqKqDqQ3v4G6xgVg3mY1xvK', true) on conflict do nothing;
