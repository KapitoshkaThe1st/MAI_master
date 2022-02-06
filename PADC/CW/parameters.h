#pragma once

#include <valarray>

#define PARAMETERS_SIZE (18)

class Parameters : public std::valarray<double> {
private:
	enum class ParameterPosition {
		BB_A0 = 0, BB_A1, BB_r0, BB_p, BB_xi, BB_q,
        AB_A0, AB_A1, AB_r0, AB_p, AB_xi, AB_q,
        AA_A0, AA_A1, AA_r0, AA_p, AA_xi, AA_q
	};

	double param(ParameterPosition pos) const {
		return (*this)[static_cast<size_t>(pos)];
	}

	double& param(ParameterPosition pos) {
		return (*this)[static_cast<size_t>(pos)];
	}

public:
    using std::valarray<double>::valarray;

	Parameters() : std::valarray<double>(PARAMETERS_SIZE) {}

	Parameters(
        double BB_A0, double BB_A1, double BB_r0, double BB_p, double BB_xi, double BB_q,
        double AB_A0, double AB_A1, double AB_r0, double AB_p, double AB_xi, double AB_q,
        double AA_A0, double AA_A1, double AA_r0, double AA_p, double AA_xi, double AA_q
    ) : Parameters()
    {
		param(ParameterPosition::BB_A0) = BB_A0;
		param(ParameterPosition::BB_A1) = BB_A1;
		param(ParameterPosition::BB_r0) = BB_r0;
		param(ParameterPosition::BB_p) = BB_p;
		param(ParameterPosition::BB_xi) = BB_xi;
		param(ParameterPosition::BB_q) = BB_q;
        
        param(ParameterPosition::AB_A0) = AB_A0;
		param(ParameterPosition::AB_A1) = AB_A1;
		param(ParameterPosition::AB_r0) = AB_r0;
		param(ParameterPosition::AB_p) = AB_p;
		param(ParameterPosition::AB_xi) = AB_xi;
		param(ParameterPosition::AB_q) = AB_q;

        param(ParameterPosition::AA_A0) = AA_A0;
		param(ParameterPosition::AA_A1) = AA_A1;
		param(ParameterPosition::AA_r0) = AA_r0;
		param(ParameterPosition::AA_p) = AA_p;
		param(ParameterPosition::AA_xi) = AA_xi;
		param(ParameterPosition::AA_q) = AA_q;
	}

    double get_BB_A0() const { return param(ParameterPosition::BB_A0); }
    double get_BB_A1() const { return param(ParameterPosition::BB_A1); }
    double get_BB_r0() const { return param(ParameterPosition::BB_r0); }
    double get_BB_p() const { return param(ParameterPosition::BB_p); }
    double get_BB_xi() const { return param(ParameterPosition::BB_xi); }
    double get_BB_q() const { return param(ParameterPosition::BB_q); }
    double get_AB_A0() const { return param(ParameterPosition::AB_A0); }
    double get_AB_A1() const { return param(ParameterPosition::AB_A1); }
    double get_AB_r0() const { return param(ParameterPosition::AB_r0); }
    double get_AB_p() const { return param(ParameterPosition::AB_p); }
    double get_AB_xi() const { return param(ParameterPosition::AB_xi); }
    double get_AB_q() const { return param(ParameterPosition::AB_q); }
    double get_AA_A0() const { return param(ParameterPosition::AA_A0); }
    double get_AA_A1() const { return param(ParameterPosition::AA_A1); }
    double get_AA_r0() const { return param(ParameterPosition::AA_r0); }
    double get_AA_p() const { return param(ParameterPosition::AA_p); }
    double get_AA_xi() const { return param(ParameterPosition::AA_xi); }
    double get_AA_q() const { return param(ParameterPosition::AA_q); }

    double get_A0(AtomType type1, AtomType type2) const { 
        if (type1 != type2){
            return get_AB_A0();
        }
        return type1 == AtomType::A ? get_AA_A0() : get_BB_A0();
    }
    double get_A1(AtomType type1, AtomType type2) const { 
        if (type1 != type2){
            return get_AB_A1();
        }
        return type1 == AtomType::A ? get_AA_A1() : get_BB_A1();
    }
    double get_r0(AtomType type1, AtomType type2) const { 
        if (type1 != type2){
            return get_AB_r0();
        }
        return type1 == AtomType::A ? get_AA_r0() : get_BB_r0();
    }
    double get_p(AtomType type1, AtomType type2) const { 
        if (type1 != type2){
            return get_AB_p();
        }
        return type1 == AtomType::A ? get_AA_p() : get_BB_p();
    }
    double get_xi(AtomType type1, AtomType type2) const { 
        if (type1 != type2){
            return get_AB_xi();
        }
        return type1 == AtomType::A ? get_AA_xi() : get_BB_xi();
    }
    double get_q(AtomType type1, AtomType type2) const { 
        if (type1 != type2){
            return get_AB_q();
        }
        return type1 == AtomType::A ? get_AA_q() : get_BB_q();
    }
};