package com.code.ting.netty.proxy.http.chain.route;

import com.code.ting.netty.proxy.http.chain.context.Request;
import io.netty.util.Attribute;
import lombok.Data;

@Data
public class From<T> {

    Request request;
    Attribute<T> attr;

    public static <K> From<K> of(Request request, Attribute<K> attr) {
        From<K> from = new From<>();
        from.setRequest(request);
        from.setAttr(attr);
        return from;
    }

}
