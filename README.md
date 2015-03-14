# Nginx Plugin for New Relic

This project provides an NPI-compatible plugin for New Relic capable of
reporting Nginx statistics to the New Relic platform.

**PRE-RELEASE WARNING:** This plugin is currently in a workable state, however
I have not yet published the plugin on New Relic's Plugin Central, nor have I
published the associated New Relic dashboard. Everything is working great on my
servers under my New Relic account, but I can't promise it will work for you!

### Motivation

This project exists simply because the [official plugin from Nginx, Inc.](http://newrelic.com/plugins/nginx-inc/13)
does not support NPI and requires yet another runtime (Python) to be
installed and maintained on my production servers. I also thought this would be
a good chance to do something useful while learning more about Java.

----

## Requirements

- A New Relic account. Sign up for a free account [here](http://newrelic.com)
- Java Runtime (JRE) environment Version 1.6 or later
- A running instance of Nginx with the [HTTP stub status module](http://nginx.org/en/docs/http/ngx_http_stub_status_module.html) enabled
- Network access to New Relic (authenticated proxies are not currently supported)

----

## Installation

This section will be updated once the plugin is published to New Relic.
