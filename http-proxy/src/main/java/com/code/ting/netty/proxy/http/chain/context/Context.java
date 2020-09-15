package com.code.ting.netty.proxy.http.chain.context;


import com.code.ting.netty.proxy.http.chain.FilterChain;
import io.netty.util.AttributeMap;

/**
 * todo: 为什么要继承AttributeMap
 */
public interface Context extends AttributeMap {

    FilterChain getChain();

    /**
     * 可以看做是Proxy内部定义的，一次Http事务的id
     */
    Long getId();

    Request getRequest();

    Response getResponse();

    void setCancelReason(CancelReason cancelReason);

}
