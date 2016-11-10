# osmosis-writesqlite

This is an [Osmosis](http://wiki.openstreetmap.org/wiki/Osmosis) plugin for storing [OpenStreetMap](https://wiki.openstreetmap.org/wiki/Main_Page) data in an [Sqlite](https://www.sqlite.org/) database.

Make note that if you simply want to load an xml or pbf dump without any filtering/transformation it would probably be faster and less resource-heavy if you used [spatialite_osm_raw](https://www.gaia-gis.it/fossil/spatialite-tools/wiki?name=spatialite_osm_raw) or [spatialite_osm_map](https://www.gaia-gis.it/fossil/spatialite-tools/wiki?name=spatialite_osm_map) from the `spatialite-tools` package instead.

## Prerequisites

The plugin requires spatialite installed as a dynamically-loaded plugin which may be packaged separately from a statically-linked cli executable. Look for `mod_spatialite.so` file in your distro's package documentation. On Ubuntu you will need `libsqlite3-mod-spatialite`. On Arch it is `libspatialite`.

## Installation

You can grab the latest version from [here](https://drive.google.com/file/d/0B_sU33gr527ZRXh2dXNQajRLX2c/view?usp=sharing) (SHA1: fadd6585bc02837af479014fcf084d31900afab4). Put it in ~/.openstreetmap/osmosis/plugins/ and you are ready to go.

To build it yourself, run:

```
git clone https://github.com/brthanmathwoag/osmosis-writesqlite.git
cd osmosis-writesqlite
mvn install
```

## Usage

You can reference the plugin in your workflow with the `write-sqlite` alias.

```
osmosis --read-pbf germany-latest.osm.pbf \
    --write-sqlite output.db recreateSchema=true
```

| Option          | Description                                                                                                                                | Valid values                           | Default value |
| --------------- | ------------------------------------------------------------------------------------------------------------------------------------------ | -------------------------------------- | ------------- |
| recreateSchema  | Whether you want to truncate an existing database.                                                                                         | yes, no, true, false                   | false         |
| batchSize       | Number of rows inserted in a single transaction. Set to -1 to commit after all rows are inserted. Set to 0 to not use transactions at all. | -1, 0, 1, ...                          | -1            |
| journalMode     | [SQLite journal mode](https://sqlite.org/pragma.html#pragma_journal_mode)                                                                  | OFF, DELETE, MEMORY, PERSIST, TRUNCATE | DELETE        |
| synchronousMode | [SQLite synchronous mode](https://sqlite.org/pragma.html#pragma_synchronous)                                                               | OFF, NORMAL, FULL                      | FULL          |
| cacheSize       | [SQLite cache size](https://sqlite.org/pragma.html#pragma_cache_size) (in pages). Set 0  to use SQLite default.                            | 0, 1, ...                              | 0             |

## Schema

[The schema](https://github.com/brthanmathwoag/osmosis-writesqlite/blob/master/src/main/resources/scripts/00-create-schema.sql) closely resembles the [Osmosis' pgsimple PostgreSQL schema](https://github.com/openstreetmap/osmosis/blob/master/package/script/pgsimple_schema_0.6.sql). Timestamps are stored as unixtimestamps in int columns; Node geometries are stored as spatialite geometries with WGS84 srid.

## Acknowledgements

This plugin uses [Taro L. Saito's](http://xerial.org) [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc) as the database driver.


