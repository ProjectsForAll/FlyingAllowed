package host.plas.flyingallowed.timers;

import host.plas.bou.scheduling.BaseRunnable;
import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.data.MoveDataCache;

public class CacheTimer extends BaseRunnable {
    public CacheTimer() {
        super(0, getCachePeriod()); // 5 minutes
    }

    @Override
    public void run() {
        if (getPeriod() != getCachePeriod()) setPeriod(getCachePeriod());

        MoveDataCache.flush();
    }

    public static long getCachePeriod() {
        return FlyingAllowed.getMainConfig().getLookupCachedTicks();
    }
}
