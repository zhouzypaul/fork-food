import csv_to_sqlite

def csvToSqlite(input_path, output_path):
    options = csv_to_sqlite.CsvOptions(typing_style="full")
    input_files= [input_path]
    csv_to_sqlite.write_csv(input_files, output_path, options)

if __name__ == '__main__':
    csvToSqlite(
        "data/yelp_academic_dataset_business.csv",
        "data/restaurants.sqlite"
    )