#pragma once

#include <valarray>

#define VALUES_SIZE (9)

class Values : public std::valarray<double> {
private:
	enum class ValuePosition {
		A = 0, E_COH, B, C11, C12, C44, E_SOL, E_DIM_IN, E_DIM_ON
	};

	double param(ValuePosition pos) const {
		return (*this)[static_cast<size_t>(pos)];
	}

	double& param(ValuePosition pos) {
		return (*this)[static_cast<size_t>(pos)];
	}

public:
    using std::valarray<double>::valarray;
	Values(double a, double e_coh, double b, double c11, double c12, 
		double c44, double e_sol, double e_dim_in, double e_dim_on) 
	: std::valarray<double>(VALUES_SIZE) 
	{
		param(ValuePosition::A) = a;
		param(ValuePosition::E_COH) = e_coh;
		param(ValuePosition::B) = b;
		param(ValuePosition::C11) = c11;
		param(ValuePosition::C12) = c12;
		param(ValuePosition::C44) = c44;
		param(ValuePosition::E_SOL) = e_sol;
		param(ValuePosition::E_DIM_IN) = e_dim_in;
		param(ValuePosition::E_DIM_ON) = e_dim_on;
	}

	double get_a() const { return param(ValuePosition::A); }
	double get_e_coh() const { return param(ValuePosition::E_COH); }
	double get_b() const { return param(ValuePosition::B); }
	double get_c11() const { return param(ValuePosition::C11); }
	double get_c12() const { return param(ValuePosition::C12); }
	double get_c44() const { return param(ValuePosition::C44); }
	double get_e_sol() const { return param(ValuePosition::E_SOL); }
	double get_e_dim_in() const { return param(ValuePosition::E_DIM_IN); }
	double get_e_dim_on() const { return param(ValuePosition::E_DIM_ON); }

	double mean_square_error(const Values& other){
		return std::pow(1.0 - *this / other, 2.0).sum() / size();
	}
};