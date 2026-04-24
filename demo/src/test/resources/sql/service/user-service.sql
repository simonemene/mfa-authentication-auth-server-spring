INSERT INTO app_user (
    username,
    password,
    totp_secret_encrypted,
    mfa_enabled,
    enabled
) VALUES (
    'simone',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiilr0JCdEJ7oU5GxYaYkRkYg0VdG5e',
    NULL,
    FALSE,
    TRUE
);