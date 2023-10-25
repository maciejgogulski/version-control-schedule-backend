ALTER TABLE block_parameter
    ADD COLUMN
        deleted boolean NOT NULL DEFAULT false;