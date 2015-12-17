#!/bin/bash
psql -U postgres -c "DROP DATABASE amosaatest;"
psql -U postgres -c "CREATE DATABASE amosaatest;"
psql -U postgres -c "CREATE USER amosaatest WITH PASSWORD 'amosaatest';"
psql -U postgres -c "GRANT ALL ON DATABASE amosaatest TO amosaatest;"
