-- auto-generated definition
create table NOTIFICATION
(
    ID            BIGINT auto_increment,
    NOTIFIER      BIGINT,
    RECEIVER      BIGINT,
    OUTERID       BIGINT,
    TYPE          INT           not null,
    GMT_CREATE    BIGINT,
    STATUS        INT default 0 not null,
    NOTIFIER_NAME VARCHAR(100),
    OUTER_TITLE   VARCHAR(256),
    constraint NOTIFICATION_PK
        primary key (ID)
);

