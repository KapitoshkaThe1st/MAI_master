#include <iostream>
#include <vector>
#include <chrono>

#include "vec.h"
#include "mat.h"

#include "lattice.h"
#include "values.h"
#include "parameters.h"
#include "estimator.h"
#include "optimizer.h"

void print_values(const Values& values){
    std::cout << "a*: " << values.get_a() << ", cohesive energy: " << values.get_e_coh() << ", bulk modulus: " << values.get_b() << std::endl
            << "c11: " << values.get_c11() << ", c12: " << values.get_c12() << ", c44: " << values.get_c44() << std::endl 
            << "e_sol: "  << values.get_e_sol() << ", e_dim_in: " << values.get_e_dim_in() << ", e_dim_on: " << values.get_e_dim_on() << std::endl;
}

void print_parameters(const Parameters& parameters){
    std::cout << "BB_A0 = " << parameters.get_BB_A0() << std::endl;
    std::cout << "BB_A1 = " << parameters.get_BB_A1() << std::endl;
    std::cout << "BB_r0 = " << parameters.get_BB_r0() << std::endl;
    std::cout << "BB_p = " << parameters.get_BB_p() << std::endl;
    std::cout << "BB_xi = " << parameters.get_BB_xi() << std::endl;
    std::cout << "BB_q = " << parameters.get_BB_q() << std::endl;

    std::cout << "AB_A0 = " << parameters.get_AB_A0() << std::endl;
    std::cout << "AB_A1 = " << parameters.get_AB_A1() << std::endl;
    std::cout << "AB_r0 = " << parameters.get_AB_r0() << std::endl;
    std::cout << "AB_p = " << parameters.get_AB_p() << std::endl;
    std::cout << "AB_xi = " << parameters.get_AB_xi() << std::endl;
    std::cout << "AB_q = " << parameters.get_AB_q() << std::endl;

    std::cout << "AA_A0 = " << parameters.get_AA_A0() << std::endl;
    std::cout << "AA_A1 = " << parameters.get_AA_A1() << std::endl;
    std::cout << "AA_r0 = " << parameters.get_AA_r0() << std::endl;
    std::cout << "AA_p = " << parameters.get_AA_p() << std::endl;
    std::cout << "AA_xi = " << parameters.get_AA_xi() << std::endl;
    std::cout << "AA_q = " << parameters.get_AA_q() << std::endl;
}

struct ErrorFunctional {
    Estimator& estm;
    double cur_a;
    const Values& values_tbl;

    ErrorFunctional(Estimator& estm, const Values& values_tbl) : estm(estm), values_tbl(values_tbl) { }

    double operator()(const Parameters& params) {
        auto values = estm.estimate(params);

        cur_a = values.get_a();

        double error = values.mean_square_error(values_tbl);

        return error;
    };
};

int main(int argc, char** argv){

    if(argc < 2) {
        std::cout << "Specify config file" << std::endl;
        exit(0);
    }

    int nx = 3, ny = 3, nz = 3;

    double a_tbl, e_coh_tbl, b_tbl, c11_tbl, c12_tbl, c44_tbl, e_sol_tbl,  e_dim_in_tbl, e_dim_on_tbl;

    char* cfg_path = argv[1];

    std::ifstream ifs(cfg_path);

    if(!ifs){
        std::cout << "Could not read file " << cfg_path << std::endl;
        exit(0);
    }

    ifs >> a_tbl >> e_coh_tbl >> b_tbl >> c11_tbl >> c12_tbl >> c44_tbl >> e_sol_tbl >> e_dim_in_tbl >> e_dim_on_tbl;


    double min_a, max_a, a_precision;
    ifs >> min_a >> max_a >> a_precision;

    double eps;
    ifs >> eps;

    double e_coh_a;

    ifs >> e_coh_a;

    std::cout << "min_a: " << min_a << ", max_a: " << max_a << ", a_precision: " << a_precision << std::endl;

    Values values_tbl(a_tbl, e_coh_tbl, b_tbl, c11_tbl, c12_tbl, c44_tbl, e_sol_tbl, e_dim_in_tbl, e_dim_on_tbl);

    std::cout << "table values: " << std::endl;
    print_values(values_tbl);

    double min_BB_A0, min_BB_A1, min_BB_r0, min_BB_p, min_BB_xi, min_BB_q;
    double min_AB_A0, min_AB_A1, min_AB_r0, min_AB_p, min_AB_xi, min_AB_q;
    double min_AA_A0, min_AA_A1, min_AA_r0, min_AA_p, min_AA_xi, min_AA_q;
    double max_BB_A0, max_BB_A1, max_BB_r0, max_BB_p, max_BB_xi, max_BB_q;
    double max_AB_A0, max_AB_A1, max_AB_r0, max_AB_p, max_AB_xi, max_AB_q;
    double max_AA_A0, max_AA_A1, max_AA_r0, max_AA_p, max_AA_xi, max_AA_q;

    ifs >> min_BB_A0 >> min_BB_A1 >> min_BB_r0 >> min_BB_p >> min_BB_xi >> min_BB_q
        >> min_AB_A0 >> min_AB_A1 >> min_AB_r0 >> min_AB_p >> min_AB_xi >> min_AB_q
        >> min_AA_A0 >> min_AA_A1 >> min_AA_r0 >> min_AA_p >> min_AA_xi >> min_AA_q
        >> max_BB_A0 >> max_BB_A1 >> max_BB_r0 >> max_BB_p >> max_BB_xi >> max_BB_q
        >> max_AB_A0 >> max_AB_A1 >> max_AB_r0 >> max_AB_p >> max_AB_xi >> max_AB_q
        >> max_AA_A0 >> max_AA_A1 >> max_AA_r0 >> max_AA_p >> max_AA_xi >> max_AA_q;

    int seed;
    ifs >> seed;

    int dummy;
    ifs >> dummy;

    bool verbose = dummy > 0;

    Parameters min_params(min_BB_A0, min_BB_A1, min_BB_r0, min_BB_p, min_BB_xi, min_BB_q,
                    min_AB_A0, min_AB_A1, min_AB_r0, min_AB_p, min_AB_xi, min_AB_q,
                    min_AA_A0, min_AA_A1, min_AA_r0, min_AA_p, min_AA_xi, min_AA_q);


    Parameters max_params(max_BB_A0, max_BB_A1, max_BB_r0, max_BB_p, max_BB_xi, max_BB_q,
                    max_AB_A0, max_AB_A1, max_AB_r0, max_AB_p, max_AB_xi, max_AB_q,
                    max_AA_A0, max_AA_A1, max_AA_r0, max_AA_p, max_AA_xi, max_AA_q);

    std::cout << "min_params: " << std::endl;
    for(size_t i = 0; i < min_params.size(); ++i){
        std::cout << min_params[i] << " ";
    }
    std::cout << std::endl;

    std::cout << "max_params: " << std::endl;
    for(size_t i = 0; i < max_params.size(); ++i){
        std::cout << max_params[i] << " ";
    }
    std::cout << std::endl;

    Parameters optimal_parameters;

    std::cout.precision(17);

#ifdef BENCHMARK

    std::cout << "Bencharking with n times: " << BENCHMARK << std::endl;

    auto start = std::chrono::high_resolution_clock::now();

    for(int i = 0; i < BENCHMARK; ++i) {

        std::cout << "iteration: " << i << std::endl;

        Estimator estm(nx, ny, nz, min_a, max_a, a_precision, e_coh_a);

        ErrorFunctional error_functional(estm, values_tbl);

        seed = seed >= 0 ? seed : time(0);
        srand(seed);

        NelderMeadOptimizer<Parameters, ErrorFunctional> optimizer(error_functional, min_params, max_params, eps * eps, false);

        optimal_parameters = optimizer.optimize();
    }

    auto end = std::chrono::high_resolution_clock::now();

    std::cout << "elapsed time: " << static_cast<double>(std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count()) / BENCHMARK << "ms" << std::endl;

    Estimator estm(nx, ny, nz, min_a, max_a, a_precision, e_coh_a);

#else

    Estimator estm(nx, ny, nz, min_a, max_a, a_precision, e_coh_a);

    ErrorFunctional error_functional(estm, values_tbl);

    seed = seed >= 0 ? seed : time(0);
    srand(seed);

    std::cout << "SEED: " << seed << std::endl;

    NelderMeadOptimizer<Parameters, ErrorFunctional> optimizer(error_functional, min_params, max_params, eps * eps, verbose);

    optimal_parameters = optimizer.optimize();
    
#endif

    print_parameters(optimal_parameters);

    Values optimal_values = estm.estimate(optimal_parameters);

    print_values(optimal_values);

    std::cout << "mean error: " << std::sqrt(optimal_values.mean_square_error(values_tbl)) << std::endl;

    return 0;
}