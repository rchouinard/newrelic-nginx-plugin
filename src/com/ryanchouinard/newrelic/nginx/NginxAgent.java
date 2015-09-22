package com.ryanchouinard.newrelic.nginx;

import com.ryanchouinard.newrelic.nginx.util.NginxBasicStatusParser;

import java.util.Map;
import java.net.MalformedURLException;
import java.net.URL;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.configuration.ConfigurationException;
import com.newrelic.metrics.publish.processors.EpochCounter;
import com.newrelic.metrics.publish.processors.Processor;

public class NginxAgent extends Agent {

    private static final String GUID = "com.ryanchouinard.newrelic.nginx";
    private static final String VERSION = "1.0.0";

    private Processor connectionsAccepted;
    private Processor connectionsDropped;
    private Processor requestsTotal;

    private String name;
    private URL url;

    public NginxAgent(String name, String statusUrl) throws ConfigurationException {
        super(GUID, VERSION);
        try {
            this.name = name;
            this.url = new URL(statusUrl);
            this.connectionsAccepted = new EpochCounter();
            this.connectionsDropped  = new EpochCounter();
            this.requestsTotal       = new EpochCounter();
        } catch (MalformedURLException e) {
            throw new ConfigurationException("Status URL could not be parsed", e);
        }
    }

    @Override
    public String getAgentName() {
        return name;
    }

    @Override
    public void pollCycle() {
        Map<String, Long> stats = NginxBasicStatusParser.parseUrl(this.url);

        if (stats != null) {
            reportMetric("conn/accepted", "Connections/sec", connectionsAccepted.process(stats.get("accepts")));
            reportMetric("conn/dropped",  "Connections/sec", connectionsDropped.process(stats.get("accepts") - stats.get("handled")));
            reportMetric("conn/active",   "Connections",     stats.get("connections"));
            reportMetric("conn/idle",     "Connections",     stats.get("waiting"));
            reportMetric("reqs/total",    "Requests/sec",    requestsTotal.process(stats.get("requests")));
            reportMetric("reqs/current",  "Requests",        stats.get("reading") + stats.get("writing"));
        }
    }

}
