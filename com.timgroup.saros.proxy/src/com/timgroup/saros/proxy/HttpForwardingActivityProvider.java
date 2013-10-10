package com.timgroup.saros.proxy;

import com.timgroup.saros4intellij.proxy.Position;
import com.timgroup.saros4intellij.proxy.client.HttpClientNavigator;

import de.fu_berlin.inf.dpp.activities.business.EditorActivity;
import de.fu_berlin.inf.dpp.activities.business.EditorActivity.Type;
import de.fu_berlin.inf.dpp.activities.business.IActivity;
import de.fu_berlin.inf.dpp.project.AbstractActivityProvider;

public class HttpForwardingActivityProvider extends AbstractActivityProvider {

    private final HttpClientNavigator httpClientNavigator;

    public HttpForwardingActivityProvider(String host, int port) {
        httpClientNavigator = new HttpClientNavigator(host, port);
    }
    
    @Override
    public void exec(IActivity activity) {
        if (activity instanceof EditorActivity) {
            EditorActivity edit = (EditorActivity) activity;
            if (edit.getType() == Type.ACTIVATED) {
                String filename = "/" + edit.getPath().getProjectRelativePath().toPortableString();
                httpClientNavigator.goTo(filename, new Position(0));
            }
        }

    }
}
