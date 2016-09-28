# osmosis-writesqlite

This is an [Osmosis](http://wiki.openstreetmap.org/wiki/Osmosis) plugin for storing [OpenStreetMap](https://wiki.openstreetmap.org/wiki/Main_Page) data in an [Sqlite](https://www.sqlite.org/) database.

## Installation

```
git clone https://github.com/brthanmathwoag/osmosis-writesqlite.git
cd osmosis-writesqlite
mvn package
mkdir -p ~/.openstreetmap/osmosis/plugins/
cp target/osmosis-writesqlite.jar ~/.openstreetmap/osmosis/plugins/
```

## Usage

You can reference the plugin in your workflow with the `write-sqlite` alias.

You can pass `recreateSchema=true` parameter if you want to truncate an existing database.

```
osmosis --read-pbf germany-latest.osm.pbf \
    --write-sqlite output.db recreateSchema=true
```

## Schema

[The schema](https://github.com/brthanmathwoag/osmosis-writesqlite/blob/master/src/main/resources/scripts/00-create-schema.sql) closely resembles the [Osmosis' pgsimple PostgreSQL schema](https://github.com/openstreetmap/osmosis/blob/master/package/script/pgsimple_schema_0.6.sql). Timestamps are stored as unixtimestamps in int columns; Node geometries are stored as spatialite geometries with WGS84 srid.

## Acknowledgements

This plugin uses [Taro L. Saito's](http://xerial.org) [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc) as the database driver.


