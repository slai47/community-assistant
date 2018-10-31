package com.half.wowsca.model;

import com.half.wowsca.model.encyclopedia.holders.ShipsHolder;
import com.half.wowsca.model.encyclopedia.items.ShipInfo;

import java.util.Comparator;

/**
 * Created by slai4 on 9/26/2015.
 */
public class ShipCompare {

    private ShipsHolder shipsHolder;

    public Comparator<Ship> battlesComparator = new Comparator<Ship>() {
        @Override
        public int compare(Ship lhs, Ship rhs) {
            return rhs.getBattles() - lhs.getBattles();
        }
    };

    public Comparator<Ship> averageExpComparator = new Comparator<Ship>() {
        @Override
        public int compare(Ship lhs, Ship rhs) {
            int rhsBattles = rhs.getBattles();
            int lhsBattles = lhs.getBattles();
            int rhsAverage = (int) (rhs.getTotalXP() / rhsBattles);
            int lhsAverage = (int) (lhs.getTotalXP() / lhsBattles);
            return rhsAverage - lhsAverage;
        }
    };
    public Comparator<Ship> averageDamageComparator = new Comparator<Ship>() {
        @Override
        public int compare(Ship lhs, Ship rhs) {
            int rhsBattles = rhs.getBattles();
            int lhsBattles = lhs.getBattles();
            int rhsAverage = (int) (rhs.getTotalDamage() / rhsBattles);
            int lhsAverage = (int) (lhs.getTotalDamage() / lhsBattles);
            return rhsAverage - lhsAverage;
        }
    };

    public Comparator<Ship> winRateComparator = new Comparator<Ship>() {
        @Override
        public int compare(Ship lhs, Ship rhs) {
            float rhsBattles = rhs.getBattles();
            float lhsBattles = lhs.getBattles();
            int rhsAverage = (int) ((rhs.getWins() / rhsBattles) * 100);
            int lhsAverage = (int) ((lhs.getWins() / lhsBattles) * 100);
            return rhsAverage - lhsAverage;
        }
    };

    public Comparator<Ship> killsComparator = new Comparator<Ship>() {
        @Override
        public int compare(Ship lhs, Ship rhs) {
            return rhs.getFrags() - lhs.getFrags();
        }
    };

    public Comparator<Ship> killsDeathComparator = new Comparator<Ship>() {
        @Override
        public int compare(Ship lhs, Ship rhs) {
            float rhsBattles = rhs.getBattles() - (float) rhs.getSurvivedBattles();
            if(rhsBattles <= 1)
                rhsBattles = 1f;
            float lhsBattles = lhs.getBattles() - (float) lhs.getSurvivedBattles();
            if(lhsBattles <= 1)
                lhsBattles = 1f;
            float rhsFrags = (float) rhs.getFrags() / rhsBattles;
            float lhsFrags = (float) lhs.getFrags() / lhsBattles;
            if(rhsFrags > lhsFrags){
                return 1;
            } else if( rhsFrags < lhsFrags) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    public Comparator<Ship> accuracyComparator = new Comparator<Ship>() {
        @Override
        public int compare(Ship lhs, Ship rhs) {
            float rhsShots = rhs.getMainBattery().getShots();
            float lhsShots = lhs.getMainBattery().getShots();
            float rhsAcc = 0;
            if (rhsShots > 0) {
                rhsAcc = (float) rhs.getMainBattery().getHits() / rhsShots;
            }
            float lhsAcc = 0;
            if (lhsShots > 0) {
                lhsAcc = (float) lhs.getMainBattery().getHits() / lhsShots;
            }
            if(rhsAcc > lhsAcc){
                return 1;
            } else if( rhsAcc < lhsAcc) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    public Comparator<Ship> accuractTorpsComparator = new Comparator<Ship>() {
        @Override
        public int compare(Ship lhs, Ship rhs) {
            float rhsShots = rhs.getTorpedoes().getShots();
            float lhsShots = lhs.getTorpedoes().getShots();
            float rhsAcc = -1;
            if (rhsShots > 0) {
                rhsAcc = (float) rhs.getTorpedoes().getHits() / rhsShots;
            }
            float lhsAcc = -1;
            if (lhsShots > 0) {
                lhsAcc = (float) lhs.getTorpedoes().getHits() / lhsShots;
            }
            if(rhsAcc > lhsAcc){
                return 1;
            } else if( rhsAcc < lhsAcc) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    public Comparator<Ship> planeKillsComparator = new Comparator<Ship>() {
        @Override
        public int compare(Ship lhs, Ship rhs) {
            return rhs.getPlanesKilled() - lhs.getPlanesKilled();
        }
    };

    public Comparator<Ship> damageComparator = new Comparator<Ship>() {
        @Override
        public int compare(Ship lhs, Ship rhs) {
            return (int) (rhs.getTotalDamage() - lhs.getTotalDamage());
        }
    };

    public Comparator<Ship> namesComparator = new Comparator<Ship>() {
        @Override
        public int compare(Ship lhs, Ship rhs) {
            ShipInfo rhsInfo = shipsHolder.get(rhs.getShipId());
            ShipInfo lhsInfo = shipsHolder.get(lhs.getShipId());
            String rhsName = "", lhsName = "";
            if (rhsInfo != null)
                rhsName = rhsInfo.getName();
            if (lhsInfo != null)
                lhsName = lhsInfo.getName();
            return lhsName.compareToIgnoreCase(rhsName);
        }
    };

    public Comparator<Ship> CARatingComparator = new Comparator<Ship>() {
        @Override
        public int compare(Ship lhs, Ship rhs) {
            return (int) (rhs.getCARating() - lhs.getCARating());
        }
    };

    public Comparator<Ship> tierDescendingComparator = new Comparator<Ship>() {
        @Override
        public int compare(Ship lhs, Ship rhs) {
            ShipInfo rhsInfo = shipsHolder.get(rhs.getShipId());
            ShipInfo lhsInfo = shipsHolder.get(lhs.getShipId());
            int rhsT = 0;
            int lhsT = 0;
            if (rhsInfo != null)
                rhsT = rhsInfo.getTier();
            if (lhsInfo != null)
                lhsT = lhsInfo.getTier();
            if (rhsT > lhsT) {
                return 1;
            } else if (rhsT < lhsT) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    public Comparator<Ship> tierAscendingComparator = new Comparator<Ship>() {
        @Override
        public int compare(Ship lhs, Ship rhs) {
            ShipInfo rhsInfo = shipsHolder.get(rhs.getShipId());
            ShipInfo lhsInfo = shipsHolder.get(lhs.getShipId());
            int rhsT = 0;
            int lhsT = 0;
            if (rhsInfo != null)
                rhsT = rhsInfo.getTier();
            if (lhsInfo != null)
                lhsT = lhsInfo.getTier();
            if (rhsT < lhsT) {
                return 1;
            } else if (rhsT > lhsT) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    public ShipsHolder getShipsHolder() {
        return shipsHolder;
    }

    public void setShipsHolder(ShipsHolder shipsHolder) {
        this.shipsHolder = shipsHolder;
    }
}
