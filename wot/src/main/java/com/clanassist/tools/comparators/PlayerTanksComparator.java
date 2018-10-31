package com.clanassist.tools.comparators;

import android.content.Context;

import com.clanassist.CAApp;
import com.clanassist.model.infoobj.Tank;
import com.clanassist.model.infoobj.Tanks;
import com.clanassist.model.player.PlayerVehicleInfo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Harrison on 8/10/2014.
 */
public class PlayerTanksComparator {

    public Comparator<PlayerVehicleInfo> battlesComparator = new Comparator<PlayerVehicleInfo>() {
        @Override
        public int compare(PlayerVehicleInfo lhs, PlayerVehicleInfo rhs) {
            return rhs.getOverallStats().getBattles() - lhs.getOverallStats().getBattles();
        }
    };

    public Comparator<PlayerVehicleInfo> winComparator = new Comparator<PlayerVehicleInfo>() {
        @Override
        public int compare(PlayerVehicleInfo lhs, PlayerVehicleInfo rhs) {
            double lWinRate = ((double) lhs.getOverallStats().getWins() / lhs.getOverallStats().getBattles()) * 100;
            double rWinRate = ((double) rhs.getOverallStats().getWins() / rhs.getOverallStats().getBattles()) * 100;
            return (int) (rWinRate - lWinRate);
        }
    };

    public Comparator<PlayerVehicleInfo> masteryComparator = new Comparator<PlayerVehicleInfo>() {
        @Override
        public int compare(PlayerVehicleInfo lhs, PlayerVehicleInfo rhs) {
            return rhs.getMarkOfMastery() - lhs.getMarkOfMastery();
        }
    };

    public Comparator<PlayerVehicleInfo> wn8Comparator = new Comparator<PlayerVehicleInfo>() {
        @Override
        public int compare(PlayerVehicleInfo lhs, PlayerVehicleInfo rhs) {
            return (int) (rhs.getWN8() - lhs.getWN8());
        }
    };

    public Comparator<PlayerVehicleInfo> wn8ClanComparator = new Comparator<PlayerVehicleInfo>() {
        @Override
        public int compare(PlayerVehicleInfo lhs, PlayerVehicleInfo rhs) {
            return (int) (rhs.getClanWN8() - lhs.getClanWN8());
        }
    };

    public Comparator<PlayerVehicleInfo> avgExpComparator = new Comparator<PlayerVehicleInfo>() {
        @Override
        public int compare(PlayerVehicleInfo lhs, PlayerVehicleInfo rhs) {
            return rhs.getOverallStats().getAverageXp() - lhs.getOverallStats().getAverageXp();
        }
    };

    public Comparator<PlayerVehicleInfo> avgDamComparator = new Comparator<PlayerVehicleInfo>() {
        @Override
        public int compare(PlayerVehicleInfo lhs, PlayerVehicleInfo rhs) {
            float lhsBattles = lhs.getOverallStats().getBattles();
            float rhsBattles = rhs.getOverallStats().getBattles();
            double lhsDamageDlt = lhs.getOverallStats().getDamageDealt() / lhsBattles;
            double rhsDamageDlt = rhs.getOverallStats().getDamageDealt() / rhsBattles;
            if (rhsDamageDlt > lhsDamageDlt) {
                return 1;
            } else if (rhsDamageDlt < lhsDamageDlt) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    public Comparator<PlayerVehicleInfo> kdComparator = new Comparator<PlayerVehicleInfo>() {
        @Override
        public int compare(PlayerVehicleInfo lhs, PlayerVehicleInfo rhs) {
            float lhsBattles = lhs.getOverallStats().getBattles();
            float rhsBattles = rhs.getOverallStats().getBattles();
            double lhskd = lhs.getOverallStats().getFrags() / lhsBattles;
            double rhskd = rhs.getOverallStats().getFrags() / rhsBattles;
            if (rhskd > lhskd) {
                return 1;
            } else if (rhskd < lhskd) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    public void compareByTankName(Context ctx, List<PlayerVehicleInfo> vehicleInfoList) {
        final Tanks tanks = CAApp.getInfoManager().getTanks(ctx);
        Collections.sort(vehicleInfoList, new Comparator<PlayerVehicleInfo>() {
            @Override
            public int compare(PlayerVehicleInfo lhs, PlayerVehicleInfo rhs) {
                Tank lhsV = tanks.getTank(lhs.getTankId());
                Tank rhsV = tanks.getTank(rhs.getTankId());
                String lhsName = "", rhsName = "";
                if (lhsV != null)
                    lhsName = lhsV.getName();
                if (rhsV != null)
                    rhsName = rhsV.getName();
                return lhsName.compareToIgnoreCase(rhsName);
            }
        });
    }

    public void compareByTier(Context ctx, List<PlayerVehicleInfo> vehicleInfoList) {
        final Tanks tanks = CAApp.getInfoManager().getTanks(ctx);
        Collections.sort(vehicleInfoList, new Comparator<PlayerVehicleInfo>() {
            @Override
            public int compare(PlayerVehicleInfo lhs, PlayerVehicleInfo rhs) {
                Tank lhsV = tanks.getTank(lhs.getTankId());
                Tank rhsV = tanks.getTank(rhs.getTankId());
                int rhsT = 0;
                int lhsT = 0;

                if (lhsV != null)
                    lhsT = lhsV.getTier();
                if (rhsV != null)
                    rhsT = rhsV.getTier();

                if (rhsT > lhsT) {
                    return 1;
                } else if (rhsT < lhsT) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    public void compareByTierReverse(Context ctx, List<PlayerVehicleInfo> vehicleInfoList) {
        final Tanks tanks = CAApp.getInfoManager().getTanks(ctx);
        Collections.sort(vehicleInfoList, new Comparator<PlayerVehicleInfo>() {
            @Override
            public int compare(PlayerVehicleInfo lhs, PlayerVehicleInfo rhs) {
                Tank lhsV = tanks.getTank(lhs.getTankId());
                Tank rhsV = tanks.getTank(rhs.getTankId());
                int rhsT = 0;
                int lhsT = 0;

                if (lhsV != null)
                    lhsT = lhsV.getTier();
                if (rhsV != null)
                    rhsT = rhsV.getTier();

                if (rhsT < lhsT) {
                    return 1;
                } else if (rhsT > lhsT) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }
}
