from typing import Counter
import numpy as np
from collections import Counter

import matplotlib.pyplot as plt

class _Node:
    def __init__(self):
        self.left = None
        self.right = None
        self.feature_index = None
        self.tresh = None
        self.cls = None

eps = 1e-5

def _approx_equal(a, b):
    return np.abs(a - b) < eps

def _loss(counts, n):
    s = 0
    for k in counts.values():
        if _approx_equal(k, 0):
            continue

        if k > 0:
            p = k / n
            s += p * np.log(p)
            # s += p * np.log2(p)

    return -s

class DecisionTree(object):
    """
    В качестве функции потерь используйте логистическую функцию потерь, или же функцию потерь
    для кросс-энтропии sklearn.metrics.log_loss
    """

    def __init__(self, max_tree_depth, min_node_records):
        self.max_tree_depth = max_tree_depth
        self.min_node_records = min_node_records

    def __leaf(self, y):
        node = _Node()
        node.cls = Counter(y).most_common(1)[0][0]
        return node

    def __branch(self, X, y, depth):
        node = _Node()

        feature_index, tresh = self.split(X, y)

        if feature_index is None: 
            # Если feature_index None, значит по какой-то причине не получилось разделить элементы. Это может случиться по следующим причнам:
            # не получилось разделить с "приростом информации" (dl > 0)
            # нельзя разделить потому что в выборке один элемент
            return self.__leaf(y)

        idx_left = X[:, feature_index] < tresh
        idx_right = ~idx_left
        Xl, yl = X[idx_left], y[idx_left]
        Xr, yr = X[idx_right], y[idx_right]

        if len(Xl) < self.min_node_records or len(Xr) < self.min_node_records or len(np.unique(y)) == 1 or depth == self.max_tree_depth - 1:
            return self.__leaf(y)

        node.feature_index = feature_index
        node.tresh = tresh
        node.left = self.__branch(Xl, yl, depth + 1)
        node.right = self.__branch(Xr, yr, depth + 1)

        return node

    def __build(self, X, y):
        self.root = self.__branch(X, y, 0)

    def fit(self, X, y):
        """
        X - матрица входным параметров n x m
        y - вектор исходов n, каждое из которых имеет одно из ограниченного множества значений,
            определяющего класс

        Предполагается, что для элементов матрицы можно выполнять операции сравнения (<,=,>)

        После выполнения данной функции дерево должно быть обученным и способным предсказывать
        значение
        """

        self.classes = set(y)
        self.root = None
        self.n_features = X.shape[1]
        self.__build(X, y)

    def split(self, X, y):
        """
        Внутренняя функция. Разбивает матрицу на 2 смежные части, максимизируя критерий.
        """

        n_records = X.shape[0]

        best_dl = float('-inf')
        counts = Counter({ cls : np.sum(y == cls) for cls in self.classes })

        l = _loss(counts, n_records)

        feature_index = None
        tresh = None

        for i in range(self.n_features):
            sort_order = X[:, i].argsort()
            Xs = X[sort_order]
            ys = y[sort_order]

            counts_l = Counter()
            counts_r = Counter(counts)

            for j in range(1, n_records):
                c = ys[j-1]
                counts_l[c] += 1
                counts_r[c] -= 1

                if _approx_equal(Xs[j-1][i], Xs[j][i]):
                    continue

                len_yl = j
                len_yr = n_records - j

                ll, lr = _loss(counts_l, len_yl), _loss(counts_r, len_yr)

                dl = l - len_yl / n_records * ll - len_yr / n_records * lr
                if dl > 0 and dl >= best_dl:
                    best_dl = dl
                    feature_index = i
                    tresh = 0.5 * (Xs[j-1][i] + Xs[j][i])

        return feature_index, tresh

    def __predict_single(self, x):
        node = self.root
        while node.left is not None and node.right is not None:
            feature_value = x[node.feature_index]
            if feature_value < node.tresh:
                node = node.left
            else:
                node = node.right

        return node.cls

    def __print(self, node, tab):
        if node.cls is not None:
            print(' ' * tab + f'cls = {node.cls}')
            return

        self.__print(node.left, tab + 2)
        print(' ' * tab + f'fi: {node.feature_index} trsh: {node.tresh}')
        self.__print(node.right, tab + 2)

    def print(self):
        self.__print(self.root, 0)


    def predict(self, X):
        """
        Выводит вектор предсказаний для заданной матрицы X
        """
        return np.array([self.__predict_single(x) for x in X])

from sklearn.model_selection import train_test_split

from sklearn.metrics import accuracy_score

from sklearn.datasets import load_digits

def main():
    x = np.array([[1], [1], [1], [1], [1], [1], [0], [0], [0], [0], [0]])
    y = np.array([1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0])

    dt = DecisionTree(1000, 1)
    dt.fit(x, y)

    dt.print()

    y_pred = dt.predict(x)

    print(f'y:      {y}')
    print(f'y_pred: {y}')

    print(accuracy_score(y_pred, y))
    exit(0)

def main1():
    np.random.seed(3)

    n1 = 500
    n2 = 500

    # n1 = 2
    # n2 = 2

    cls = [0, 1]

    x1 = np.random.randn(n1, 2)
    # x2 = np.random.randn(40, 2) + np.array([0.7, 0.7])
    # x2 = np.random.randn(n2, 2) + 1.5
    x2 = np.random.randn(n2, 2) + 0.5

    x = np.concatenate([x1, x2], axis=0)
    y = np.array([cls[0]] * n1 + [cls[1]] * n2)
    # y = np.array(list(range(n1 + n2)))

    print(f'{x.shape=}')
    print(f'{y.shape=}')

    perm = np.random.permutation(n1 + n2)

    x = x[perm]
    y = y[perm]

    dt = DecisionTree(1000, 1)
    dt.fit(x, y)
    dt.print()

    y_pred = dt.predict(x)

    print(f'y:      {y}')
    print(f'y_pred: {y_pred}')

    print(f'train_accuracy: {accuracy_score(y_pred, y)}')

    n_grid_points = 300
    x_grid = np.linspace(-3, 3, n_grid_points)
    y_grid = np.linspace(-3, 3, n_grid_points)

    points = []
    for xp in x_grid:
        for yp in y_grid:
            points.append([xp, yp])

    points = np.array(points)

    points_pred = dt.predict(points)

    points1 = points[points_pred == cls[0]]
    points2 = points[points_pred == cls[1]]

    fig, ax = plt.subplots()

    color1 = (0, 0, 1)
    color2 = (1, 0, 0)

    ax.plot(*list(zip(*points1)), color=color1, alpha=0.05, marker='o', linestyle='')
    ax.plot(*list(zip(*points2)), color=color2, alpha=0.05, marker='o', linestyle='')

    ax.plot(*list(zip(*x1)), color=color1, alpha=1, marker='.', linestyle='')
    ax.plot(*list(zip(*x2)), color=color2, alpha=1, marker='.', linestyle='')

    plt.show()

def main2():
    data = load_digits()
    X, y = data.data, data.target

    dt = DecisionTree(max_tree_depth=10, min_node_records=3)

    x_train, x_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=17)

    print(f'{x_train.shape=}')
    print(f'{y_train.shape=}')

    dt.fit(x_train, y_train)
    dt.print()

    y_train_pred = dt.predict(x_train)
    y_test_pred = dt.predict(x_test)

    print(f'train accuracy: {accuracy_score(y_train_pred, y_train)}')
    print(f'test_accuracy: {accuracy_score(y_test_pred, y_test)}')

if __name__ == '__main__':

    import time

    start = time.time()

    # main()
    # main1()
    main2()

    end = time.time()
    print(f'elapsed_time: {end - start}s')
