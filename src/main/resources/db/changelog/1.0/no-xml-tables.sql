create table publication_details(
    id bigint not null primary key,
    code varchar(50),
    issue varchar(10),
    language varchar(10),
    constraint unique_uk_pub_details unique(code, issue, language),
    constraint foreign_fk_1 foreign key(id) references publications(id)
);