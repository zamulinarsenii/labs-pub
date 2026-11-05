-- A: Albums containing at least five tracks. Include all ties for shortest.
SELECT a.title AS album,t.title AS track,t.duration_seconds
FROM album a JOIN track t ON t.album_id=a.id
WHERE a.id IN(SELECT album_id FROM track GROUP BY album_id HAVING count(*)>=5)
AND t.duration_seconds=(SELECT min(x.duration_seconds) FROM track x WHERE x.album_id=a.id)
ORDER BY a.id,t.id;
