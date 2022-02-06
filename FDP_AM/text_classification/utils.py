def split_data(x, y, test_portion):
    n_samples = len(x)
    train_size = round(n_samples * (1 - test_portion))
    test_size = round(n_samples * test_portion)

    return x[:train_size], y[:train_size], x[train_size:train_size + test_size], y[train_size:train_size + test_size]