CREATE TABLE url (
      id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
      url VARCHAR NOT NULL,
      hash VARCHAR(6) NOT NULL,
      created_at timestamptz DEFAULT current_timestamp NOT NULL
);