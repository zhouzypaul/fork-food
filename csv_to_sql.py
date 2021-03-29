import csv_to_sqlite

options = csv_to_sqlite.CsvOptions(typing_style="full")
input_files= ["data/yelp_academic_dataset_business.csv"]
csv_to_sqlite.write_csv(input_files, "data/restaurants.sqlite", options)