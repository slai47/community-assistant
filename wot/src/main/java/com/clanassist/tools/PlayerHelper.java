package com.clanassist.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Harrison on 5/21/2014.
 */
public class PlayerHelper {

    private static final int MASTERY_TOTAL = 4;

//    "commander": "Commander",

//    "executive_officer": "Executive Officer",
//    "combat_officer": "Combat Officer",

//    "recruitment_officer": "Recruitment Officer"
//    "junior_officer": "Junior Officer",
//    "personnel_officer": "Personnel Officer",
//    "quartermaster": "Quartermaster",
//    "intelligence_officer": "Intelligence Officer",

//    "private": "Private",
//    "recruit": "Recruit",
//    "reservist": "Reservist",

    private static final String COMMANDER = "commander";

    private static final String EXECUTIVE_OFFICER = "executive_officer";
    private static final String COMBAT_OFFICER = "combat_officer";

    private static final String JUNIOR = "junior_officer";
    private static final String PERSONNEL = "personnel_officer";
    private static final String QUARTERMASTER = "quartermaster";
    private static final String RECRUITER = "recruitment_officer";
    private static final String INTELLIGENCE_OFFICER = "intelligence_officer";

    private static final String SOLDIER = "private";
    private static final String RECRUIT = "recruit";
    private static final String RESERVIST = "reservist";

    private static Map<String, Integer> ranks;

    public static Map<String, Integer> getRanks() {
        if (ranks == null) {
            ranks = new HashMap<String, Integer>();
            ranks.put(COMMANDER, 11);

            ranks.put(EXECUTIVE_OFFICER, 10);
            ranks.put(COMBAT_OFFICER, 9);

            ranks.put(JUNIOR, 8);
            ranks.put(QUARTERMASTER, 7);
            ranks.put(INTELLIGENCE_OFFICER, 6);
            ranks.put(RECRUITER, 5);
            ranks.put(PERSONNEL, 4);

            ranks.put(SOLDIER, 3);
            ranks.put(RECRUIT, 2);
            ranks.put(RESERVIST, 1);
        }
        return ranks;
    }

    public static String getMasteryBadgeLevel(int mastery) {
        String mas = "";
        if (mastery == MASTERY_TOTAL) {
            mas = "M";
        } else if (mastery == 1) {
            mastery = 3;
        } else if (mastery == 3) {
            mastery = 1;
        }
        if (mastery < MASTERY_TOTAL)
            mas = mastery + "";
        return mas;
    }
}
