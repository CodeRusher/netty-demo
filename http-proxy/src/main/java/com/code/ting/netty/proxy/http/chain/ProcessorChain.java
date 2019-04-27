package com.code.ting.netty.proxy.http.chain;


import com.code.ting.netty.proxy.http.chain.context.Context;
import java.util.LinkedList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessorChain {

    private LinkedList<Processor> processors = new LinkedList<>();


    public void addProcessor(Processor processor) {
        processors.add(processor);
    }

    public void fireChain(Context context) {

        int index = 0;
        boolean throwed = false;
        for (int i = 0; i < processors.size(); i++) {
            Processor p = processors.get(i);
            index = i;
            try {
                p.pre(context);
                if (!p.process(context)) {
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

        // Processor.after must invoke
        while (index >= 0) {
            try {
                Processor p = processors.get(index);
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
