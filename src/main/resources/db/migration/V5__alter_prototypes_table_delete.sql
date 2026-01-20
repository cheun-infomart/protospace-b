

ALTER TABLE prototypes DROP CONSTRAINT IF EXISTS prototypes_user_id_fkey;
ALTER TABLE prototypes 
ADD CONSTRAINT fk_prototypes_user_id 
FOREIGN KEY (user_id) REFERENCES users(id) 
ON DELETE CASCADE;

ALTER TABLE comments DROP CONSTRAINT IF EXISTS comments_user_id_fkey;
ALTER TABLE comments 
ADD CONSTRAINT fk_comments_user_id 
FOREIGN KEY (user_id) REFERENCES users(id) 
ON DELETE CASCADE;