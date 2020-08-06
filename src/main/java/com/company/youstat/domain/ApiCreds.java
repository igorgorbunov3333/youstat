package com.company.youstat.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiCreds {
    private Installed installed;

    public Installed getInstalled() {
        return installed;
    }

    public void setInstalled(Installed installed) {
        this.installed = installed;
    }

    public class Installed {
        @JsonProperty("client_id")
        private String clientId;
        @JsonProperty("client_secret")
        private String clientSecret;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }
    }
}

