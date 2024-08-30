create sequence client_seq start with 1 increment by 1
create sequence profil_seq start with 1 increment by 1
create sequence right_seq start with 1 increment by 1
create sequence subscription_seq start with 1 increment by 1
create sequence user_seq start with 1 increment by 1

    create table bcp_sec_client (
       id bigint not null,
        city varchar(255),
        country varchar(255),
        email varchar(255),
        phone varchar(255),
        postal_code varchar(255),
        street varchar(255),
        code varchar(50) not null,
        default_language varchar(255),
        first_name varchar(100),
        name varchar(100) not null,
        nature varchar(255) not null,
        type varchar(255) not null,
        primary key (id)
    )

    create table bcp_sec_profil (
       id bigint not null,
        client varchar(255) not null,
        code varchar(25) not null,
        description varchar(50),
        name varchar(50) not null,
        type varchar(255) not null,
        primary key (id)
    )

    create table bcp_sec_right (
       id bigint not null,
        functionality varchar(255) not null,
        level varchar(255) not null,
        profile_id bigint,
        user_id bigint,
        primary key (id)
    )

    create table bcp_sec_subscription (
       id bigint not null,
        active boolean not null,
        max_project integer not null,
        max_user integer not null,
        project_count integer not null,
        subscription_from timestamp,
        subscription_to timestamp,
        user_count integer not null,
        primary key (id)
    )

    create table bcp_sec_user (
       id bigint not null,
        account_expired boolean not null,
        account_locked boolean not null,
        active boolean not null,
        city varchar(255),
        country varchar(255),
        email varchar(255),
        phone varchar(255),
        postal_code varchar(255),
        street varchar(255),
        client varchar(255),
        credentials_expired boolean not null,
        default_language varchar(255),
        first_name varchar(255),
        login varchar(50) not null,
        name varchar(50) not null,
        primary key (id)
    )
create sequence client_seq start with 1 increment by 1
create sequence profil_seq start with 1 increment by 1
create sequence right_seq start with 1 increment by 1
create sequence subscription_seq start with 1 increment by 1
create sequence user_seq start with 1 increment by 1

    create table bcp_sec_client (
       id bigint not null,
        city varchar(255),
        country varchar(255),
        email varchar(255),
        phone varchar(255),
        postal_code varchar(255),
        street varchar(255),
        code varchar(50) not null,
        default_language varchar(255),
        first_name varchar(100),
        name varchar(100) not null,
        nature varchar(255) not null,
        type varchar(255) not null,
        primary key (id)
    )

    create table bcp_sec_profil (
       id bigint not null,
        client varchar(255) not null,
        code varchar(25) not null,
        description varchar(50),
        name varchar(50) not null,
        type varchar(255) not null,
        primary key (id)
    )

    create table bcp_sec_right (
       id bigint not null,
        functionality varchar(255) not null,
        level varchar(255) not null,
        profile_id bigint,
        user_id bigint,
        primary key (id)
    )

    create table bcp_sec_subscription (
       id bigint not null,
        active boolean not null,
        max_project integer not null,
        max_user integer not null,
        project_count integer not null,
        subscription_from timestamp,
        subscription_to timestamp,
        user_count integer not null,
        primary key (id)
    )

    create table bcp_sec_user (
       id bigint not null,
        account_expired boolean not null,
        account_locked boolean not null,
        active boolean not null,
        city varchar(255),
        country varchar(255),
        email varchar(255),
        phone varchar(255),
        postal_code varchar(255),
        street varchar(255),
        client varchar(255),
        credentials_expired boolean not null,
        default_language varchar(255),
        first_name varchar(255),
        login varchar(50) not null,
        name varchar(50) not null,
        primary key (id)
    )
create sequence client_seq start with 1 increment by 1
create sequence profil_seq start with 1 increment by 1
create sequence right_seq start with 1 increment by 1
create sequence subscription_seq start with 1 increment by 1
create sequence user_seq start with 1 increment by 1

    create table bcp_sec_client (
       id bigint not null,
        city varchar(255),
        country varchar(255),
        email varchar(255),
        phone varchar(255),
        postal_code varchar(255),
        street varchar(255),
        code varchar(50) not null,
        default_language varchar(255),
        first_name varchar(100),
        name varchar(100) not null,
        nature varchar(255) not null,
        type varchar(255) not null,
        primary key (id)
    )

    create table bcp_sec_profil (
       id bigint not null,
        client varchar(255) not null,
        code varchar(25) not null,
        description varchar(50),
        name varchar(50) not null,
        type varchar(255) not null,
        primary key (id)
    )

    create table bcp_sec_right (
       id bigint not null,
        functionality varchar(255) not null,
        level varchar(255) not null,
        profile_id bigint,
        user_id bigint,
        primary key (id)
    )

    create table bcp_sec_subscription (
       id bigint not null,
        active boolean not null,
        max_project integer not null,
        max_user integer not null,
        project_count integer not null,
        subscription_from timestamp,
        subscription_to timestamp,
        user_count integer not null,
        primary key (id)
    )

    create table bcp_sec_user (
       id bigint not null,
        account_expired boolean not null,
        account_locked boolean not null,
        active boolean not null,
        city varchar(255),
        country varchar(255),
        email varchar(255),
        phone varchar(255),
        postal_code varchar(255),
        street varchar(255),
        client varchar(255),
        credentials_expired boolean not null,
        default_language varchar(255),
        first_name varchar(255),
        login varchar(50) not null,
        name varchar(50) not null,
        primary key (id)
    )
create sequence client_seq start with 1 increment by 1
create sequence profil_seq start with 1 increment by 1
create sequence right_seq start with 1 increment by 1
create sequence subscription_seq start with 1 increment by 1
create sequence user_seq start with 1 increment by 1

    create table bcp_sec_client (
       id bigint not null,
        city varchar(255),
        country varchar(255),
        email varchar(255),
        phone varchar(255),
        postal_code varchar(255),
        street varchar(255),
        code varchar(50) not null,
        default_language varchar(255),
        first_name varchar(100),
        name varchar(100) not null,
        nature varchar(255) not null,
        type varchar(255) not null,
        primary key (id)
    )

    create table bcp_sec_profil (
       id bigint not null,
        client varchar(255) not null,
        code varchar(25) not null,
        description varchar(50),
        name varchar(50) not null,
        type varchar(255) not null,
        primary key (id)
    )

    create table bcp_sec_right (
       id bigint not null,
        functionality varchar(255) not null,
        level varchar(255) not null,
        profile_id bigint,
        user_id bigint,
        primary key (id)
    )

    create table bcp_sec_subscription (
       id bigint not null,
        active boolean not null,
        max_project integer not null,
        max_user integer not null,
        project_count integer not null,
        subscription_from timestamp,
        subscription_to timestamp,
        user_count integer not null,
        primary key (id)
    )

    create table bcp_sec_user (
       id bigint not null,
        account_expired boolean not null,
        account_locked boolean not null,
        active boolean not null,
        city varchar(255),
        country varchar(255),
        email varchar(255),
        phone varchar(255),
        postal_code varchar(255),
        street varchar(255),
        client varchar(255),
        credentials_expired boolean not null,
        default_language varchar(255),
        first_name varchar(255),
        login varchar(50) not null,
        name varchar(50) not null,
        primary key (id)
    )
create sequence client_seq start with 1 increment by 1
create sequence profil_seq start with 1 increment by 1
create sequence right_seq start with 1 increment by 1
create sequence subscription_seq start with 1 increment by 1
create sequence user_seq start with 1 increment by 1

    create table bcp_sec_client (
       id bigint not null,
        city varchar(255),
        country varchar(255),
        email varchar(255),
        phone varchar(255),
        postal_code varchar(255),
        street varchar(255),
        code varchar(50) not null,
        default_language varchar(255),
        first_name varchar(100),
        name varchar(100) not null,
        nature varchar(255) not null,
        type varchar(255) not null,
        primary key (id)
    )

    create table bcp_sec_profil (
       id bigint not null,
        client varchar(255) not null,
        code varchar(25) not null,
        description varchar(50),
        name varchar(50) not null,
        type varchar(255) not null,
        primary key (id)
    )

    create table bcp_sec_right (
       id bigint not null,
        functionality varchar(255) not null,
        level varchar(255) not null,
        profile_id bigint,
        user_id bigint,
        primary key (id)
    )

    create table bcp_sec_subscription (
       id bigint not null,
        active boolean not null,
        max_project integer not null,
        max_user integer not null,
        project_count integer not null,
        subscription_from timestamp,
        subscription_to timestamp,
        user_count integer not null,
        primary key (id)
    )

    create table bcp_sec_user (
       id bigint not null,
        account_expired boolean not null,
        account_locked boolean not null,
        active boolean not null,
        city varchar(255),
        country varchar(255),
        email varchar(255),
        phone varchar(255),
        postal_code varchar(255),
        street varchar(255),
        client varchar(255),
        credentials_expired boolean not null,
        default_language varchar(255),
        first_name varchar(255),
        login varchar(50) not null,
        name varchar(50) not null,
        primary key (id)
    )
