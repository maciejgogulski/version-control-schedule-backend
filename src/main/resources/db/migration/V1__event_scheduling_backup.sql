--
-- PostgreSQL database dump
--

-- Dumped from database version 14.9 (Ubuntu 14.9-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 14.9 (Ubuntu 14.9-0ubuntu0.22.04.1)

-- Started on 2023-11-07 16:16:17 CET

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
-- TOC entry 246 (class 1255 OID 16626)
-- Name: assign_addressee_to_schedule_tag(bigint, bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.assign_addressee_to_schedule_tag(p_addressee_id bigint, p_schedule_tag_id bigint) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO schedule_tag_addressee
        (addressee_id, schedule_tag_id)
    VALUES (p_addressee_id, p_schedule_tag_id);
END;
$$;


ALTER FUNCTION public.assign_addressee_to_schedule_tag(p_addressee_id bigint, p_schedule_tag_id bigint) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 235 (class 1259 OID 16641)
-- Name: staged_event; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.staged_event (
    id bigint NOT NULL,
    schedule_tag_id bigint NOT NULL,
    committed boolean DEFAULT false NOT NULL,
    "timestamp" timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.staged_event OWNER TO postgres;

--
-- TOC entry 257 (class 1255 OID 16769)
-- Name: find_latest_staged_event_for_block_parameter(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.find_latest_staged_event_for_block_parameter(p_block_parameter_id bigint) RETURNS SETOF public.staged_event
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT staged_event.id, staged_event.schedule_tag_id, committed, timestamp
                 FROM staged_event
                          LEFT JOIN schedule_tag on staged_event.schedule_tag_id = schedule_tag.id
                          LEFT JOIN schedule_block on schedule_tag.id = schedule_block.schedule_tag_id
                          LEFT JOIN block_parameter on schedule_block.id = block_parameter.schedule_block_id
                 WHERE block_parameter.id = p_block_parameter_id
                 AND staged_event.committed = false
                 ORDER BY timestamp desc
                 LIMIT 1;
END;
$$;


ALTER FUNCTION public.find_latest_staged_event_for_block_parameter(p_block_parameter_id bigint) OWNER TO postgres;

--
-- TOC entry 261 (class 1255 OID 16775)
-- Name: find_latest_staged_event_for_block_parameter(bigint, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.find_latest_staged_event_for_block_parameter(p_block_parameter_id bigint, p_committed boolean) RETURNS SETOF public.staged_event
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT staged_event.id, staged_event.schedule_tag_id, committed, timestamp
                 FROM staged_event
                          LEFT JOIN schedule_tag on staged_event.schedule_tag_id = schedule_tag.id
                          LEFT JOIN schedule_block on schedule_tag.id = schedule_block.schedule_tag_id
                          LEFT JOIN block_parameter on schedule_block.id = block_parameter.schedule_block_id
                 WHERE block_parameter.id = p_block_parameter_id
                 AND staged_event.committed = p_committed
                 ORDER BY timestamp desc
                 LIMIT 1;
END;
$$;


ALTER FUNCTION public.find_latest_staged_event_for_block_parameter(p_block_parameter_id bigint, p_committed boolean) OWNER TO postgres;

--
-- TOC entry 241 (class 1259 OID 16744)
-- Name: modification; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.modification (
    id bigint NOT NULL,
    staged_event_id bigint NOT NULL,
    block_parameter_id bigint NOT NULL,
    type character varying(30) NOT NULL,
    old_value character varying(1000),
    new_value character varying(1000) NOT NULL,
    "timestamp" timestamp without time zone NOT NULL
);


ALTER TABLE public.modification OWNER TO postgres;

--
-- TOC entry 260 (class 1255 OID 16771)
-- Name: find_modification_for_staged_event_and_parameter_dict(bigint, bigint, bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE OR REPLACE FUNCTION public.find_modification_for_staged_event_and_parameter_dict(p_staged_event_id bigint, p_schedule_block_id bigint, p_parameter_dict_id bigint) RETURNS SETOF public.modification
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT modification.id, staged_event_id, block_parameter_id, type, old_value, new_value, timestamp
                 FROM modification
                    LEFT JOIN block_parameter on modification.block_parameter_id = block_parameter.id
                 WHERE staged_event_id = p_staged_event_id
                 AND parameter_dict_id = p_parameter_dict_id
                 AND block_parameter.schedule_block_id = p_schedule_block_id
                 LIMIT 1;
END;
$$;


ALTER FUNCTION public.find_modification_for_staged_event_and_parameter_dict(p_staged_event_id bigint, p_schedule_block_id bigint, p_parameter_dict_id bigint) OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 16386)
-- Name: addressee; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.addressee (
    dtype character varying(31) NOT NULL,
    id bigint NOT NULL,
    email character varying(255),
    first_name character varying(255),
    last_name character varying(255),
    password character varying(255),
    user_name character varying(255)
);


ALTER TABLE public.addressee OWNER TO postgres;

--
-- TOC entry 242 (class 1255 OID 16617)
-- Name: get_addressees_for_schedule_tag(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_addressees_for_schedule_tag(p_schedule_tag_id bigint) RETURNS SETOF public.addressee
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT addressee.dtype, addressee.id, email, first_name, last_name, password, user_name
                 FROM addressee
                          INNER JOIN schedule_tag_addressee sta on addressee.id = sta.addressee_id
                 WHERE sta.schedule_tag_id = p_schedule_tag_id;
END;

$$;


ALTER FUNCTION public.get_addressees_for_schedule_tag(p_schedule_tag_id bigint) OWNER TO postgres;

--
-- TOC entry 258 (class 1255 OID 16772)
-- Name: get_modifications_for_staged_event(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_modifications_for_staged_event(p_staged_event_id bigint) RETURNS TABLE(id bigint, staged_event_id bigint, block_parameter_id bigint, block_name character varying, parameter_name character varying, type character varying, old_value character varying, new_value character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT modification.id,
                        modification.staged_event_id,
                        modification.block_parameter_id,
                        schedule_block.name,
                        parameter_dict.name,
                        modification.type,
                        modification.old_value,
                        modification.new_value
                 FROM modification
                          INNER JOIN staged_event on modification.staged_event_id = staged_event.id
                          INNER JOIN block_parameter on modification.block_parameter_id = block_parameter.id
                          INNER JOIN schedule_block on block_parameter.schedule_block_id = schedule_block.id
                          INNER JOIN parameter_dict on block_parameter.parameter_dict_id = parameter_dict.id
                 WHERE modification.staged_event_id = p_staged_event_id;
END;
$$;


ALTER FUNCTION public.get_modifications_for_staged_event(p_staged_event_id bigint) OWNER TO postgres;

--
-- TOC entry 259 (class 1255 OID 16721)
-- Name: get_parameters_for_schedule_block(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_parameters_for_schedule_block(p_schedule_block_id bigint) RETURNS TABLE(id bigint, schedule_block_id bigint, parameter_name character varying, value character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY SELECT block_parameter.id, schedule_block.id, parameter_dict.name, block_parameter.value
                 FROM block_parameter
                          INNER JOIN schedule_block on block_parameter.schedule_block_id = schedule_block.id
                          INNER JOIN parameter_dict on block_parameter.parameter_dict_id = parameter_dict.id
                 WHERE schedule_block.id = p_schedule_block_id
                   AND block_parameter.deleted = false;
END;
$$;


ALTER FUNCTION public.get_parameters_for_schedule_block(p_schedule_block_id bigint) OWNER TO postgres;

--
-- TOC entry 213 (class 1259 OID 16400)
-- Name: addressee_group; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.addressee_group (
    id bigint NOT NULL,
    name character varying(255)
);


ALTER TABLE public.addressee_group OWNER TO postgres;

--
-- TOC entry 211 (class 1259 OID 16394)
-- Name: addressee_group_addressee; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.addressee_group_addressee (
    addressee_group_id bigint NOT NULL,
    addressee_id bigint NOT NULL
);


ALTER TABLE public.addressee_group_addressee OWNER TO postgres;

--
-- TOC entry 212 (class 1259 OID 16399)
-- Name: addressee_group_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.addressee_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.addressee_group_id_seq OWNER TO postgres;

--
-- TOC entry 3563 (class 0 OID 0)
-- Dependencies: 212
-- Name: addressee_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.addressee_group_id_seq OWNED BY public.addressee_group.id;


--
-- TOC entry 209 (class 1259 OID 16385)
-- Name: addressee_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.addressee_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.addressee_id_seq OWNER TO postgres;

--
-- TOC entry 3564 (class 0 OID 0)
-- Dependencies: 209
-- Name: addressee_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.addressee_id_seq OWNED BY public.addressee.id;


--
-- TOC entry 239 (class 1259 OID 16701)
-- Name: block_parameter; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.block_parameter (
    id bigint NOT NULL,
    parameter_dict_id bigint NOT NULL,
    schedule_block_id bigint NOT NULL,
    value character varying(1000) NOT NULL,
    deleted boolean DEFAULT false NOT NULL
);


ALTER TABLE public.block_parameter OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 16700)
-- Name: block_parameter_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.block_parameter_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.block_parameter_id_seq OWNER TO postgres;

--
-- TOC entry 3565 (class 0 OID 0)
-- Dependencies: 238
-- Name: block_parameter_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.block_parameter_id_seq OWNED BY public.block_parameter.id;


--
-- TOC entry 215 (class 1259 OID 16423)
-- Name: event; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event (
    id bigint NOT NULL,
    message_content character varying(255),
    name character varying(255),
    "timestamp" timestamp(6) without time zone,
    event_type_id bigint
);


ALTER TABLE public.event OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 16422)
-- Name: event_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.event_id_seq OWNER TO postgres;

--
-- TOC entry 3566 (class 0 OID 0)
-- Dependencies: 214
-- Name: event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.event_id_seq OWNED BY public.event.id;


--
-- TOC entry 219 (class 1259 OID 16442)
-- Name: event_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event_type (
    id bigint NOT NULL,
    message_content character varying(255),
    name character varying(255)
);


ALTER TABLE public.event_type OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 16431)
-- Name: event_type_addressee; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event_type_addressee (
    event_type_id bigint NOT NULL,
    addressee_id bigint NOT NULL
);


ALTER TABLE public.event_type_addressee OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16436)
-- Name: event_type_addressee_group; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event_type_addressee_group (
    event_type_id bigint NOT NULL,
    addressee_group_id bigint NOT NULL
);


ALTER TABLE public.event_type_addressee_group OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 16441)
-- Name: event_type_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.event_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.event_type_id_seq OWNER TO postgres;

--
-- TOC entry 3567 (class 0 OID 0)
-- Dependencies: 218
-- Name: event_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.event_type_id_seq OWNED BY public.event_type.id;


--
-- TOC entry 221 (class 1259 OID 16451)
-- Name: message; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.message (
    id bigint NOT NULL,
    accepted_by_addressee boolean NOT NULL,
    send_at timestamp(6) without time zone,
    addressee_id bigint,
    event_id bigint
);


ALTER TABLE public.message OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16450)
-- Name: message_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.message_id_seq OWNER TO postgres;

--
-- TOC entry 3568 (class 0 OID 0)
-- Dependencies: 220
-- Name: message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.message_id_seq OWNED BY public.message.id;


--
-- TOC entry 240 (class 1259 OID 16743)
-- Name: modification_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.modification_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.modification_id_seq OWNER TO postgres;

--
-- TOC entry 3569 (class 0 OID 0)
-- Dependencies: 240
-- Name: modification_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.modification_id_seq OWNED BY public.modification.id;


--
-- TOC entry 237 (class 1259 OID 16655)
-- Name: parameter_dict; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.parameter_dict (
    id bigint NOT NULL,
    name character varying(100) NOT NULL
);


ALTER TABLE public.parameter_dict OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 16654)
-- Name: parameter_dict_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.parameter_dict_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.parameter_dict_id_seq OWNER TO postgres;

--
-- TOC entry 3570 (class 0 OID 0)
-- Dependencies: 236
-- Name: parameter_dict_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.parameter_dict_id_seq OWNED BY public.parameter_dict.id;


--
-- TOC entry 223 (class 1259 OID 16467)
-- Name: permission; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.permission (
    id bigint NOT NULL,
    name character varying(255)
);


ALTER TABLE public.permission OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16466)
-- Name: permission_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.permission_id_seq OWNER TO postgres;

--
-- TOC entry 3571 (class 0 OID 0)
-- Dependencies: 222
-- Name: permission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.permission_id_seq OWNED BY public.permission.id;


--
-- TOC entry 225 (class 1259 OID 16474)
-- Name: role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.role (
    id bigint NOT NULL,
    name character varying(255)
);


ALTER TABLE public.role OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16473)
-- Name: role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.role_id_seq OWNER TO postgres;

--
-- TOC entry 3572 (class 0 OID 0)
-- Dependencies: 224
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.role_id_seq OWNED BY public.role.id;


--
-- TOC entry 226 (class 1259 OID 16480)
-- Name: role_permission; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.role_permission (
    permission_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE public.role_permission OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 16486)
-- Name: schedule_block; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.schedule_block (
    id bigint NOT NULL,
    end_date timestamp(6) without time zone NOT NULL,
    name character varying(255) NOT NULL,
    start_date timestamp(6) without time zone NOT NULL,
    schedule_tag_id bigint NOT NULL
);


ALTER TABLE public.schedule_block OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 16485)
-- Name: schedule_block_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.schedule_block_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.schedule_block_id_seq OWNER TO postgres;

--
-- TOC entry 3573 (class 0 OID 0)
-- Dependencies: 227
-- Name: schedule_block_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.schedule_block_id_seq OWNED BY public.schedule_block.id;


--
-- TOC entry 230 (class 1259 OID 16493)
-- Name: schedule_tag; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.schedule_tag (
    id bigint NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.schedule_tag OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 16598)
-- Name: schedule_tag_addressee; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.schedule_tag_addressee (
    id bigint NOT NULL,
    schedule_tag_id bigint NOT NULL,
    addressee_id bigint NOT NULL
);


ALTER TABLE public.schedule_tag_addressee OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 16597)
-- Name: schedule_tag_addressee_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.schedule_tag_addressee_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.schedule_tag_addressee_id_seq OWNER TO postgres;

--
-- TOC entry 3574 (class 0 OID 0)
-- Dependencies: 232
-- Name: schedule_tag_addressee_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.schedule_tag_addressee_id_seq OWNED BY public.schedule_tag_addressee.id;


--
-- TOC entry 229 (class 1259 OID 16492)
-- Name: schedule_tag_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.schedule_tag_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.schedule_tag_id_seq OWNER TO postgres;

--
-- TOC entry 3575 (class 0 OID 0)
-- Dependencies: 229
-- Name: schedule_tag_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.schedule_tag_id_seq OWNED BY public.schedule_tag.id;


--
-- TOC entry 234 (class 1259 OID 16640)
-- Name: staged_event_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.staged_event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.staged_event_id_seq OWNER TO postgres;

--
-- TOC entry 3576 (class 0 OID 0)
-- Dependencies: 234
-- Name: staged_event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.staged_event_id_seq OWNED BY public.staged_event.id;


--
-- TOC entry 231 (class 1259 OID 16499)
-- Name: user_account_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_account_role (
    user_account_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE public.user_account_role OWNER TO postgres;

--
-- TOC entry 3301 (class 2604 OID 16389)
-- Name: addressee id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.addressee ALTER COLUMN id SET DEFAULT nextval('public.addressee_id_seq'::regclass);


--
-- TOC entry 3302 (class 2604 OID 16403)
-- Name: addressee_group id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.addressee_group ALTER COLUMN id SET DEFAULT nextval('public.addressee_group_id_seq'::regclass);


--
-- TOC entry 3315 (class 2604 OID 16704)
-- Name: block_parameter id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.block_parameter ALTER COLUMN id SET DEFAULT nextval('public.block_parameter_id_seq'::regclass);


--
-- TOC entry 3303 (class 2604 OID 16426)
-- Name: event id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event ALTER COLUMN id SET DEFAULT nextval('public.event_id_seq'::regclass);


--
-- TOC entry 3304 (class 2604 OID 16445)
-- Name: event_type id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_type ALTER COLUMN id SET DEFAULT nextval('public.event_type_id_seq'::regclass);


--
-- TOC entry 3305 (class 2604 OID 16454)
-- Name: message id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message ALTER COLUMN id SET DEFAULT nextval('public.message_id_seq'::regclass);


--
-- TOC entry 3317 (class 2604 OID 16747)
-- Name: modification id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.modification ALTER COLUMN id SET DEFAULT nextval('public.modification_id_seq'::regclass);


--
-- TOC entry 3314 (class 2604 OID 16658)
-- Name: parameter_dict id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.parameter_dict ALTER COLUMN id SET DEFAULT nextval('public.parameter_dict_id_seq'::regclass);


--
-- TOC entry 3306 (class 2604 OID 16470)
-- Name: permission id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.permission ALTER COLUMN id SET DEFAULT nextval('public.permission_id_seq'::regclass);


--
-- TOC entry 3307 (class 2604 OID 16477)
-- Name: role id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role ALTER COLUMN id SET DEFAULT nextval('public.role_id_seq'::regclass);


--
-- TOC entry 3308 (class 2604 OID 16489)
-- Name: schedule_block id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_block ALTER COLUMN id SET DEFAULT nextval('public.schedule_block_id_seq'::regclass);


--
-- TOC entry 3309 (class 2604 OID 16496)
-- Name: schedule_tag id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_tag ALTER COLUMN id SET DEFAULT nextval('public.schedule_tag_id_seq'::regclass);


--
-- TOC entry 3310 (class 2604 OID 16601)
-- Name: schedule_tag_addressee id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_tag_addressee ALTER COLUMN id SET DEFAULT nextval('public.schedule_tag_addressee_id_seq'::regclass);


--
-- TOC entry 3311 (class 2604 OID 16644)
-- Name: staged_event id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.staged_event ALTER COLUMN id SET DEFAULT nextval('public.staged_event_id_seq'::regclass);


--
-- TOC entry 3526 (class 0 OID 16386)
-- Dependencies: 210
-- Data for Name: addressee; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.addressee (dtype, id, email, first_name, last_name, password, user_name) FROM stdin;
Addressee	2	paul.goodman@example.com	Paullll	Goodman	\N	\N
Addressee	4	robert.johnson@gmail.com	Robert	Johnson	\N	\N
Addressee	5	mark.robertson@example.com	Mark	Robertson	\N	\N
\.


--
-- TOC entry 3529 (class 0 OID 16400)
-- Dependencies: 213
-- Data for Name: addressee_group; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.addressee_group (id, name) FROM stdin;
\.


--
-- TOC entry 3527 (class 0 OID 16394)
-- Dependencies: 211
-- Data for Name: addressee_group_addressee; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.addressee_group_addressee (addressee_group_id, addressee_id) FROM stdin;
\.


--
-- TOC entry 3555 (class 0 OID 16701)
-- Dependencies: 239
-- Data for Name: block_parameter; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.block_parameter (id, parameter_dict_id, schedule_block_id, value, deleted) FROM stdin;
1	1	9	Colegium Oecologicum 101	f
3	1	8	Colegium Oecologicum 101	f
4	2	9	Zbigniew Szpunar	f
5	3	9	Zdalne	f
8	2	18	Zbigniew Szpunar	f
9	3	18	Zdalne	f
31	2	20	Rob Rob	f
32	9	19	Stacjonarne	t
29	2	19	Rob Robi	t
30	1	19	Col Mechanicum 106	t
34	2	19	Mark mark	f
6	1	18	Collegium Oecologicum 102	f
20	7	18	parval	t
28	8	18	pararara	t
13	6	18	param value	t
10	4	18	Laboratorium	t
12	5	18	next value	t
36	10	21	asdasddasd	t
37	11	21	asdasdasdasd	t
39	1	21	CO 2	t
40	1	21	CO 3	t
41	1	21	CO 3	t
38	2	21	Piotr Knychała	f
42	1	21	CO 2	t
43	1	21	CO 3	t
44	1	21	CO 3	t
45	1	21	CO 2	t
46	1	21	CO 3	f
47	1	22	CO 3	f
\.


--
-- TOC entry 3531 (class 0 OID 16423)
-- Dependencies: 215
-- Data for Name: event; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.event (id, message_content, name, "timestamp", event_type_id) FROM stdin;
\.


--
-- TOC entry 3535 (class 0 OID 16442)
-- Dependencies: 219
-- Data for Name: event_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.event_type (id, message_content, name) FROM stdin;
\.


--
-- TOC entry 3532 (class 0 OID 16431)
-- Dependencies: 216
-- Data for Name: event_type_addressee; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.event_type_addressee (event_type_id, addressee_id) FROM stdin;
\.


--
-- TOC entry 3533 (class 0 OID 16436)
-- Dependencies: 217
-- Data for Name: event_type_addressee_group; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.event_type_addressee_group (event_type_id, addressee_group_id) FROM stdin;
\.


--
-- TOC entry 3537 (class 0 OID 16451)
-- Dependencies: 221
-- Data for Name: message; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.message (id, accepted_by_addressee, send_at, addressee_id, event_id) FROM stdin;
\.


--
-- TOC entry 3557 (class 0 OID 16744)
-- Dependencies: 241
-- Data for Name: modification; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.modification (id, staged_event_id, block_parameter_id, type, old_value, new_value, "timestamp") FROM stdin;
6	1	31	CREATE_PARAMETER	\N	Rob Rob	2023-10-25 15:14:10.153435
7	1	32	CREATE_PARAMETER	\N	Stacjonarne	2023-10-25 16:11:09.008298
8	1	34	CREATE_PARAMETER	\N	Mark mark	2023-10-25 19:47:48.001825
9	1	6	UPDATE_PARAMETER	\N	Collegium Oecologicum 102	2023-10-25 19:54:43.898972
10	1	8	UPDATE_PARAMETER	\N	Zbigniew Szpunar	2023-10-25 19:54:43.89874
11	1	9	UPDATE_PARAMETER	\N	Zdalne	2023-10-25 19:54:43.901603
12	1	10	DELETE_PARAMETER	\N	Laboratorium	2023-10-25 19:54:43.917752
13	1	12	DELETE_PARAMETER	\N	next value	2023-10-25 19:54:43.917752
16	1	38	CREATE_PARAMETER	\N	Piotr Knychała	2023-10-25 20:16:19.171348
24	1	46	CREATE_PARAMETER	\N	CO 3	2023-10-25 20:53:16.281217
25	2	47	CREATE_PARAMETER	\N	CO 3	2023-10-27 09:37:34.474114
\.


--
-- TOC entry 3553 (class 0 OID 16655)
-- Dependencies: 237
-- Data for Name: parameter_dict; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.parameter_dict (id, name) FROM stdin;
1	Sala
2	Prowadzący
3	Forma
4	Rodzaj
5	Next block
6	Good param
7	better param
8	another param
9	Forma zajęć
10	asdasdasd
11	lolokokokok
\.


--
-- TOC entry 3539 (class 0 OID 16467)
-- Dependencies: 223
-- Data for Name: permission; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.permission (id, name) FROM stdin;
\.


--
-- TOC entry 3541 (class 0 OID 16474)
-- Dependencies: 225
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.role (id, name) FROM stdin;
\.


--
-- TOC entry 3542 (class 0 OID 16480)
-- Dependencies: 226
-- Data for Name: role_permission; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.role_permission (permission_id, role_id) FROM stdin;
\.


--
-- TOC entry 3544 (class 0 OID 16486)
-- Dependencies: 228
-- Data for Name: schedule_block; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.schedule_block (id, end_date, name, start_date, schedule_tag_id) FROM stdin;
8	2023-10-06 14:31:42	Test block 2	2023-10-06 14:31:42	4
9	2023-10-06 16:00:00	Test block 3	2023-10-06 10:00:00	4
11	2023-09-28 09:29:25	asdasd	2023-09-28 09:29:25	2
12	2023-09-29 09:29:25	asdasdasd	2023-09-29 09:29:25	2
16	2023-10-12 17:56:52	Nowy blok	2023-10-12 17:56:52	2
17	2023-10-25 13:00:00	Test blok 3	2023-10-25 12:00:00	2
20	2023-10-26 14:23:04	OOO	2023-10-26 15:00:00	7
19	2023-10-25 16:10:01	Blok	2023-10-25 16:10:01	7
18	2023-10-27 14:00:00	Zadanie inżynierskie 2	2023-10-27 08:45:00	7
21	2023-10-25 20:01:46	Zadanie inżynierskie 3	2023-10-25 20:01:46	7
22	2023-10-27 14:00:00	Zadanie inżynierskie 2	2023-10-27 08:45:00	8
\.


--
-- TOC entry 3546 (class 0 OID 16493)
-- Dependencies: 230
-- Data for Name: schedule_tag; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.schedule_tag (id, name) FROM stdin;
2	test schedule 2
4	Test schedule 5
7	Plan with staged event
8	Informatyka R4 S7 Stacjonarne 2023/24
\.


--
-- TOC entry 3549 (class 0 OID 16598)
-- Dependencies: 233
-- Data for Name: schedule_tag_addressee; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.schedule_tag_addressee (id, schedule_tag_id, addressee_id) FROM stdin;
12	2	4
13	2	5
15	2	2
17	4	2
18	4	4
20	4	5
\.


--
-- TOC entry 3551 (class 0 OID 16641)
-- Dependencies: 235
-- Data for Name: staged_event; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.staged_event (id, schedule_tag_id, committed, "timestamp") FROM stdin;
1	7	f	2023-10-21 15:26:13.302544
2	8	f	2023-10-27 09:04:53.474875
\.


--
-- TOC entry 3547 (class 0 OID 16499)
-- Dependencies: 231
-- Data for Name: user_account_role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_account_role (user_account_id, role_id) FROM stdin;
\.


--
-- TOC entry 3577 (class 0 OID 0)
-- Dependencies: 212
-- Name: addressee_group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.addressee_group_id_seq', 1, false);


--
-- TOC entry 3578 (class 0 OID 0)
-- Dependencies: 209
-- Name: addressee_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.addressee_id_seq', 5, true);


--
-- TOC entry 3579 (class 0 OID 0)
-- Dependencies: 238
-- Name: block_parameter_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.block_parameter_id_seq', 47, true);


--
-- TOC entry 3580 (class 0 OID 0)
-- Dependencies: 214
-- Name: event_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.event_id_seq', 1, false);


--
-- TOC entry 3581 (class 0 OID 0)
-- Dependencies: 218
-- Name: event_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.event_type_id_seq', 1, false);


--
-- TOC entry 3582 (class 0 OID 0)
-- Dependencies: 220
-- Name: message_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.message_id_seq', 1, false);


--
-- TOC entry 3583 (class 0 OID 0)
-- Dependencies: 240
-- Name: modification_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.modification_id_seq', 25, true);


--
-- TOC entry 3584 (class 0 OID 0)
-- Dependencies: 236
-- Name: parameter_dict_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.parameter_dict_id_seq', 11, true);


--
-- TOC entry 3585 (class 0 OID 0)
-- Dependencies: 222
-- Name: permission_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.permission_id_seq', 1, false);


--
-- TOC entry 3586 (class 0 OID 0)
-- Dependencies: 224
-- Name: role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.role_id_seq', 1, false);


--
-- TOC entry 3587 (class 0 OID 0)
-- Dependencies: 227
-- Name: schedule_block_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.schedule_block_id_seq', 22, true);


--
-- TOC entry 3588 (class 0 OID 0)
-- Dependencies: 232
-- Name: schedule_tag_addressee_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.schedule_tag_addressee_id_seq', 20, true);


--
-- TOC entry 3589 (class 0 OID 0)
-- Dependencies: 229
-- Name: schedule_tag_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.schedule_tag_id_seq', 8, true);


--
-- TOC entry 3590 (class 0 OID 0)
-- Dependencies: 234
-- Name: staged_event_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.staged_event_id_seq', 2, true);


--
-- TOC entry 3321 (class 2606 OID 16398)
-- Name: addressee_group_addressee addressee_group_addressee_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.addressee_group_addressee
    ADD CONSTRAINT addressee_group_addressee_pkey PRIMARY KEY (addressee_group_id, addressee_id);


--
-- TOC entry 3323 (class 2606 OID 16405)
-- Name: addressee_group addressee_group_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.addressee_group
    ADD CONSTRAINT addressee_group_pkey PRIMARY KEY (id);


--
-- TOC entry 3319 (class 2606 OID 16393)
-- Name: addressee addressee_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.addressee
    ADD CONSTRAINT addressee_pkey PRIMARY KEY (id);


--
-- TOC entry 3359 (class 2606 OID 16708)
-- Name: block_parameter block_parameter_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.block_parameter
    ADD CONSTRAINT block_parameter_pkey PRIMARY KEY (id);


--
-- TOC entry 3325 (class 2606 OID 16430)
-- Name: event event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT event_pkey PRIMARY KEY (id);


--
-- TOC entry 3329 (class 2606 OID 16440)
-- Name: event_type_addressee_group event_type_addressee_group_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_type_addressee_group
    ADD CONSTRAINT event_type_addressee_group_pkey PRIMARY KEY (event_type_id, addressee_group_id);


--
-- TOC entry 3327 (class 2606 OID 16435)
-- Name: event_type_addressee event_type_addressee_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_type_addressee
    ADD CONSTRAINT event_type_addressee_pkey PRIMARY KEY (event_type_id, addressee_id);


--
-- TOC entry 3331 (class 2606 OID 16449)
-- Name: event_type event_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_type
    ADD CONSTRAINT event_type_pkey PRIMARY KEY (id);


--
-- TOC entry 3333 (class 2606 OID 16456)
-- Name: message message_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message
    ADD CONSTRAINT message_pkey PRIMARY KEY (id);


--
-- TOC entry 3362 (class 2606 OID 16751)
-- Name: modification modification_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.modification
    ADD CONSTRAINT modification_pkey PRIMARY KEY (id);


--
-- TOC entry 3355 (class 2606 OID 16662)
-- Name: parameter_dict parameter_dict_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.parameter_dict
    ADD CONSTRAINT parameter_dict_name_key UNIQUE (name);


--
-- TOC entry 3357 (class 2606 OID 16660)
-- Name: parameter_dict parameter_dict_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.parameter_dict
    ADD CONSTRAINT parameter_dict_pkey PRIMARY KEY (id);


--
-- TOC entry 3335 (class 2606 OID 16472)
-- Name: permission permission_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.permission
    ADD CONSTRAINT permission_pkey PRIMARY KEY (id);


--
-- TOC entry 3339 (class 2606 OID 16484)
-- Name: role_permission role_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role_permission
    ADD CONSTRAINT role_permission_pkey PRIMARY KEY (permission_id, role_id);


--
-- TOC entry 3337 (class 2606 OID 16479)
-- Name: role role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 3341 (class 2606 OID 16491)
-- Name: schedule_block schedule_block_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_block
    ADD CONSTRAINT schedule_block_pkey PRIMARY KEY (id);


--
-- TOC entry 3349 (class 2606 OID 16603)
-- Name: schedule_tag_addressee schedule_tag_addressee_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_tag_addressee
    ADD CONSTRAINT schedule_tag_addressee_pkey PRIMARY KEY (id);


--
-- TOC entry 3343 (class 2606 OID 16498)
-- Name: schedule_tag schedule_tag_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_tag
    ADD CONSTRAINT schedule_tag_pkey PRIMARY KEY (id);


--
-- TOC entry 3353 (class 2606 OID 16648)
-- Name: staged_event staged_event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.staged_event
    ADD CONSTRAINT staged_event_pkey PRIMARY KEY (id);


--
-- TOC entry 3345 (class 2606 OID 16505)
-- Name: schedule_tag uk_dcbl6siq8igjbi2lrbu5pormb; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_tag
    ADD CONSTRAINT uk_dcbl6siq8igjbi2lrbu5pormb UNIQUE (name);


--
-- TOC entry 3364 (class 2606 OID 16753)
-- Name: modification unique_modification; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.modification
    ADD CONSTRAINT unique_modification UNIQUE (staged_event_id, block_parameter_id);


--
-- TOC entry 3351 (class 2606 OID 16624)
-- Name: schedule_tag_addressee unique_schedule_tag_addressee; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_tag_addressee
    ADD CONSTRAINT unique_schedule_tag_addressee UNIQUE (schedule_tag_id, addressee_id);


--
-- TOC entry 3347 (class 2606 OID 16503)
-- Name: user_account_role user_account_role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_account_role
    ADD CONSTRAINT user_account_role_pkey PRIMARY KEY (user_account_id, role_id);


--
-- TOC entry 3360 (class 1259 OID 16774)
-- Name: unique_block_parameter; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX unique_block_parameter ON public.block_parameter USING btree (schedule_block_id, parameter_dict_id) WHERE (deleted = false);


--
-- TOC entry 3369 (class 2606 OID 16536)
-- Name: event_type_addressee fk1iq5pdmqljsip6ljtbh8kifg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_type_addressee
    ADD CONSTRAINT fk1iq5pdmqljsip6ljtbh8kifg FOREIGN KEY (event_type_id) REFERENCES public.event_type(id);


--
-- TOC entry 3372 (class 2606 OID 16551)
-- Name: message fk55n0v74app8x6wsfbq0d54yhs; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message
    ADD CONSTRAINT fk55n0v74app8x6wsfbq0d54yhs FOREIGN KEY (addressee_id) REFERENCES public.addressee(id);


--
-- TOC entry 3370 (class 2606 OID 16541)
-- Name: event_type_addressee_group fk6nan52b35cflvpfu7xypsh1s2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_type_addressee_group
    ADD CONSTRAINT fk6nan52b35cflvpfu7xypsh1s2 FOREIGN KEY (addressee_group_id) REFERENCES public.addressee_group(id);


--
-- TOC entry 3371 (class 2606 OID 16546)
-- Name: event_type_addressee_group fk8kknmp2vujfr7lifthse9yfr2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_type_addressee_group
    ADD CONSTRAINT fk8kknmp2vujfr7lifthse9yfr2 FOREIGN KEY (event_type_id) REFERENCES public.event_type(id);


--
-- TOC entry 3377 (class 2606 OID 16586)
-- Name: user_account_role fk8rnrrtsm2cgvvk3r0dysgg9rd; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_account_role
    ADD CONSTRAINT fk8rnrrtsm2cgvvk3r0dysgg9rd FOREIGN KEY (role_id) REFERENCES public.role(id);


--
-- TOC entry 3380 (class 2606 OID 16609)
-- Name: schedule_tag_addressee fk_addressee_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_tag_addressee
    ADD CONSTRAINT fk_addressee_id FOREIGN KEY (addressee_id) REFERENCES public.addressee(id);


--
-- TOC entry 3385 (class 2606 OID 16759)
-- Name: modification fk_block_parameter_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.modification
    ADD CONSTRAINT fk_block_parameter_id FOREIGN KEY (block_parameter_id) REFERENCES public.block_parameter(id);


--
-- TOC entry 3383 (class 2606 OID 16716)
-- Name: block_parameter fk_parameter_dict_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.block_parameter
    ADD CONSTRAINT fk_parameter_dict_id FOREIGN KEY (parameter_dict_id) REFERENCES public.parameter_dict(id);


--
-- TOC entry 3382 (class 2606 OID 16711)
-- Name: block_parameter fk_schedule_block; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.block_parameter
    ADD CONSTRAINT fk_schedule_block FOREIGN KEY (schedule_block_id) REFERENCES public.schedule_block(id);


--
-- TOC entry 3379 (class 2606 OID 16604)
-- Name: schedule_tag_addressee fk_schedule_tag; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_tag_addressee
    ADD CONSTRAINT fk_schedule_tag FOREIGN KEY (schedule_tag_id) REFERENCES public.schedule_tag(id);


--
-- TOC entry 3381 (class 2606 OID 16649)
-- Name: staged_event fk_schedule_tag; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.staged_event
    ADD CONSTRAINT fk_schedule_tag FOREIGN KEY (schedule_tag_id) REFERENCES public.schedule_tag(id);


--
-- TOC entry 3384 (class 2606 OID 16754)
-- Name: modification fk_staged_event; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.modification
    ADD CONSTRAINT fk_staged_event FOREIGN KEY (staged_event_id) REFERENCES public.staged_event(id);


--
-- TOC entry 3374 (class 2606 OID 16571)
-- Name: role_permission fka6jx8n8xkesmjmv6jqug6bg68; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role_permission
    ADD CONSTRAINT fka6jx8n8xkesmjmv6jqug6bg68 FOREIGN KEY (role_id) REFERENCES public.role(id);


--
-- TOC entry 3366 (class 2606 OID 16511)
-- Name: addressee_group_addressee fkbn0672f0j7ygwtqh06tf46ebs; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.addressee_group_addressee
    ADD CONSTRAINT fkbn0672f0j7ygwtqh06tf46ebs FOREIGN KEY (addressee_group_id) REFERENCES public.addressee_group(id);


--
-- TOC entry 3368 (class 2606 OID 16531)
-- Name: event_type_addressee fkcp4k2a1g6wbfboybncg2usmq6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_type_addressee
    ADD CONSTRAINT fkcp4k2a1g6wbfboybncg2usmq6 FOREIGN KEY (addressee_id) REFERENCES public.addressee(id);


--
-- TOC entry 3365 (class 2606 OID 16506)
-- Name: addressee_group_addressee fkdf71fs7jhwplxpbpv54845fnl; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.addressee_group_addressee
    ADD CONSTRAINT fkdf71fs7jhwplxpbpv54845fnl FOREIGN KEY (addressee_id) REFERENCES public.addressee(id);


--
-- TOC entry 3378 (class 2606 OID 16591)
-- Name: user_account_role fke6yix1u0fyhtyxmiuh0esl6n5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_account_role
    ADD CONSTRAINT fke6yix1u0fyhtyxmiuh0esl6n5 FOREIGN KEY (user_account_id) REFERENCES public.addressee(id);


--
-- TOC entry 3375 (class 2606 OID 16576)
-- Name: role_permission fkf8yllw1ecvwqy3ehyxawqa1qp; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role_permission
    ADD CONSTRAINT fkf8yllw1ecvwqy3ehyxawqa1qp FOREIGN KEY (permission_id) REFERENCES public.permission(id);


--
-- TOC entry 3367 (class 2606 OID 16526)
-- Name: event fkgxoo7ftgbsrwr4i27wb9ylu1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT fkgxoo7ftgbsrwr4i27wb9ylu1 FOREIGN KEY (event_type_id) REFERENCES public.event_type(id);


--
-- TOC entry 3373 (class 2606 OID 16556)
-- Name: message fkn49j1k5pgk4e0h71etqxc0r4g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message
    ADD CONSTRAINT fkn49j1k5pgk4e0h71etqxc0r4g FOREIGN KEY (event_id) REFERENCES public.event(id);


--
-- TOC entry 3376 (class 2606 OID 16581)
-- Name: schedule_block fkog6ixjv5quu8o23plkq3jdtqn; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_block
    ADD CONSTRAINT fkog6ixjv5quu8o23plkq3jdtqn FOREIGN KEY (schedule_tag_id) REFERENCES public.schedule_tag(id);


-- Completed on 2023-11-07 16:16:17 CET

--
-- PostgreSQL database dump complete
--

