lexer grammar LangLexer;

@header {package coproswi;}

fragment SEP: [ \t\r\n]+;
fragment MAYSEP: [ \t\r\n]*;

L_PAREN: MAYSEP '(';
R_PAREN: MAYSEP ')' MAYSEP;

ADD_STATE_KW: 'add_state' SEP;
ADD_QUERY_TYPE_KW: 'add_query_type' SEP;
ADD_DATA_TYPE_KW: 'add_data_type' SEP;
STABLE_KW: 'stable' SEP;
TRANSIENT_KW: 'transient' SEP;
DEFINE_CC_KW: 'define_cache_controller' SEP;
DEFINE_CMGR_KW: 'define_coherency_manager' SEP;
SET_DEFAULT_STATE_KW: 'set_default_state' SEP;

SEND_QUERY_KW: 'send_query' SEP;
SET_STATE_KW: 'set_state' SEP;
HIT_KW: 'hit' SEP;
SUCCEED_KW: 'hit' MAYSEP;
BUS_ACCESS_KW: 'bus_access' SEP;
IGNORE_KW: 'ignore' MAYSEP;
STALL_KW: 'stall' MAYSEP;
RESUME_KW: 'resume' MAYSEP;
NONE_KW: 'none' MAYSEP;
MARK_INTERFERENCE_KW: 'mark_interference' SEP;
MINOR_INTERFERENCE_KW: 'minor' MAYSEP;
EXPELLING_INTERFERENCE_KW: 'expelling' MAYSEP;
DEMOTING_INTERFERENCE_KW: 'demoting' MAYSEP;
STORE_REPLY_TO_KW: ('store_reply_to' | 'store_owner') MAYSEP;
RESET_REPLY_TO_KW: ('reset_reply_to' | 'reset_owner') MAYSEP;

WRITE_DATA_KW: 'write_data' MAYSEP;
READ_DATA_KW: 'read_data' MAYSEP;

REPLY_TO_KW: 'reply_to' MAYSEP;
SENDER_KW: 'sender' MAYSEP;
MEMORY_KW: 'memory' MAYSEP;
ERL_BANG_KW: '!' MAYSEP;

IF_IS_OWNER_KW: 'if_is_owner' SEP;

WS: SEP;

ID: [A-Z][A-Z0-9_]+;

COMMENT: (';;'|'#'|'//'|'%'|'--') .*? '\n' -> channel(HIDDEN);
