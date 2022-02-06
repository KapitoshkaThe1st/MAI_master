import numpy as np

from interpolator import Interpolator

class QuadraticInterpolator(Interpolator):
    def __init__(self, f, dim, eps=1e-4):
        super().__init__(f, dim)
        self.eps = eps

    def phi(self, x):
        return np.maximum(0.0, 1 - np.abs(x))

    def phi_q(self, x):
        return np.maximum(0.0, -(x-1)*(x+1))
    
    def phi_li(self, x, l, i):
        result = 1
        for k in range(len(x)):
            if l[k] == 0:
                result *= self.phi(2**l[k] * x[k] - i[k])
            else:
                result *= self.phi_q(2**l[k] * x[k] - i[k])
            
        return result

    def correct(self, l, i, k, a):
        if l[k] > 0:
            im1 = i[:k] + (i[k] - 1,) + i[k+1:]
            am1 = self.compute_coef(l, im1, k + 1)
            
            ip1 = i[:k] + (i[k] + 1,) + i[k+1:]
            ap1 = self.compute_coef(l, ip1, k + 1)
            if l[k] == 1:
                a -= 0.5 * (am1 + ap1)
            else:
                if i[k] % 4 == 1:
                    ip3 = i[:k] + (i[k] + 3,) + i[k+1:]
                    ap3 = self.compute_coef(l, ip3, k + 1)
                    a -= 0.125 * (3 * am1 + 6 * ap1 - ap3)
                
                elif i[k] % 4 == 3:
                    im3 = i[:k] + (i[k] - 3,) + i[k+1:]
                    am3 = self.compute_coef(l, im3, k + 1)
                    a -= 0.125 * (6 * am1 + 3 * ap1 - am3)
                
                else:
                    raise RuntimeError("WTF?! It can't be")
        return a

class AdaptiveQuadraticInterpolator(QuadraticInterpolator):
    def __init__(self, f, dim, eps=1e-4):
        self.eps = eps
        super().__init__(f, dim)

    def split_condition(self, l, i, a):
        return abs(a) > self.eps

class SparseQuadraticInterpolator(QuadraticInterpolator):
    def __init__(self, f, dim, max_sum_level):
        self.max_sum_level = max_sum_level
        super().__init__(f, dim)

    def split_condition(self, l, i, a):
        return sum(l) < self.max_sum_level + self.dim - 1

class RegularQuadraticInterpolator(QuadraticInterpolator):
    def __init__(self, f, dim, max_level):
        self.max_level = max_level
        super().__init__(f, dim)

    def split_one_dim(self, l, i, k):
        if l[k] < self.max_level:
            super().split_one_dim(l, i, k)

    def split_condition(self, l, i, a):
        return True