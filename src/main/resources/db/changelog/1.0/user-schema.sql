create table users(
                      username varchar(50) not null primary key,
                      password varchar(100) not null,
                      enabled boolean not null
);

create table authorities (
                             username varchar(50) not null,
                             authority varchar(50) not null,
                             constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index ix_auth_username on authorities (username,authority);

-- Стартовые пользователи
insert into users(username, password, enabled) values ('admin', '$2y$12$mTQ8Y/mnlItOsMUEH/EVJOuUF558ChpzWSLJ9HuB0cM.5eboqvftO', true);
insert into authorities(username, authority) values ('admin', 'ROLE_ADMIN');
insert into authorities(username, authority) values ('admin', 'ROLE_EDITOR');

insert into users(username, password, enabled) values ('editor', '$2y$12$mTQ8Y/mnlItOsMUEH/EVJOuUF558ChpzWSLJ9HuB0cM.5eboqvftO', true);
insert into authorities(username, authority) values ('editor', 'ROLE_EDITOR');

insert into users(username, password, enabled) values ('user', '$2y$12$mTQ8Y/mnlItOsMUEH/EVJOuUF558ChpzWSLJ9HuB0cM.5eboqvftO', true);
insert into authorities(username, authority) values ('user', 'ROLE_USER');