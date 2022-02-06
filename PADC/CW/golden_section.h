#pragma once

#include <iostream>

const double phi = 1.6180339887498;

template<class F>
double golden_section(const F& f, double a, double b, double eps = 0.01){
    while(true){
        double x1 = b - (b - a) / phi;
        double x2 = a + (b - a) / phi;

        double y1 = f(x1);
        double y2 = f(x2);

        if(y1 > y2){
            a = x1;
        }
        else{
            b = x2;
        }

        if(fabs(b - a) < eps)
            return (a + b) / 2.0;
    }
}