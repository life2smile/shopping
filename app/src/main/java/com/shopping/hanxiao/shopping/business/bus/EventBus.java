package com.shopping.hanxiao.shopping.business.bus;

import com.squareup.otto.Bus;

/**
 * Created by wenzhi on 17/9/19.
 */

public final class EventBus {
    private static class BusHolder {
        private static Bus mBus = new Bus();
    }

    public static Bus getBus() {
        return BusHolder.mBus;
    }


    public static void sendEvent(Object event) {
        getBus().post(event);
    }
}
