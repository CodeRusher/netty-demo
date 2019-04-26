package com.code.ting.netty.proxy.http.chain.proccesser;


import com.code.ting.netty.proxy.http.chain.Proccesser;
import com.code.ting.netty.proxy.http.chain.context.Context;
import org.apache.commons.lang3.StringUtils;

public class AuthProccessor implements Proccesser {

    @Override
    public boolean proccess(Context context) {
        if (StringUtils.isBlank(context.getRequest().getHead("auth"))) {
            return false;
        }

        return true;
    }
}
