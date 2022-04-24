CREATE TABLE IF NOT EXISTS compute_intensive (id SERIAL PRIMARY KEY, val BIGINT);

CREATE TABLE IF NOT EXISTS data_intensive (
    id SERIAL PRIMARY KEY,
    key_id INTEGER,
    array_id INTEGER,
    val BIGINT
);