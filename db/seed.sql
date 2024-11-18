--
-- PostgreSQL database dump
--

-- Dumped from database version 16.4 (Debian 16.4-1.pgdg120+2)
-- Dumped by pg_dump version 16.4

-- Started on 2024-11-18 22:08:36

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3365 (class 0 OID 16518)
-- Dependencies: 217
-- Data for Name: service_layanan; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.service_layanan (service_code, service_name, service_icon, service_tariff) FROM stdin;
PAJAK	Pajak PBB	https://nutech-integrasi.app/dummy.jpg	40000.00
PLN	Listrik	https://nutech-integrasi.app/dummy.jpg	10000.00
PDAM	PDAM Berlangganan	https://nutech-integrasi.app/dummy.jpg	40000.00
PULSA	Pulsa	https://nutech-integrasi.app/dummy.jpg	40000.00
PGN	PGN Berlangganan	https://nutech-integrasi.app/dummy.jpg	50000.00
MUSIK	Musik Berlangganan	https://nutech-integrasi.app/dummy.jpg	50000.00
TV	TV Berlangganan	https://nutech-integrasi.app/dummy.jpg	50000.00
PAKET_DATA	Paket data	https://nutech-integrasi.app/dummy.jpg	50000.00
VOUCHER_GAME	Voucher Game	https://nutech-integrasi.app/dummy.jpg	100000.00
VOUCHER_MAKANAN	Voucher Makanan	https://nutech-integrasi.app/dummy.jpg	100000.00
QURBAN	Qurban	https://nutech-integrasi.app/dummy.jpg	200000.00
ZAKAT	Zakat	https://nutech-integrasi.app/dummy.jpg	300000.00
\.


--
-- TOC entry 3364 (class 0 OID 16460)
-- Dependencies: 216
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, email, password, first_name, last_name, balance, invoice) FROM stdin;
1	user@nutech-integrasiiiii.com	$2a$10$AVvCi/Q.ysJ5T7EK5nxthe9JMpsXJlM.Juy54hGlzZccw70yvhnPu	User	Nutech	560000.00	\N
\.


--
-- TOC entry 3367 (class 0 OID 16609)
-- Dependencies: 219
-- Data for Name: transactions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.transactions (id, user_id, amount, transaction_type, created_at, invoice, service_id) FROM stdin;
1	1	200000.00	TOPUP	2024-11-18 21:07:37.263305	\N	\N
2	1	40000.00	PAYMENT	2024-11-18 21:11:07.096575	INV-20241118-A13	PULSA
3	1	200000.00	TOPUP	2024-11-18 21:38:21.515306	\N	\N
\.


--
-- TOC entry 3373 (class 0 OID 0)
-- Dependencies: 218
-- Name: transactions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.transactions_id_seq', 3, true);


--
-- TOC entry 3374 (class 0 OID 0)
-- Dependencies: 215
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 1, true);


-- Completed on 2024-11-18 22:08:57

--
-- PostgreSQL database dump complete
--

