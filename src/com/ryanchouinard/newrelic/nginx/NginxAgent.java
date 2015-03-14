package com.ryanchouinard.newrelic.nginx;

import com.newrelic.metrics.publish.util.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.configuration.ConfigurationException;
import com.newrelic.metrics.publish.processors.EpochCounter;
import com.newrelic.metrics.publish.processors.Processor;

public class NginxAgent extends Agent {

    private static final String GUID = "com.ryanchouinard.newrelic.nginx";
    private static final String VERSION = "0.1.0-dev";

    private static final Logger logger = Logger.getLogger(NginxAgent.class);
    private static final Pattern pattern = Pattern.compile("Active connections: (\\d+).+ (\\d+) (\\d+) (\\d+).+Reading: (\\d+) Writing: (\\d+) Waiting: (\\d+)");

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
        Map<String, Long> stats = getStats();

        if (stats != null) {
            reportMetric("conn/accepted", "Connections/sec", connectionsAccepted.process(stats.get("accepts")));
            reportMetric("conn/dropped",  "Connections/sec", connectionsDropped.process(stats.get("accepts") - stats.get("handled")));
            reportMetric("conn/active",   "Connections",     stats.get("connections"));
            reportMetric("conn/idle",     "Connections",     stats.get("waiting"));
            reportMetric("reqs/total",    "Requests/sec",    requestsTotal.process(stats.get("requests")));
            reportMetric("reqs/current",  "Requests",        stats.get("reading") + stats.get("writing"));
        }
    }

    private Map<String, Long> getStats() {
        String statusText = getStatusResponse();

        if (statusText != null) {
            Matcher m = pattern.matcher(statusText);

            if (m.find()) {
                Map<String, Long> map = new HashMap<String, Long>();
                map.put("connections", Long.parseLong(m.group(1)));
                map.put("accepts",     Long.parseLong(m.group(2)));
                map.put("handled",     Long.parseLong(m.group(3)));
                map.put("requests",    Long.parseLong(m.group(4)));
                map.put("reading",     Long.parseLong(m.group(5)));
                map.put("writing",     Long.parseLong(m.group(6)));
                map.put("waiting",     Long.parseLong(m.group(7)));

                return map;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private String getStatusResponse() {
        String response = null;
        BufferedReader reader = null;
        InputStream inputStream = null;
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Accept", "text/plain");
            response = getStringFromInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }

    private static String getStringFromInputStream(InputStream inputStream) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();

        String line;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return builder.toString();
    }

}
