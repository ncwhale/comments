-- +goose Up
-- +goose StatementBegin
CREATE TABLE public.comments
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    root_id uuid NOT NULL,
    reply_id uuid DEFAULT NULL,
    content text,
    meta jsonb,
    PRIMARY KEY (id)
);
-- +goose StatementEnd

-- +goose Down
-- +goose StatementBegin
DROP TABLE public.comments;
-- +goose StatementEnd
