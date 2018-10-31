package com.clanassist.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Harrison on 9/29/2014.
 */
public class HitManager {

    public static Map<Integer, Boolean> hasHitClanDetails;
    public static Map<Integer, Boolean> hasHitPlayerProfile;

    public static void clear() {
        hasHitPlayerProfile = null;
        hasHitClanDetails = null;
    }

    public static boolean hasHitClanProfile(Integer clanId) {
        if (hasHitClanDetails == null) {
            hasHitClanDetails = new HashMap<Integer, Boolean>();
        }
        try {
            return hasHitClanDetails.get(clanId);
        } catch (Exception e) {
            return false;
        }
    }

    public static void hitClanProfile(Integer clanId) {
        if (hasHitClanDetails == null) {
            hasHitClanDetails = new HashMap<Integer, Boolean>();
        }
        hasHitClanDetails.put(clanId, true);
    }

    public static void removeClanProfileHit(Integer clanId) {
        if (hasHitClanDetails != null) {
            hasHitClanDetails.remove(clanId);
        }
    }

    public static boolean hasHitPlayerProfile(Integer playerId) {
        if (hasHitPlayerProfile == null) {
            hasHitPlayerProfile = new HashMap<Integer, Boolean>();
        }
        try {
            return hasHitPlayerProfile.get(playerId);
        } catch (Exception e) {
            return false;
        }
    }

    public static void hitPlayerProfile(Integer playerId) {
        if (hasHitPlayerProfile == null) {
            hasHitPlayerProfile = new HashMap<Integer, Boolean>();
        }
        hasHitPlayerProfile.put(playerId, true);
    }

    public static void removePlayerProfileHit(Integer playerId) {
        if (hasHitPlayerProfile != null) {
            hasHitPlayerProfile.remove(playerId);
        }
    }
}