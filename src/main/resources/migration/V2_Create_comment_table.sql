-- auto-generated definition
create table COMMENT
(
    ID            BIGINT auto_increment
        primary key,
    PARENT_ID     BIGINT,
    TYPE          INT,
    COMMENTATOR   BIGINT,
    GMT_CREATE    BIGINT,
    GMT_MODIFIED  BIGINT,
    LIKE_COUNT    BIGINT default 0,
    CONTENT       VARCHAR(1024),
    COMMENT_COUNT INT    default 0
);

