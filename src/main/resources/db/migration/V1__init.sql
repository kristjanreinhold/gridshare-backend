-- GridShare EV — initial schema (spec §5)

create table app_user (
    id          uuid primary key,
    google_sub  varchar(255),
    name        varchar(255) not null,
    email       varchar(255) not null,
    phone       varchar(64)  not null,
    iban        varchar(64),
    rating      numeric(2,1),
    created_at  timestamptz  not null default now()
);

create table listing (
    id             uuid primary key,
    host_id        uuid not null references app_user(id),
    lat            double precision not null,
    lng            double precision not null,
    charger_type   varchar(32) not null,
    power_kw       numeric(6,2) not null,
    price_per_hour numeric(8,2) not null,
    instructions   text not null,
    available      boolean not null default true,
    auto_accept    boolean not null default false,
    active         boolean not null default true,
    rating         numeric(2,1),
    created_at     timestamptz not null default now()
);
create index idx_listing_active_available on listing(active, available);
create index idx_listing_host on listing(host_id);

create table booking (
    id                  uuid primary key,
    listing_id          uuid not null references listing(id),
    driver_phone        varchar(64) not null,
    start_time          timestamptz not null,
    duration_min        integer not null,
    price               numeric(8,2) not null,
    status              varchar(32) not null,
    created_at          timestamptz not null default now(),
    host_responded_at   timestamptz,
    paid_at             timestamptz,
    access_instructions text
);
create index idx_booking_listing on booking(listing_id);
create index idx_booking_status on booking(status);

create table payment (
    id            uuid primary key,
    booking_id    uuid not null references booking(id),
    montonio_ref  varchar(128) not null unique,
    amount        numeric(8,2) not null,
    status        varchar(32) not null,
    payout_status varchar(32) not null
);

create table payout (
    id          uuid primary key,
    booking_id  uuid not null references booking(id),
    amount      numeric(8,2) not null,
    status      varchar(32) not null,
    created_at  timestamptz not null default now()
);

create table rating (
    id          uuid primary key,
    booking_id  uuid not null references booking(id),
    subject     varchar(16) not null,
    score       integer not null,
    comment     text
);

create table dispute (
    id          uuid primary key,
    booking_id  uuid not null references booking(id),
    description text not null,
    status      varchar(16) not null,
    resolution  text,
    created_at  timestamptz not null default now()
);
