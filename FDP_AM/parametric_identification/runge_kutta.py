import numpy as np
import matplotlib.pyplot as plt
from bisect import bisect_left

def lower_bound(a, x):
    return bisect_left(a, x, 0, len(a))

def one(v):
    return 1.0

class RungeKuttaSolver:
    def __init__(self, functions, initial_conditions, a, b, h):
        self.a = a
        self.b = b
        self.h = h

        functions = [one] + functions
        initial_conditions = [a] + initial_conditions

        n_functions = len(functions)
        n_points = int((b - a) / h) + 1

        self.xs = [self.a + i * self.h for i in range(n_points)]

        self.function_value = [[0] * n_points for _ in range(n_functions)]
        for j in range(n_functions):
            self.function_value[j][0] = initial_conditions[j]

        for i in range(n_points-1):
            y_init = [self.function_value[j][i] for j in range(n_functions)]
            y_cur = y_init[:]

            k1 = [0] * n_functions
            k2 = [0] * n_functions
            k3 = [0] * n_functions
            k4 = [0] * n_functions

            for j in range(n_functions):
                k1[j] = functions[j](y_cur)

            for j in range(n_functions):
                y_cur[j] = y_init[j] + 0.5 * h * k1[j]
            
            for j in range(n_functions):
                k2[j] = functions[j](y_cur)

            for j in range(n_functions):
                y_cur[j] = y_init[j] + 0.5 * h * k2[j]

            for j in range(n_functions):
                k3[j] = functions[j](y_cur)

            for j in range(n_functions):
                y_cur[j] = y_init[j] + h * k3[j]
            
            for j in range(n_functions):
                k4[j] = functions[j](y_cur)

            for j in range(n_functions):
                self.function_value[j][i+1] = self.function_value[j][i] + self.h / 6 * (k1[j] + 2 * (k2[j] + k3[j]) + k4[j])

    def __call__(self, k, x):
        # print(f'{self.function_value}')
        i = max(min(lower_bound(self.function_value[0], x) - 1, len(self.function_value[0]) - 2), 0)
        # print(f'{x=} {i=}')
        c = (x - self.function_value[0][i]) / self.h
        return self.function_value[k+1][i] * (1 - c) + self.function_value[k+1][i+1] * c

# y' - 3y / x = x^3 + x

def y_reference(x):
    return x ** 4 - x ** 2 + 3 * np.abs(x) ** 3

def f(v):
    # print(f'{v=}')
    x = v[0]
    y = v[1]
    return x ** 3 + x + 3 * y / x

if __name__ == '__main__':
    a = 1
    b = 2

    h = 0.1
    y0 = 3

    fs = [f]
    initial_conditions = [y0]

    solver = RungeKuttaSolver(fs, initial_conditions, a, b, h)

    # print(solver(0, a + h + h/2))

    fig, ax = plt.subplots(figsize=(10, 10))

    x = np.linspace(a, b, 1000)
    y_ref = y_reference(x)
    y_solution = np.array([solver(0, xx) for xx in x])

    ax.plot(x, y_ref, 'r')
    ax.plot(x, y_solution, 'g')

    plt.show()