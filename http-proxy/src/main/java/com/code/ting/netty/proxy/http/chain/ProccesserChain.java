package com.code.ting.netty.proxy.http.chain;


import com.code.ting.netty.proxy.http.chain.context.Context;
import java.util.LinkedList;

public class ProccesserChain {

    LinkedList<Proccesser> proccessers = new LinkedList<>();

    public void addProccesser(Proccesser proccesser) {
        proccessers.add(proccesser);
    }

    public void fireChain(Context context) {
        for (Proccesser p : proccessers) {
            if (p.proccess(context)) {
                return;
            }
        }
    }

}
