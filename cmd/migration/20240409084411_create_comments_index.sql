-- +goose NO TRANSACTION
-- +goose Up
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_comments_root_id on public.comments(root_id ASC);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_comments_reply_id on public.comments(reply_id ASC NULLS FIRST) NULLS DISTINCT;
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_comments_url on public.comments USING gin ((meta->'url') jsonb_path_ops);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_comments_created_at on public.comments USING gin ((meta->'created_at') jsonb_path_ops);

-- +goose Down
DROP INDEX IF EXISTS idx_comments_url;
DROP INDEX IF EXISTS idx_comments_created_at;
DROP INDEX IF EXISTS idx_comments_reply_id;
DROP INDEX IF EXISTS idx_comments_root_id;
