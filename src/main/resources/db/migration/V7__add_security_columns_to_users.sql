alter table users add column security_question varchar(255);
alter table users add column security_answer varchar(255);

update users set security_question = '好きな食べ物は？', security_answer = 'リンゴ';

ALTER TABLE users ALTER COLUMN security_question SET NOT NULL;
ALTER TABLE users ALTER COLUMN security_answer SET NOT NULL;