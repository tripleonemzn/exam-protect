-- Seed admin default (username: admin, password hash example). Ganti hash untuk produksi.
-- Hash ini hanya contoh. Gunakan bcrypt baru sesuai kebutuhan.
-- BCrypt hash for "password" with cost 10 (public example)
insert into admins (username, pass_hash, active)
values ('admin', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5WXwX1a3uzMdCFAPxuxr9Y1IoF/2.', true)
on conflict (username) do nothing;
