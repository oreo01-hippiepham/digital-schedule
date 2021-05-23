SELECT task.id, task.name, start_time, end_time, task_type.task_name, task.hash FROM task
JOIN schedule ON task.id = schedule.task_id
JOIN person ON person.id = schedule.person_id
JOIN task_type ON task.type_id = task_type.id
WHERE email = '%s'
ORDER BY start_time
;

