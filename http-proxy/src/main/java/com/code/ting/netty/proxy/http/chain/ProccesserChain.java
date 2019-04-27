package com.code.ting.netty.proxy.http.chain;


import com.code.ting.netty.proxy.http.chain.context.Context;
import java.util.LinkedList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProccesserChain {

    private LinkedList<Proccesser> proccessers = new LinkedList<>();


    public void addProccesser(Proccesser proccesser) {
        proccessers.add(proccesser);
    }

    public void fireChain(Context context) {

        int index = 0;
        boolean throwed = false;
        for (int i = 0; i < proccessers.size(); i++) {
            Proccesser p = proccessers.get(i);
            index = i;
            try {
                p.pre(context);
                if (!p.proccess(context)) {
                    break;
                }
            } catch (Throwable t) {
                log.error("error in chain :{}", t.getMessage(), t);
                throwed = true;
            }

            if (throwed) {
                break;
            }
        }

        // Proccesser.after must invoke
        while (index >= 0) {
            try {
                Proccesser p = proccessers.get(index);
                p.after(context);
            } catch (Throwable t) {
                log.error("error in chain.after :{}", t.getMessage(), t);
                throwed = true;
            }

            index--;
        }

        if(throwed){
            handleThrowable(context);
        }
    }

    private void handleThrowable(Context context){
        context.getResponse().writeBody("error".getBytes());
    }

}
