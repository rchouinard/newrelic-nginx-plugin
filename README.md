# Nginx Plugin for New Relic

This project provides an NPI-compatible plugin for New Relic capable of
reporting Nginx statistics to the New Relic platform.


### Motivation

This project exists simply because the [official plugin from Nginx, Inc.](http://newrelic.com/plugins/nginx-inc/13)
does not support NPI and requires yet another runtime (Python) to be
installed and maintained on my production servers. I also thought this would be
a good chance to do something useful while learning more about Java.


## Requirements

- A New Relic account. Sign up for a free account [here](http://newrelic.com)
- Java Runtime (JRE) environment Version 1.6 or later
- A running instance of Nginx with the [HTTP stub status module](http://nginx.org/en/docs/http/ngx_http_stub_status_module.html) enabled
- Network access to New Relic (authenticated proxies are not currently supported)


## Installation

This plugin supports installation via the New Relic Platform Installer (NPI).
More information about the NPI tool and how to install it can be found in the
[New Relic docs](https://docs.newrelic.com/docs/plugins/plugins-new-relic/installing-plugins/installing-npi-compatible-plugin).

The plugin can then be installed using the npi tool:
`./npi install com.ryanchouinard.newrelic.nginx`


## Configuration

You will need an instance of Nginx running with a status URL accessible by the
host running the plugin. Consult the Nginx manual for details about the
[http_stub_status_module](http://nginx.org/en/docs/http/ngx_http_stub_status_module.html).

During installation, you will be prompted to configure the plugin. Choosing
to do so will open the configuration file in an editor. The configuration file
is a simple JSON document. The keys should be self-explanatory, but `name` can
be any string you wish to identify the host in the New Relic dashboards, and
`status_url` is the full URL to the Nginx stub status page.


## Collected Data

Overview

 - Connections
 - Request rate

![Overview screenshot](/screenshots/overview.png?raw=true)

Connections

 - Active
 - Idle
 - Accept rate
 - Drop rate

![Connections screenshot](/screenshots/connections.png?raw=true)

Requests

 - Active
 - Rate

![Requests screenshot](/screenshots/requests.png?raw=true)

## License

The MIT License (MIT). Please see [License File](LICENSE.md) for more information.
