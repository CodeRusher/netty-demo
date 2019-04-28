package com.code.ting.netty.proxy.http.chain.context;


public enum Status {
    NEW,
    REQUEST_HEADER_READ,
    REQUEST_COMPLETED,
    RESPONSE_HEADER_READ,
    RESPONSE_COMPLETED
}
