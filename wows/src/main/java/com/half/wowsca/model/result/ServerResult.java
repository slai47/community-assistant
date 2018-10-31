package com.half.wowsca.model.result;

import com.half.wowsca.model.ServerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by slai4 on 12/2/2015.
 */
public class ServerResult {

    private List<ServerInfo> wotNumbers;
    private List<ServerInfo> wowsNumbers;

    public ServerResult() {
        wotNumbers = new ArrayList<>();
        wowsNumbers = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ServerResult{" +
                "wotNumbers=" + wotNumbers +
                ", wowsNumbers=" + wowsNumbers +
                '}';
    }

    public List<ServerInfo> getWotNumbers() {
        return wotNumbers;
    }

    public void setWotNumbers(List<ServerInfo> wotNumbers) {
        this.wotNumbers = wotNumbers;
    }

    public List<ServerInfo> getWowsNumbers() {
        return wowsNumbers;
    }

    public void setWowsNumbers(List<ServerInfo> wowsNumbers) {
        this.wowsNumbers = wowsNumbers;
    }
}
