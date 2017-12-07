import sqlite3
import shutil
import csv

print('Copying!')
shutil.copyfile('RawFacilities.db', 'Test.db')
print('Done!')

# Nullable value for a non-string encoded in a string
def nul(s):
    if(s == ""):
        return None
    return s

# Nullable value for a string (includes quotation marks)
def nulS(s):
    if(s == ""):
        return None
    return "'" + s + "'"

def nulI(s):
    if(s == ""):
        return None
    return int(s)

with sqlite3.connect('Test.db') as conn:
    cur = conn.cursor()
    with open('review.csv', newline='') as review_file:
        rows = csv.reader(review_file)
        is_first = True
        for r in rows:
            if is_first:
                is_first = False
                continue # skip first row since it is column names
            print(", ".join(r))
            cur.execute("insert into Review(facility_id, rating, text, role, longest_time_sober, cost_per_month, drug_dealers, employed_since, review_datetime, review_id) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",(
                int(r[0]), int(r[1]), nulS(r[2]), nulS(r[3]),
                nulS(r[4]), # longest time
                nulI(r[5]), # cost_per_mo
                nul(r[6]), # drug
                nul(r[7]), # employ
                nulS(r[8]), # datetime
                int(r[9]), # review_id
            ))
