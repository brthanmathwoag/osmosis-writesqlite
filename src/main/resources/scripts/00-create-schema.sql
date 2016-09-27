DROP TABLE IF EXISTS actions;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS nodes;
DROP TABLE IF EXISTS node_tags;
DROP TABLE IF EXISTS ways;
DROP TABLE IF EXISTS way_nodes;
DROP TABLE IF EXISTS way_tags;
DROP TABLE IF EXISTS relations;
DROP TABLE IF EXISTS relation_members;
DROP TABLE IF EXISTS relation_tags;
DROP TABLE IF EXISTS schema_info;

DROP INDEX IF EXISTS idx_node_tags_node_id;
DROP INDEX IF EXISTS idx_way_tags_way_id;
DROP INDEX IF EXISTS idx_way_nodes_node_id;
DROP INDEX IF EXISTS idx_relation_tags_relation_id;


-- Create a table which will contain a single row defining the current schema version.
CREATE TABLE schema_info (
    version integer NOT NULL PRIMARY KEY
);


-- Create a table for users.
CREATE TABLE users (
    id int NOT NULL PRIMARY KEY,
    name text NOT NULL
);


-- Create a table for nodes.
CREATE TABLE nodes (
    id int NOT NULL PRIMARY KEY,
    version int NOT NULL,
    user_id int NOT NULL,
    tstamp int NOT NULL,
    changeset_id int NOT NULL
);


-- Create a table for node tags.
CREATE TABLE node_tags (
    node_id int NOT NULL,
    k text NOT NULL,
    v text NOT NULL
);


-- Create a table for ways.
CREATE TABLE ways (
    id int NOT NULL PRIMARY KEY,
    version int NOT NULL,
    user_id int NOT NULL,
    tstamp int NOT NULL,
    changeset_id int NOT NULL
);


-- Create a table for representing way to node relationships.
CREATE TABLE way_nodes (
    way_id int NOT NULL,
    node_id int NOT NULL,
    sequence_id int NOT NULL,
    PRIMARY KEY (way_id, sequence_id)
);


-- Create a table for way tags.
CREATE TABLE way_tags (
    way_id int NOT NULL,
    k text NOT NULL,
    v text
);


-- Create a table for relations.
CREATE TABLE relations (
    id int NOT NULL PRIMARY KEY,
    version int NOT NULL,
    user_id int NOT NULL,
    tstamp int NOT NULL,
    changeset_id int NOT NULL
);

-- Create a table for representing relation member relationships.
CREATE TABLE relation_members (
    relation_id int NOT NULL,
    member_id int NOT NULL,
    member_type text NOT NULL,
    member_role text NOT NULL,
    sequence_id int NOT NULL,
    PRIMARY KEY (relation_id, sequence_id)
);


-- Create a table for relation tags.
CREATE TABLE relation_tags (
    relation_id int NOT NULL,
    k text NOT NULL,
    v text NOT NULL
);


-- Configure the schema version.
INSERT INTO schema_info (version) VALUES (5);


SELECT AddGeometryColumn('nodes', 'geom', 4326, 'POINT', 'XY');

.exit
