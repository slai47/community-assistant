package com.half.wowsca.model.saveobjects;

import com.half.wowsca.model.CaptainDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by slai4 on 9/22/2015.
 */
public class SavedDetails {

    private List<CaptainDetails> details;

    public SavedDetails() {
        details = new ArrayList<CaptainDetails>();
    }

    public List<CaptainDetails> getDetails() {
        return details;
    }

    public void setDetails(List<CaptainDetails> details) {
        this.details = details;
    }
}
