
from numpy.lib.shape_base import split
from queue import Queue

class Interpolator:
    def __init__(self, f, dim):
        self.f = f
        self.dim = dim
        self.basis = dict()
        self.splited = set()
        self.mem = dict()
        
        self.build_sparse_grid()

    def phi(self, x):
        raise NotImplemented('abstract class')
        
    def phi_li(self, x, l, i):
        raise NotImplemented('abstract class')

    def normalize_li(self, l, i):
        i_new = list(i)
        l_new = list(l)
        for j in range(self.dim):
            if i_new[j] > 0:
                while i_new[j] & 1 == 0:
                    i_new[j] >>= 1
                    l_new[j] -= 1
            else:
                l_new[j] = 0
        return tuple(l_new), tuple(i_new)

    def correct(a):
        raise NotImplemented('abstract class')

    def compute_coef(self, l, i, k=0):
        # print(f'{l=} {i=} {k=}')
        l, i = self.normalize_li(l, i)

        key = (l, i, k)
        if key in self.mem:
            return self.mem[key]

        if k == self.dim:
            x_li = [1 / (2 ** l[d])* i[d] for d in range(self.dim)] 

            result = self.f(x_li)
            return result
        
        a = self.compute_coef(l, i, k + 1)
        a = self.correct(l, i, k, a)

        self.mem[(l, i, k)] = a
        return a

    def add_to_basis(self, l, i, value):
        key = (l, i)
        if key not in self.basis:
            self.basis[key] = value

    def build_trivial_grid_aux(self, l, i, k=0):
        if k >= self.dim:
            self.add_to_basis(l, i, self.f(i))
            return

        l = l + (0, )
        self.build_trivial_grid_aux(l, i + (0, ), k+1)
        self.build_trivial_grid_aux(l, i + (1, ), k+1)

    def build_trivial_grid(self):
        self.build_trivial_grid_aux((), (), 0)
        
        for (key, a) in self.basis.items():
            self.mem[(*key, 0)] = a

    def split_one_dim(self, l, i, k):
        new_lk = l[k] + 1
        new_l = l[:k] + (new_lk,) + l[k+1:]
        
        new_ik = 2 * i[k] - 1
        if 0 < new_ik < 2 ** new_lk:
            new_i = i[:k] + (new_ik,) + i[k+1:]
            self.add_to_basis(new_l, new_i, self.compute_coef(new_l, new_i))
            self.q.put((new_l, new_i))

        new_ik = 2 * i[k] + 1
        if 0 < new_ik < 2 ** new_lk:
            new_i = i[:k] + (new_ik,) + i[k+1:]
            self.add_to_basis(new_l, new_i, self.compute_coef(new_l, new_i))
            self.q.put((new_l, new_i))

    def split(self, l, i):
        for k in range(self.dim):
            self.split_one_dim(l, i, k)
        self.splited.add((l, i))

    def split_condition(self, l, i, a):
        raise NotImplemented('abstract class')


    def build_sparse_grid(self):
        self.build_trivial_grid()

        split_count = 0

        
        self.q = Queue()
        for key in self.basis:
            self.q.put(key)

        while not self.q.empty():
            key = self.q.get()
            a = self.basis[key]
            l, i = key
            if self.split_condition(l, i, a) and key not in self.splited:
                split_count += 1
                self.split(l, i)

    def eval(self, x):
        result = 0
        for ((l, i), a) in self.basis.items():
            result += a * self.phi_li(x, l, i)

        return result

    def __call__(self, x):
        return self.eval(x)
