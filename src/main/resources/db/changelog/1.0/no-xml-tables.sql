create table publication_details(
    id bigint not null primary key,
    code varchar(50) not null,
    issue varchar(10) not null,
    language varchar(10) not null,
    title varchar(255) not null,
    issue_date timestamp not null,
    load_datetime timestamp with time zone,
    constraint unique_uk_pub_details unique(code, issue, language),
    constraint foreign_fk_1 foreign key(id) references publications(id)
);