create table if not exists users (
    id integer primary key,
    first_name text not null
);

insert into users (first_name) values
  ('Jerry'),
  ('Jenny'),
  ('George'),
  ('Johanna'),
  ('John'),
  ('Anne');
