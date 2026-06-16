-- Google-provisioned hosts have no phone yet (they fill it on the profile page),
-- so phone must be nullable. iban was already nullable.
alter table app_user alter column phone drop not null;
