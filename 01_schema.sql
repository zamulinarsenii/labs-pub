-- Execute in the existing database music_store. Fresh schema, no DROP DATABASE.
CREATE SEQUENCE artist_seq START WITH 1;
CREATE SEQUENCE album_seq START WITH 1;
CREATE SEQUENCE track_seq START WITH 1;
CREATE TABLE artist(id BIGINT PRIMARY KEY DEFAULT nextval('artist_seq'),name VARCHAR(150) NOT NULL UNIQUE CHECK(length(trim(name))>0));
CREATE TABLE album(id BIGINT PRIMARY KEY DEFAULT nextval('album_seq'),artist_id BIGINT NOT NULL REFERENCES artist(id),title VARCHAR(150) NOT NULL CHECK(length(trim(title))>0),genre VARCHAR(80) NOT NULL CHECK(length(trim(genre))>0),UNIQUE(artist_id,title));
CREATE TABLE track(id BIGINT PRIMARY KEY DEFAULT nextval('track_seq'),album_id BIGINT NOT NULL REFERENCES album(id),title VARCHAR(150) NOT NULL CHECK(length(trim(title))>0),duration_seconds INTEGER NOT NULL CHECK(duration_seconds>0),UNIQUE(album_id,title));
