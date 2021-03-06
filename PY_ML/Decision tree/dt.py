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

        feature_index, tresh = self.__split(X, y)

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

    def __split(self, X, y):
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

if __name__ == '__main__':
    raise Exception('module standalone execution prohibited')