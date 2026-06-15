-- Sample data mirroring the MSW mocks (Tallinn-area listings)

insert into app_user (id, name, email, phone, iban, rating, created_at) values
 ('11111111-1111-1111-1111-111111111111', 'Mari Tamm', 'mari.tamm@example.ee', '+37251234567', 'EE382200221020145685', 4.8, '2026-05-01T09:00:00Z'),
 ('22222222-2222-2222-2222-222222222222', 'Jaan Kask', 'jaan.kask@example.ee', '+37255550101', 'EE471000001020145699', 4.5, '2026-05-10T12:00:00Z');

insert into listing (id, host_id, lat, lng, charger_type, power_kw, price_per_hour, instructions, available, auto_accept, active, rating) values
 ('33333333-3333-3333-3333-333333333301', '11111111-1111-1111-1111-111111111111', 59.4370, 24.7536, 'TYPE2', 11, 3.50, 'Sissesõit hoovi värava tagant, kood 1234#. Laadija parempoolse garaaži seinal.', true, false, true, 4.8),
 ('33333333-3333-3333-3333-333333333302', '22222222-2222-2222-2222-222222222222', 59.4230, 24.7945, 'CCS', 22, 6.00, 'Parkla 2. korrus, koht B17. Laadija aktiveerub minu telefonist.', true, false, true, 4.5),
 ('33333333-3333-3333-3333-333333333303', '11111111-1111-1111-1111-111111111111', 59.4440, 24.7500, 'SCHUKO', 3.7, 1.50, 'Aiamaja taga välispistik. Helista kohale jõudes.', false, false, true, null);

insert into booking (id, listing_id, driver_phone, start_time, duration_min, price, status, created_at, host_responded_at, paid_at, access_instructions) values
 ('44444444-4444-4444-4444-444444444401', '33333333-3333-3333-3333-333333333301', '+37259990001', '2026-06-16T18:00:00Z', 120, 7.00, 'PENDING_HOST', '2026-06-15T10:00:00Z', null, null, null),
 ('44444444-4444-4444-4444-444444444402', '33333333-3333-3333-3333-333333333301', '+37259990002', '2026-06-14T20:00:00Z', 180, 10.50, 'COMPLETED', '2026-06-14T15:00:00Z', '2026-06-14T15:02:00Z', '2026-06-14T15:05:00Z', 'Sissesõit hoovi värava tagant, kood 1234#.');

insert into payout (id, booking_id, amount, status, created_at) values
 ('55555555-5555-5555-5555-555555555501', '44444444-4444-4444-4444-444444444402', 9.45, 'PAID', '2026-06-15T06:00:00Z');

insert into dispute (id, booking_id, description, status, resolution, created_at) values
 ('66666666-6666-6666-6666-666666666601', '44444444-4444-4444-4444-444444444402', 'Laadija ei andnud täisvõimsust kogu sessiooni vältel.', 'OPEN', null, '2026-06-15T08:00:00Z');
