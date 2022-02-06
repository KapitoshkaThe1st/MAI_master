class Metrics:
    def __confusion_matrix(self, y_true, y_pred, cls):
        tp, fp, fn, tn = 0, 0, 0, 0
        for yp, ya in zip(y_pred, y_true):
            if yp == ya:
                if ya == cls:
                    tp += 1
                else:
                    tn += 1
            else:
                if ya == cls:
                    fn += 1
                else:
                    fp += 1

        return tp, fp, fn, tn

    def __init__(self, y_true, y_pred):
        self.classes = list(set(y_pred))

        self.metrics = {}
        for cls in self.classes:
            self.metrics[cls] = self.__confusion_matrix(y_true, y_pred, cls)

    def confusion_matrix(self, cls):
        return self.metrics[cls]

    def accuracy(self):
        tp, fp, fn, tn = self.metrics[self.classes[0]]
        return (tp + tn) / (tp + tn + fp + fn)

    def precsion(self, cls):
        tp, fp, _, _ = self.metrics[cls]
        return tp / (tp + fp)

    def recall(self, cls):
        tp, _, fn, _ = self.metrics[cls]
        return tp / (tp + fn)

