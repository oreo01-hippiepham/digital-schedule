SELECT start_time, end_time, task.name, task_type.task_name, task.id
FROM schedule

JOIN task
	ON schedule.task_id = task.id
JOIN person
	ON person.id = schedule.person_id
JOIN task_type
	ON task.type_id = task_type.id

WHERE email = '%s'
ORDER BY start_time
;