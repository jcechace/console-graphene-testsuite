package org.jboss.as.console.testsuite.fragments;

import java.util.List;

/**
 * Created by mvelas on 25.3.2014.
 */
public class InfoTableFragment extends ResourceTableFragment {
    public String at(String key) {
        String[] info = root.getText().split("\n");
        for(int i = 0; i < info.length; i += 2) {
            if(info[i].contains(key)) {
                return info[i+1];
            }
        }
        return null;
    }
}
