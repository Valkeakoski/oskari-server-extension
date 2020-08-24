# Oskari backend Valkeakoski

Site running: http://kartat.vlk.fi

This repository contains server extensions for Valkeakoski.

Support: [Sitowise Oy](https://sitowise.com) / [support@sitowise.com](mailto:support@sitowise.com)

## Installation

### Development

* run `mvn clean install`
* copy `oskari-map.war` to `jetty/webapps`
* start jetty

### Valkeakoski site

Run following code in `/home/oskari` -folder:
```
sudo sh install.sh
```

Now all are installed and you can test it when jetty starts (if not then try to start again).

## Flyway modules

| Module        | Description                              |
|---------------|------------------------------------------|
| `valkeakoski` | common valkeakoski module, use it always |
