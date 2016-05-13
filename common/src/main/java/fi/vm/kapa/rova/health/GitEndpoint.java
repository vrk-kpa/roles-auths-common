/**
 * The MIT License
 * Copyright (c) 2016 Population Register Centre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fi.vm.kapa.rova.health;

import java.io.IOException;
import java.util.Properties;

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.core.env.Environment;

import fi.vm.kapa.rova.logging.Logger;

public class GitEndpoint extends AbstractEndpoint<GitEndpoint.GitRepositoryState> {

    private static Logger LOG = Logger.getLogger(GitEndpoint.class);

    private GitRepositoryState gitRepositoryState;

    public GitEndpoint() {
        super("git");
    }

    @Override
    public boolean isEnabled() {
        Environment env = getEnvironment();
        Boolean enabled = env.getProperty("endpoints.git.enabled", Boolean.class);
        if (enabled == null) {
            return super.isEnabled();
        }
        return enabled;
    }

    @Override
    public boolean isSensitive() {
        Environment env = getEnvironment();
        Boolean sensitive = env.getProperty("endpoints.git.sensitive", Boolean.class);
        if (sensitive == null) {
            return super.isSensitive();
        }
        return sensitive;
    }

    @Override
    public GitRepositoryState invoke() {
        if (gitRepositoryState == null) {
            Properties properties = new Properties();
            try {
                properties.load(getClass().getClassLoader().getResourceAsStream("git.properties"));
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }

            gitRepositoryState = new GitRepositoryState(properties);
        }
        return gitRepositoryState;
    }

    public class GitRepositoryState {

        private final String tags;
        private final String branch;
        private final String dirty;
        private final String remoteOriginUrl;
        private final String commitId;
        private final String commitIdAbbrev;
        private final String describe;
        private final String describeShort;
        private final String commitUserName;
        private final String commitUserEmail;
        private final String commitMessageFull;
        private final String commitMessageShort;
        private final String commitTime;
        private final String closestTagName;
        private final String closestTagCommitCount;
        private final String buildUserName;
        private final String buildUserEmail;
        private final String buildTime;
        private final String buildHost;
        private final String buildVersion;

        public GitRepositoryState(Properties properties) {
            this.tags = String.valueOf(properties.get("git.tags"));
            this.branch = String.valueOf(properties.get("git.branch"));
            this.dirty = String.valueOf(properties.get("git.dirty"));
            this.remoteOriginUrl = String.valueOf(properties.get("git.remote.origin.url"));

            this.commitId = String.valueOf(properties.get("git.commit.id"));
            this.commitIdAbbrev = String.valueOf(properties.get("git.commit.id.abbrev"));
            this.describe = String.valueOf(properties.get("git.commit.id.describe"));
            this.describeShort = String.valueOf(properties.get("git.commit.id.describe-short"));
            this.commitUserName = String.valueOf(properties.get("git.commit.user.name"));
            this.commitUserEmail = String.valueOf(properties.get("git.commit.user.email"));
            this.commitMessageFull = String.valueOf(properties.get("git.commit.message.full"));
            this.commitMessageShort = String.valueOf(properties.get("git.commit.message.short"));
            this.commitTime = String.valueOf(properties.get("git.commit.time"));
            this.closestTagName = String.valueOf(properties.get("git.closest.tag.name"));
            this.closestTagCommitCount = String.valueOf(properties.get("git.closest.tag.commit.count"));

            this.buildUserName = String.valueOf(properties.get("git.build.user.name"));
            this.buildUserEmail = String.valueOf(properties.get("git.build.user.email"));
            this.buildTime = String.valueOf(properties.get("git.build.time"));
            this.buildHost = String.valueOf(properties.get("git.build.host"));
            this.buildVersion = String.valueOf(properties.get("git.build.version"));
        }

        public String getTags() {
            return tags;
        }

        public String getBranch() {
            return branch;
        }

        public String getDirty() {
            return dirty;
        }

        public String getRemoteOriginUrl() {
            return remoteOriginUrl;
        }

        public String getCommitId() {
            return commitId;
        }

        public String getCommitIdAbbrev() {
            return commitIdAbbrev;
        }

        public String getDescribe() {
            return describe;
        }

        public String getDescribeShort() {
            return describeShort;
        }

        public String getCommitUserName() {
            return commitUserName;
        }

        public String getCommitUserEmail() {
            return commitUserEmail;
        }

        public String getCommitMessageFull() {
            return commitMessageFull;
        }

        public String getCommitMessageShort() {
            return commitMessageShort;
        }

        public String getCommitTime() {
            return commitTime;
        }

        public String getClosestTagName() {
            return closestTagName;
        }

        public String getClosestTagCommitCount() {
            return closestTagCommitCount;
        }

        public String getBuildUserName() {
            return buildUserName;
        }

        public String getBuildUserEmail() {
            return buildUserEmail;
        }

        public String getBuildTime() {
            return buildTime;
        }

        public String getBuildHost() {
            return buildHost;
        }

        public String getBuildVersion() {
            return buildVersion;
        }
    }
}
