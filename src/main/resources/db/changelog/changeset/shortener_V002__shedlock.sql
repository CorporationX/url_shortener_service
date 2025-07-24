CREATE TABLE shedlock (
      name varchar(64) primary key,
      lock_until timestamp(3) null,
      locked_at timestamp(3) null,
      locked_by varchar(255),
);