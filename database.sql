--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: message_events; Type: TABLE; Schema: public; Owner: kostya_by; Tablespace: 
--

CREATE TABLE message_events (
    id integer NOT NULL,
    message_id integer NOT NULL,
    event_type text NOT NULL,
    created_at timestamp without time zone,
    updated_at timestamp without time zone
);


ALTER TABLE message_events OWNER TO kostya_by;

--
-- Name: message_events_id_seq; Type: SEQUENCE; Schema: public; Owner: kostya_by
--

CREATE SEQUENCE message_events_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE message_events_id_seq OWNER TO kostya_by;

--
-- Name: message_events_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: kostya_by
--

ALTER SEQUENCE message_events_id_seq OWNED BY message_events.id;


--
-- Name: messages; Type: TABLE; Schema: public; Owner: kostya_by; Tablespace: 
--

CREATE TABLE messages (
    id integer NOT NULL,
    user_id integer NOT NULL,
    content text NOT NULL,
    created_at timestamp without time zone,
    updated_at timestamp without time zone
);


ALTER TABLE messages OWNER TO kostya_by;

--
-- Name: messages_id_seq; Type: SEQUENCE; Schema: public; Owner: kostya_by
--

CREATE SEQUENCE messages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE messages_id_seq OWNER TO kostya_by;

--
-- Name: messages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: kostya_by
--

ALTER SEQUENCE messages_id_seq OWNED BY messages.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: kostya_by; Tablespace: 
--

CREATE TABLE users (
    id integer NOT NULL,
    name character(56) NOT NULL,
    password character(32) NOT NULL,
    email character(320) NOT NULL,
    created_at timestamp without time zone,
    updated_at timestamp without time zone
);


ALTER TABLE users OWNER TO kostya_by;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: kostya_by
--

CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE users_id_seq OWNER TO kostya_by;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: kostya_by
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: kostya_by
--

ALTER TABLE ONLY message_events ALTER COLUMN id SET DEFAULT nextval('message_events_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: kostya_by
--

ALTER TABLE ONLY messages ALTER COLUMN id SET DEFAULT nextval('messages_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: kostya_by
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- Data for Name: message_events; Type: TABLE DATA; Schema: public; Owner: kostya_by
--

COPY message_events (id, message_id, event_type, created_at, updated_at) FROM stdin;
35	110	add_message	2015-03-10 00:54:33.011	2015-03-10 00:54:33.011
36	111	add_message	2015-03-10 00:55:08.987	2015-03-10 00:55:08.987
37	112	add_message	2015-03-10 00:55:20.385	2015-03-10 00:55:20.385
38	113	add_message	2015-03-10 00:55:32.711	2015-03-10 00:55:32.711
\.


--
-- Name: message_events_id_seq; Type: SEQUENCE SET; Schema: public; Owner: kostya_by
--

SELECT pg_catalog.setval('message_events_id_seq', 38, true);


--
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: kostya_by
--

COPY messages (id, user_id, content, created_at, updated_at) FROM stdin;
110	1	Привет всем!	2015-03-10 00:54:32.996	2015-03-10 00:54:32.996
111	4	Какой замечательный чат!	2015-03-10 00:55:08.974	2015-03-10 00:55:08.974
112	4	Просто чудо!	2015-03-10 00:55:20.373	2015-03-10 00:55:20.373
113	4	Все бы чаты так замечательно выглядели! 	2015-03-10 00:55:32.698	2015-03-10 00:55:32.698
\.


--
-- Name: messages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: kostya_by
--

SELECT pg_catalog.setval('messages_id_seq', 113, true);


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: kostya_by
--

COPY users (id, name, password, email, created_at, updated_at) FROM stdin;
1	kostya_by                                               	6d8f1155f554213d2c8e48162d1caab6	kostya.sokol.by@gmail.com                                                                                                                                                                                                                                                                                                       	2015-03-06 00:14:58.403	2015-03-06 00:14:58.403
3	safari                                                  	60190cb2b5a1d64c6c22fdda4d9e29e0	safari@apple.com                                                                                                                                                                                                                                                                                                                	\N	\N
4	chrome                                                  	554838a8451ac36cb977e719e9d6623c	chrome@google.com                                                                                                                                                                                                                                                                                                               	\N	2015-03-09 23:32:12.655
\.


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: kostya_by
--

SELECT pg_catalog.setval('users_id_seq', 4, true);


--
-- Name: message_events_pkey; Type: CONSTRAINT; Schema: public; Owner: kostya_by; Tablespace: 
--

ALTER TABLE ONLY message_events
    ADD CONSTRAINT message_events_pkey PRIMARY KEY (id);


--
-- Name: messages_pkey; Type: CONSTRAINT; Schema: public; Owner: kostya_by; Tablespace: 
--

ALTER TABLE ONLY messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: kostya_by; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: message_events_message_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: kostya_by
--

ALTER TABLE ONLY message_events
    ADD CONSTRAINT message_events_message_id_fkey FOREIGN KEY (message_id) REFERENCES messages(id);


--
-- Name: messages_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: kostya_by
--

ALTER TABLE ONLY messages
    ADD CONSTRAINT messages_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: kostya_by
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM kostya_by;
GRANT ALL ON SCHEMA public TO kostya_by;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

