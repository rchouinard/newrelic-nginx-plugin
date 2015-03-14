package com.ryanchouinard.newrelic.nginx;

import java.util.Map;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.AgentFactory;
import com.newrelic.metrics.publish.configuration.ConfigurationException;

public class NginxAgentFactory extends AgentFactory {

    @Override
    public Agent createConfiguredAgent(Map<String, Object> properties) throws ConfigurationException {
        String name = (String) properties.get("name");
        String statusUrl = (String) properties.get("status_url");

        if (name == null || statusUrl == null) {
            throw new ConfigurationException("'name' and 'status_url' cannot be null. Do you have a 'config/plugin.json' file?");
        }

        return new NginxAgent(name, statusUrl);
    }
}
