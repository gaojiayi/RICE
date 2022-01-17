package com.gaojy.rice.controller.longpolling;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaojy
 * @ClassName ManyTaskPullRequest.java
 * @Description TODO
 * @createTime 2022/01/17 14:53:00
 */
public class ManyTaskPullRequest {
    private final ArrayList<PullRequest> pullRequestList = new ArrayList<>();

    public synchronized void addPullRequest(final PullRequest pullRequest) {
        this.pullRequestList.add(pullRequest);
    }

    public synchronized void addPullRequest(final List<PullRequest> many) {
        this.pullRequestList.addAll(many);
    }

    public synchronized List<PullRequest> cloneListAndClear() {
        if (!this.pullRequestList.isEmpty()) {
            List<PullRequest> result = (ArrayList<PullRequest>) this.pullRequestList.clone();
            this.pullRequestList.clear();
            return result;
        }

        return null;
    }
}
