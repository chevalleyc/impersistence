# Database Setup and Migrations

## Objectives

This module provides:

- creation of a new DB to host quads
- creation of the required tables, triggers etc.
- compile the DB schema with jOOQ to expose the POJO API

## Pre-requisite

- Java version 17+ (latest 18.0.2)
- Apache Maven 3.8.6 (important to support jOOQ 3.17)
- Postgresql 12+ 

## Building It

### Database Creation

`sudo -u postgres psql < createdb.sql`

### Generating POJOs

`mvn clean install`