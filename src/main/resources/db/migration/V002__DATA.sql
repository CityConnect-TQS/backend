-- Insert default data
INSERT INTO public.bus (id, capacity, company)
VALUES (1, 50, 'Flix Bus'),
       (2, 30, 'Renex'),
       (3, 20, 'Transdev'),
       (4, 40, 'Rede Expressos'),
       (5, 20, 'Flix Bus');
       

INSERT INTO public.city (id, name, latitude, longitude)
VALUES (1, 'Murtosa', 40.7608, -8.6375),
       (2, 'Estarreja', 40.7583, -8.5725),
       (3, 'Aveiro', 40.6401, -8.6538),
       (4, 'Ovar', 40.8636, -8.6281);

INSERT INTO public.trip (id, arrival_time, departure_time, free_seats, price, arrival_id, bus_id, departure_id)
VALUES (1, '2024-05-08 08:00:00', '2024-05-08 06:00:00', 50, 10.00, 3, 1, 1),
       (2, '2024-05-09 10:00:00', '2024-05-09 09:00:00', 30, 8.50, 4, 2, 3),
       (3, '2024-05-09 12:00:00', '2024-05-09 11:00:00', 20, 12.00, 1, 3, 2),
       (4, '2024-05-10 14:00:00', '2024-05-10 13:00:00', 40, 15.50, 2, 4, 4),
       (5, '2024-05-11 16:00:00', '2024-05-11 15:00:00', 50, 9.00, 3, 1, 4),
       (6, '2024-05-11 18:00:00', '2024-05-11 17:00:00', 30, 11.00, 2, 2, 1),
       (7, '2024-05-12 20:00:00', '2024-05-12 19:00:00', 20, 7.50, 1, 3, 3),
       (8, '2024-05-13 22:00:00', '2024-05-13 21:00:00', 40, 5.00, 2, 4, 2),
       (9, '2024-05-14 00:00:00', '2024-05-13 23:00:00', 50, 20.00, 3, 1, 1),
       (10, '2024-05-15 08:00:00', '2024-05-15 06:00:00', 50, 10.00, 3, 1, 1),
       (11, '2024-05-16 10:00:00', '2024-05-16 09:00:00', 30, 8.50, 4, 2, 3),
       (12, '2024-05-16 12:00:00', '2024-05-16 11:00:00', 20, 12.00, 1, 3, 2),
       (13, '2024-05-17 14:00:00', '2024-05-17 13:00:00', 40, 15.50, 2, 4, 4),
       (14, '2024-05-18 16:00:00', '2024-05-18 15:00:00', 50, 9.00, 3, 1, 4),
       (15, '2024-05-18 18:00:00', '2024-05-18 17:00:00', 30, 11.00, 4, 2, 1),
       (16, '2024-05-19 20:00:00', '2024-05-19 19:00:00', 20, 7.50, 1, 3, 3),
       (17, '2024-05-20 22:00:00', '2024-05-20 21:00:00', 40, 5.00, 2, 4, 2),
       (18, '2024-05-21 00:00:00', '2024-05-20 23:00:00', 50, 20.00, 3, 1, 1),
       (19, '2024-05-22 08:00:00', '2024-05-22 06:00:00', 50, 10.00, 3, 1, 1),
       (20, '2024-05-23 10:00:00', '2024-05-23 09:00:00', 30, 8.50, 4, 2, 3),
       (21, '2024-05-08 12:00:00', '2024-05-08 11:00:00', 20, 12.00, 4, 3, 1),
       (22, '2024-05-09 14:00:00', '2024-05-09 13:00:00', 40, 15.50, 1, 4, 2),
       (23, '2024-05-09 16:00:00', '2024-05-09 15:00:00', 50, 9.00, 3, 1, 3),
       (24, '2024-05-09 18:00:00', '2024-05-09 17:00:00', 30, 11.00, 2, 2, 1),
       (25, '2024-05-10 20:00:00', '2024-05-10 19:00:00', 20, 7.50, 3, 3, 4),
       (26, '2024-05-11 22:00:00', '2024-05-11 21:00:00', 40, 5.00, 4, 4, 3),
       (27, '2024-05-11 00:00:00', '2024-05-10 23:00:00', 50, 20.00, 1, 1, 2),
       (28, '2024-05-11 08:00:00', '2024-05-11 06:00:00', 50, 10.00, 2, 2, 4),
       (29, '2024-05-12 10:00:00', '2024-05-12 09:00:00', 30, 8.50, 3, 3, 1),
       (30, '2024-05-12 12:00:00', '2024-05-12 11:00:00', 20, 12.00, 4, 4, 2),
       (31, '2024-05-13 14:00:00', '2024-05-13 13:00:00', 40, 15.50, 1, 1, 3),
       (32, '2024-05-14 16:00:00', '2024-05-14 15:00:00', 50, 9.00, 2, 2, 4),
       (33, '2024-05-14 18:00:00', '2024-05-14 17:00:00', 30, 11.00, 3, 3, 1),
       (34, '2024-05-15 20:00:00', '2024-05-15 19:00:00', 20, 7.50, 4, 4, 2),
       (35, '2024-05-16 22:00:00', '2024-05-16 21:00:00', 40, 5.00, 1, 1, 3),
       (36, '2024-05-16 00:00:00', '2024-05-15 23:00:00', 50, 20.00, 2, 2, 4),
       (37, '2024-05-17 08:00:00', '2024-05-17 06:00:00', 50, 10.00, 3, 3, 1),
       (38, '2024-05-18 10:00:00', '2024-05-18 09:00:00', 30, 8.50, 4, 4, 2),
       (39, '2024-05-18 12:00:00', '2024-05-18 11:00:00', 20, 12.00, 1, 1, 3),
       (40, '2024-05-19 14:00:00', '2024-05-19 13:00:00', 40, 15.50, 2, 2, 4),
       (41, '2024-05-20 16:00:00', '2024-05-20 15:00:00', 50, 9.00, 3, 3, 1),
       (42, '2024-05-20 18:00:00', '2024-05-20 17:00:00', 30, 11.00, 4, 4, 2),
       (43, '2024-05-21 20:00:00', '2024-05-21 19:00:00', 20, 7.50, 1, 1, 3),
       (44, '2024-05-22 22:00:00', '2024-05-22 21:00:00', 40, 5.00, 2, 2, 4),
       (45, '2024-05-22 00:00:00', '2024-05-21 23:00:00', 50, 20.00, 3, 3, 1),
       (46, '2024-05-23 08:00:00', '2024-05-23 06:00:00', 50, 10.00, 4, 4, 2),
       (47, '2024-05-24 10:00:00', '2024-05-24 09:00:00', 30, 8.50, 1, 1, 3),
       (48, '2024-05-24 12:00:00', '2024-05-24 11:00:00', 20, 12.00, 2, 2, 4),
       (49, '2024-05-25 14:00:00', '2024-05-25 13:00:00', 40, 15.50, 3, 3, 1),
       (50, '2024-05-26 16:00:00', '2024-05-26 15:00:00', 50, 9.00, 4, 4, 2),
       (51, '2024-05-08 08:00:00', '2024-05-08 06:00:00', 50, 10.00, 3, 1, 1),
       (52, '2024-05-08 10:00:00', '2024-05-08 09:00:00', 30, 8.50, 2, 2, 4);


-- Set serial sequence
SELECT pg_catalog.setval('public.bus_id_seq', 4, true);
SELECT pg_catalog.setval('public.city_id_seq', 4, true);
SELECT pg_catalog.setval('public.trip_id_seq', 52, true);
