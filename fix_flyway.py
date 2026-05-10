#!/usr/bin/env python3
"""
Flyway repair script - removes bad V2.0.14 migration entry from flyway_schema_history
so that V2.0.15 (CREATE TABLE Propietario) can run cleanly.
"""

import subprocess
import sys

DB_URL = "jdbc:postgresql://ep-raspy-sound-aip2i5um-pooler.c-4.us-east-1.aws.neon.tech/daat_db_dev?sslmode=require&channel_binding=require"
DB_USER = "neondb_owner"
DB_PASS = "npg_7TphxeoKQ4FZ"

def install_psycopg():
    print("[*] Instalando psycopg2-binary...")
    subprocess.check_call([sys.executable, "-m", "pip", "install", "psycopg2-binary", "-q"])

try:
    import psycopg2
except ImportError:
    install_psycopg()
    import psycopg2

def parse_jdbc_url(jdbc_url):
    parts = jdbc_url.replace("jdbc:postgresql://", "").split("?")[0]
    host_port_db = parts.split("/")
    host_port = host_port_db[0].split(":")
    return {
        "host": host_port[0],
        "port": int(host_port[1]) if len(host_port) > 1 else 5432,
        "dbname": host_port_db[1],
    }

def main():
    conn_info = parse_jdbc_url(DB_URL)
    print(f"[*] Conectando a {conn_info['host']}:{conn_info['port']}/{conn_info['dbname']} ...")

    conn = psycopg2.connect(
        host=conn_info["host"],
        port=conn_info["port"],
        dbname=conn_info["dbname"],
        user=DB_USER,
        password=DB_PASS,
        sslmode="require",
    )
    conn.autocommit = True
    cur = conn.cursor()

    # 1. Check current state
    cur.execute("SELECT version, description, checksum, installed_on, success FROM flyway_schema_history ORDER BY installed_rank")
    rows = cur.fetchall()
    print(f"\n[*] flyway_schema_history ({len(rows)} entries):")
    print(f"    {'Version':<12} {'Description':<40} {'Checksum':<15} {'Success':<8}")
    print(f"    {'-'*75}")
    for row in rows:
        print(f"    {str(row[0]):<12} {str(row[1]):<40} {str(row[2]):<15} {str(row[3])}  {'OK' if row[4] else 'FAIL'}")

    # 2. Check if Propietario table exists
    cur.execute("SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'Propietario')")
    exists = cur.fetchone()[0]
    print(f"\n[*] Tabla \"Propietario\" existe: {exists}")

    # 3. Delete entries that have no local migration file
    for v in ['2.0.14', '2.0.15']:
        cur.execute("SELECT version, description FROM flyway_schema_history WHERE version = %s", (v,))
        entries = cur.fetchall()
        if entries:
            print(f"\n[*] Eliminando V{v} de flyway_schema_history ({len(entries)} entrada(s)):")
            for e in entries:
                print(f"      {e[0]} - {e[1]}")
            cur.execute("DELETE FROM flyway_schema_history WHERE version = %s", (v,))
            print("    ✓ Eliminada(s)")
        else:
            print(f"\n[*] No hay entrada V{v} en flyway_schema_history")

    # 4. Verify
    cur.execute("SELECT version, description, installed_on FROM flyway_schema_history ORDER BY installed_rank")
    rows = cur.fetchall()
    print(f"\n[*] Migraciones restantes ({len(rows)}):")
    for row in rows:
        print(f"    {str(row[0]):<12} {str(row[1]):<50} {row[2]}")

    cur.close()
    conn.close()
    print("\n[*] Listo. Reinicia el backend para que Flyway ejecute V2.0.16")

if __name__ == "__main__":
    main()
