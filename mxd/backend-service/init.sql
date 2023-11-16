CREATE TABLE example_table (
    id serial PRIMARY KEY,
    response_data VARCHAR (50)
);
SELECT example_table FROM information_schema.tables WHERE table_schema = 'public';
