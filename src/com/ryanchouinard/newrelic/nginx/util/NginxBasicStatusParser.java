package com.ryanchouinard.newrelic.nginx.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.net.HttpURLConnection;
import java.net.URL;

public class NginxBasicStatusParser
{

    private static final Pattern pattern = Pattern.compile("Active connections: (\\d+).+ (\\d+) (\\d+) (\\d+).+Reading: (\\d+) Writing: (\\d+) Waiting: (\\d+)", Pattern.DOTALL);

    public static Map<String, Long> parseUrl(URL statusUrl)
    {
        String statusText = null;
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) statusUrl.openConnection();
            connection.addRequestProperty("Accept", "text/plain");
            statusText = getStringFromInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return parseString(statusText);
    }

    public static Map<String, Long> parseString(String statusText)
    {
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
    }

    private static String getStringFromInputStream(InputStream inputStream) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();

        String line;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
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
