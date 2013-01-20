package me.itidez.plugins.icreative;

import me.itidez.plugins.utils.IPermission;

public enum Perms implements IPermission {
    CONFIG("config"),
    REGIONS("regions"),
    REGIONS_BYPASS("regions_bypass"),
    GM("switch_gamemode"),
    GM_BACKONLY("switch_gamemode.backonly"),
    GM_SURVIVAL("switch_gamemode.survival"),
    GM_CREATIVE("switch_gamemode.creative"),
    GM_ADVENTURE("switch_gamemode.adventure"),
    GM_OTHER("switch_gamemode.other"),
    KEEPINVENTORY("keepinventory");
    
    private static final String NS = "limitedcreative";
    
    private String perm;
    private Perms(String permission) {
        perm = permission;
    }
    @Override
    public String toString() {
        return NS + SEP + perm;
    }
    
    public enum NoLimit implements IPermission {
        DROP("drop"),
        PICKUP("pickup"),
        CHEST("chest"),
        SIGN("sign"),
        BUTTON("button"),
        LEVER("lever"),
        PVP("pvp"),
        MOB_DAMAGE("mob_damage"),
        USE("use"),
        BREAK("break");
        
        private static final String NS = "nolimit";
        
        private String perm;
        private NoLimit(String permission) {
            perm = permission;
        }
        @Override
        public String toString() {
            return Perms.NS + SEP + NoLimit.NS + SEP + perm;
        }
    }
    
    public static final class CmdBlock { // not the best way, but this matches to everything in this plugin ;)
        public static final String NS = "cmdblock";
        
        public static IPermission ALL = new IPermission() {
            public String toString() {
                return Perms.NS + SEP + CmdBlock.NS + SEP + "all";
            }
        };
    }
}
