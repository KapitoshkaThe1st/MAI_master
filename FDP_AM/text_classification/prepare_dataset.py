import csv
import re
import random

def preprocess_text(text):
    text = re.sub(r"http\S+", "", text)
    text = re.sub(r"RT @", '@', text)
    text = re.sub(r"@\S+", "", text)
    text = re.sub(r'[^а-яА-Яa-zA-Z\s]', ' ', text)
    text = re.sub(r'\s', ' ', text)
    text = re.sub(' +', ' ', text)

    return text.strip()

files = ['positive.csv', 'negative.csv']

data = []
for file in files:
    with open(file, newline='') as csvfile:
        reader = csv.reader(csvfile, delimiter=';')
        for row in reader:
            data.append(row)

map_class = {
    '1': '1',
    '-1': '0'
}

data = [[preprocess_text(d[3]), map_class[d[4]]] for d in data]
random.shuffle(data)

with open('data.csv', 'w+') as csvfile:
    writer = csv.writer(csvfile, delimiter=',')
    for row in data:
        writer.writerow(row)