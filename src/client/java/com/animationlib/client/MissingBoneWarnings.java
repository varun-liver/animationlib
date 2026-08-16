package com.animationlib.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Warns once (not every frame) when an animation's bone name doesn't exist on the model it's
 * being applied to, so a name mismatch is diagnosable instead of silently doing nothing. */
public final class MissingBoneWarnings {
    private static final Logger LOGGER = LoggerFactory.getLogger("animationlib");
    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();

    private MissingBoneWarnings() {
    }

    public static void warnOnce(int entityId, String bone, String modelClassName) {
        if (WARNED.add(entityId + ":" + bone)) {
            LOGGER.warn("animationlib: entity {} has no \"{}\" part on its {} model; that bone won't animate",
                    entityId, bone, modelClassName);
        }
    }
}
