SELECT CONCAT(f_name, ' ', l_name) AS full_name
FROM person
WHERE email = '%s';