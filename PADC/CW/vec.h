#ifndef VEC_H
#define VEC_H

#include <iostream>
#include <cmath>

struct vec3{
    double x, y, z;
    vec3() : x(0.0f), y(0.0f), z(0.0f) {}
    vec3(double val) : x(val), y(val), z(val) {}
    vec3(double x, double y, double z) : x(x), y(y), z(z) {}
} __attribute__ ((aligned(16)));

vec3 operator+(const vec3 &a, const vec3 &b){
    return {a.x + b.x, a.y + b.y, a.z + b.z};
}

void operator+=(vec3 &a, const vec3 &b){
    a.x += b.x;
    a.y += b.y;
    a.z += b.z;
}

vec3 operator-(const vec3 &a){
    return {-a.x, -a.y, -a.z};
}

vec3 operator-(const vec3 &a, const vec3 &b){
    return {a.x - b.x, a.y - b.y, a.z - b.z};
}

void operator-=(vec3 &a, const vec3 &b){
    a.x -= b.x;
    a.y -= b.y;
    a.z -= b.z;
}

vec3 operator*(double c, const vec3 &v){
    return {c * v.x, c * v.y, c * v.z};
}

vec3 operator*(const vec3 &v, double c){
    return c * v;
}

vec3 operator*(const vec3 &v, const vec3 &w){
    return {v.x * w.x, v.y * w.y, v.z * w.z};
}

std::ostream& operator<<(std::ostream &os, const vec3 &f){
    os << f.x << ' ' << f.y << ' ' << f.z;
    return os;
}

std::istream& operator>>(std::istream &is, vec3 &f){
    is >> f.x >> f.y >> f.z;
    return is;
}

double length(const vec3& v){
    return sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
}

double distance(const vec3& a, const vec3& b){
    return length(a - b);
}

#endif