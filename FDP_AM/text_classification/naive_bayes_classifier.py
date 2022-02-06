from collections import Counter
import math as m

import json

class NaiveBayesClassifier:
    def fit(self, x_train, y_train):
        self.classes = set(y_train)
        self.n_total_texts = len(x_train)
        self.n_texts_by_class = Counter(y_train)
        unique_words = set()
        self.n_words_by_class = {cls : 0 for cls in self.classes}
        self.words_by_class = {cls : Counter() for cls in self.classes}

        for k, (words, cls) in enumerate(zip(x_train, y_train)):
            self.words_by_class[cls] += Counter(words)
            self.n_words_by_class[cls] += len(words)
            unique_words.update(words)

            print(f'Processed: {(k + 1) / self.n_total_texts * 100.0:.2f}% ({k + 1} out of {self.n_total_texts})', end='\r')

        self.n_unique_words = len(unique_words)
        print('\nCompleted!')


    def probs(self, words):
        probs = dict()
        for cls in self.classes:
            prob = m.log(self.n_texts_by_class[cls] / self.n_total_texts)
            for word in words:
                word_freq = 0 if word not in self.words_by_class[cls] else self.words_by_class[cls][word]

                prob += m.log((word_freq + 1) / (self.n_unique_words + self.n_words_by_class[cls]))
            probs[cls] = prob

        return probs

    def predict(self, x):
        y_predicted = []
        for words in x:
            p = self.probs(words)
            y_predicted.append(max(p.items(), key=lambda p: p[1])[0])
        return y_predicted

    def load_stats(self, stats_file='stats.json'):
        with open(stats_file, 'r') as f:
            data = json.load(f)
        self.classes = data['classes']
        self.n_total_texts = data['n_total_texts']
        self.n_texts_by_class = data['n_texts_by_class']
        self.n_words_by_class = data['n_words_by_class']
        self.words_by_class = data['words_by_class']
        self.n_unique_words = data['n_unique_words']
        

    def save_stats(self, stats_file='stats.json'):
        data = {
            'classes' : list(self.classes),
            'n_total_texts' : self.n_total_texts,
            'n_texts_by_class' : self.n_texts_by_class,
            'n_words_by_class' : self.n_words_by_class,
            'words_by_class' : self.words_by_class,
            'n_unique_words' : self.n_unique_words
        }

        with open(stats_file, 'w') as f:
            json.dump(data, f)


if __name__ == '__main__':
    raise NotImplemented("it's a module, bro")
