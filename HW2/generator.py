from dotenv import load_dotenv
import argparse
import random
import os
import psycopg2

parser = argparse.ArgumentParser(description='Generator of clicks on display')
parser.add_argument('-c', type=int, default=10, help='amount of arrays')
parser.add_argument('-max', type=int, default=100, help='max of value generation')
parser.add_argument('-len', type=int, default=10, help='size of arrays')
parser.add_argument('-db', type=str, default='db2', help='name of database')
parser.add_argument('-debug', action='store_true', help='use debug mode')
parser.set_defaults(debug=False)


def generate(max, len):
    array = [str(random.randint(0, max)) for _ in range(len)]
    return ",".join(array)


def load(count, max, len, database):
    hostname = os.getenv("HOST_PSQL")
    login = os.getenv("LOGIN_PSQL")
    password = os.getenv("PASSWORD_PSQL")

    try:
        conn = psycopg2.connect(dbname=database, user=login,
                                password=password, host=hostname)
        cursor = conn.cursor()
    except:
        print("Failed to connect to database")
        exit(1)

    with open('dump.sql', 'w') as fd:
        for _ in range(count):
            sqltext = 'INSERT INTO "inputs" (val) VALUES ( {'+generate(max, len)+'} );'
            print(sqltext, file=fd)
            insert = psycopg2.sql.SQL(sqltext)
            cursor.execute(insert)

    cursor.execute('SELECT * FROM "inputs"')
    records = cursor.fetchall()
    print('Total items in table: %d' % (len(records)))
    conn.commit()
    cursor.close()
    conn.close()


if __name__ == "__main__":
    args = parser.parse_args()
    if args.debug:
        load_dotenv()
    load(args.c, args.max, args.len, args.db)