UPDATE task

SET name = '%s',
    type_id = %d,
    start_time = '%s',
    end_time = '%s'

WHERE id = %d;