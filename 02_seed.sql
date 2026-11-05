INSERT INTO artist(name) VALUES ('Aurora Band'),('North Wind'),('Blue River'),('City Lights'),('Solar Trio');
INSERT INTO album(artist_id,title,genre) SELECT id,'Album '||id,CASE WHEN id%2=0 THEN 'Jazz' ELSE 'Rock' END FROM artist;
INSERT INTO track(album_id,title,duration_seconds) SELECT a.id,'Track '||n,CASE WHEN n=1 THEN 3 WHEN n=2 THEN 180 WHEN n=3 THEN 180 ELSE 180+n*10 END FROM album a CROSS JOIN generate_series(1,6) n WHERE a.id<>5 OR n<=3;
