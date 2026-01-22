CREATE TABLE likes (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    prototype_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE (user_id, prototype_id),
    
    CONSTRAINT fk_likes_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_likes_prototype_id FOREIGN KEY (prototype_id) REFERENCES prototypes (id) ON DELETE CASCADE
);
