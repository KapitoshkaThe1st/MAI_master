import csv
import re
import sys

from naive_bayes_classifier import NaiveBayesClassifier
from metrics import Metrics
import utils

def usage():
    usage = f"""usage: {sys.argv[0]} [learn | classify] parameters...
learn -- Command to collect statistics
    --data_file <> -- path to file with texts to be classified
    --stats_file <> -- path to computed statistics during learning stage
    --result_file <> -- path to file where classification result will be stored
classify -- Command to classify texts
    --data_file <> -- path to file with texts to be classified
    --stats_file <> -- path to computed statistics during learning stage
    --result_file <> -- path to file where classification result will be stored
"""
    print(usage, end='')
    exit(0)

def parse_args(args):
    flags = list(filter(lambda arg: arg.startswith('-'), args))

    parameters = {}
    for f in flags:
        idx = args.index(f) + 1
        if idx < len(args):
            value = args[idx]
            if not value.startswith('-'):
                parameters[f] = value

    flags = set(filter(lambda f: f not in parameters, flags))
    return flags, parameters

def preprocess(data):
    return list(map(lambda text: list(filter(lambda word: len(word) > 2, re.sub(r'[^\w\s]','', text.lower()).split(' '))), data))

def learn(data_file, stats_file):

    x = []
    y = []

    try:
        with open(data_file, newline='') as csvfile:
            reader = csv.reader(csvfile, delimiter=',')
            for row in reader:
                x.append(row[0])
                y.append(row[1])
    except Exception as e:
        print(f'An error while reading {data_file} occured:\n' + str(e))

    x = preprocess(x[1:])
    y = y[1:]

    x_train, y_train, x_test, y_test = utils.split_data(x, y, test_portion=0.1)

    classifier = NaiveBayesClassifier()

    classifier.fit(x_train, y_train)
    classifier.save_stats(stats_file=stats_file)

    y_predicted = classifier.predict(x_train)

    train_metrics = Metrics(y_train, y_predicted)
    print('Metrics on train data:')

    print(f'\naccuracy: {train_metrics.accuracy()}')

    for cls in train_metrics.classes:
        print(f'\nclass: {decode[cls]}')
        print(f'\nprecision: {train_metrics.precsion(cls)}, recall: {train_metrics.recall(cls)}')

    y_predicted = classifier.predict(x_test)

    train_metrics = Metrics(y_test, y_predicted)
    print('Metrics on test data:')

    print(f'\naccuracy: {train_metrics.accuracy()}')

    for cls in train_metrics.classes:
        print(f'\nclass: {decode[cls]}')
        print(f'\nprecision: {train_metrics.precsion(cls)}, recall: {train_metrics.recall(cls)}')

def classify(data_file, stats_file, result_file):
    x_raw = []
    try:
        with open(data_file, newline='') as csvfile:
            reader = csv.reader(csvfile, delimiter=',')
            for row in reader:
                x_raw.append(row[0])
    except Exception as e:
        print(f'An error while reading {data_file} occured:\n' + str(e))

    x = preprocess(x_raw)

    classifier = NaiveBayesClassifier()
    try:
        classifier.load_stats(stats_file)
    except Exception as e:
        print(f'An error while loading stats occured:\n' + str(e))

    y_predicted = classifier.predict(x)

    try:
        with open(result_file, 'w+', newline='') as csvfile:
            writer = csv.writer(csvfile, delimiter=',')
            for text, cls in zip(x_raw, y_predicted):
                writer.writerow([text, decode[cls]])
    except Exception as e:
        print(f'An error while writing {result_file} occured:\n' + str(e))

    print(f'Result saved as {result_file}')

if len(sys.argv) < 2:
    usage()

_, mode, *args = sys.argv

flags, parameters = parse_args(args)

decode = {
    '0' : 'negative',
    '1' : 'positive'
}

if mode == 'learn':
    stats_file = 'stats.json' if '--stats_file' not in parameters else parameters['--stats_file']
    data_file = 'data.csv' if '--data_file' not in parameters else parameters['--data_file']

    learn(data_file, stats_file)

elif mode == 'classify':
    stats_file = 'stats.json' if '--stats_file' not in parameters else parameters['--stats_file']
    data_file = 'data.csv' if '--data_file' not in parameters else parameters['--data_file']
    result_file = 'result.csv' if '--result_file' not in parameters else parameters['--result_file']

    classify(data_file, stats_file, result_file)

else:
    usage()