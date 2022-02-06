#ifndef MAT_H
#define MAT_H

#include "vec.h"
#include <iostream>

struct mat3{
    double m[3][3];
    mat3() {}
    mat3(double m11, double m12, double m13,
            double m21, double m22, double m23,
            double m31, double m32, double m33){
        m[0][0] = m11; m[0][1] = m12; m[0][2] = m13;
        m[1][0] = m21; m[1][1] = m22; m[1][2] = m23;
        m[2][0] = m31; m[2][1] = m32; m[2][2] = m33;
    }

    static mat3 identity(){
        mat3 res;
        for(int i = 0; i < 3; ++i){
            for(int j = 0; j < 3; ++j){
                res.m[i][j] = 0.0f;
            }
        }
        for(int i = 0; i < 3; ++i)
            res.m[i][i] = 1.0f;

        return res;
    }

    double* operator[](int idx){
        return m[idx];
    }
};

std::ostream& operator<<(std::ostream &os, const mat3 &m){
    for(int i = 0 ; i < 3; ++i){
        for(int j = 0; j < 3; ++j){
            os << m.m[i][j] << ' ';
        }
        os << std::endl;
    }
    return os;
}

double det(const mat3 &m){
    return m.m[0][0] * m.m[1][1] * m.m[2][2] + m.m[1][0] * m.m[0][2] * m.m[2][1] + m.m[2][0] * m.m[0][1] * m.m[1][2]
        - m.m[0][2] * m.m[1][1] * m.m[2][0] - m.m[0][0] * m.m[1][2] * m.m[2][1] - m.m[0][1] * m.m[1][0] * m.m[2][2]; 
}

mat3 inv(const mat3 &m){
        double d = det(m);

        double m11 = (m.m[1][1] * m.m[2][2] - m.m[2][1] * m.m[1][2]) / d;
        double m12 = (m.m[2][1] * m.m[0][2] - m.m[0][1] * m.m[2][2]) / d;
        double m13 = (m.m[0][1] * m.m[1][2] - m.m[1][1] * m.m[0][2]) / d;

        double m21 = (m.m[2][0] * m.m[1][2] - m.m[1][0] * m.m[2][2]) / d;
        double m22 = (m.m[0][0] * m.m[2][2] - m.m[2][0] * m.m[0][2]) / d;
        double m23 = (m.m[1][0] * m.m[0][2] - m.m[0][0] * m.m[1][2]) / d;

        double m31 = (m.m[1][0] * m.m[2][1] - m.m[2][0] * m.m[1][1]) / d;
        double m32 = (m.m[2][0] * m.m[0][1] - m.m[0][0] * m.m[2][1]) / d;
        double m33 = (m.m[0][0] * m.m[1][1] - m.m[1][0] * m.m[0][1]) / d;

        return mat3(m11, m12, m13,
                    m21, m22, m23,
                    m31, m32, m33);
    }

mat3 operator*(const mat3 &a, const mat3 &b){
    mat3 res;
    for(int i = 0; i < 3; ++i){
        for(int j = 0; j < 3; ++j){
            double sum = 0.0f;
            for(int k = 0; k < 3; ++k){
                sum += a.m[i][k] * b.m[k][j];
            }
            res.m[i][j] = sum;
        }
    }
    return res;
}

mat3 operator+(const mat3 &a, const mat3 &b){
    mat3 res;
    for(int i = 0; i < 3; ++i)
        for(int j = 0; j < 3; ++j)
            res.m[i][j] = a.m[i][j] + b.m[i][j];
    
    return res;
}

mat3 operator*(double a, const mat3 &m){
    mat3 res;
    for(int i = 0; i < 3; ++i)
        for(int j = 0; j < 3; ++j)
            res.m[i][j] = a * m.m[i][j];
    
    return res;
}

mat3 operator*(const mat3 &m, double a){
    return a * m;
}

vec3 operator*(const mat3 &m, const vec3 &v){
    vec3 res;
    res.x = m.m[0][0] * v.x + m.m[0][1] * v.y + m.m[0][2] * v.z; 
    res.y = m.m[1][0] * v.x + m.m[1][1] * v.y + m.m[1][2] * v.z; 
    res.z = m.m[2][0] * v.x + m.m[2][1] * v.y + m.m[2][2] * v.z; 
    return res;
}

#endif