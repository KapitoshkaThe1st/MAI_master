#pragma once

#include "mat.h"

class SpecialMatrices {
public:
    static mat3 bulk_matrix(double alpha){
        double val = 1.0 + alpha;
        return mat3(val, 0.0, 0.0,
                    0.0, val, 0.0,
                    0.0, 0.0, val);
    }

    static mat3 elasticity11_matrix(double alpha){
        double val = 1.0 + alpha;
        return mat3(val, 0.0, 0.0,
                    0.0, val, 0.0,
                    0.0, 0.0, 1.0);
    }

    static mat3 elasticity12_matrix(double alpha){
        return mat3(1.0 + alpha, 0.0, 0.0,
                    0.0, 1.0 - alpha, 0.0,
                    0.0, 0.0, 1.0);
    }

    static mat3 elasticity44_matrix(double alpha){
        double val = 1.0 / (1.0 - alpha * alpha);
        return mat3(1.0, alpha, 0.0,
                    alpha, 1.0, 0.0,
                    0.0, 0.0, val);
    }
};