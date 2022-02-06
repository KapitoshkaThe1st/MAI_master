#pragma once

#include <vector>
#include <fstream>

#include <omp.h>

#include "atom.h"
#include "mat.h"
#include "parameters.h"

class Lattice {
    
public:

    std::vector<Atom> atoms;

    double a;
    int nx, ny, nz;

    bool is_uniform = true;

    int n_atoms() {
        return atoms.size();
    }

    double v0(){
        return a*a*a * nx * ny * nz;
    }

    void drop(double z_treshhold) {
        atoms.erase(std::remove_if(atoms.begin(), atoms.end(), [=](const Atom& atom) {return atom.position.z > z_treshhold;}), atoms.end());
    }

    void set_a(double a){
        this->a = a;
    }

    inline double r_cutoff() const {
        return 1.7 * a;
    }

    inline vec3 period() const {
        return vec3(nx, ny, nz);
    }

    Lattice(double a, int nx, int ny, int nz) : a{a}, nx{nx}, ny{ny}, nz{nz}
    {
        atoms.reserve(4 * nx * ny * nz);

        vec3 offset1(0.5, 0.5, 0.0);
        vec3 offset2(0.0, 0.5, 0.5);
        vec3 offset3(0.5, 0.0, 0.5);

        for(int k = 0; k < nz; ++k){
            for(int j = 0; j < ny; ++j){
                for(int i = 0; i < nx; ++i){
                    vec3 position = vec3(i, j, k);
                    atoms.emplace_back(position);
                    atoms.emplace_back(position + offset1);
                    atoms.emplace_back(position + offset2);
                    atoms.emplace_back(position + offset3);
                }
            }
        }
    }

    double periodic_distance(const vec3& p1, const vec3& p2, const mat3 * const deform_mat) const {
        auto r = p1 - p2;
        
        r.x = fabs(r.x); 
        r.y = fabs(r.y); 
        r.z = fabs(r.z);

        vec3 prd = period();

        if(r.x > 0.5 * prd.x)
            r.x -= prd.x;
        if(r.y > 0.5 * prd.y)
            r.y -= prd.y;
        if(r.z > 0.5 * prd.z)
            r.z -= prd.z;

        return a * (deform_mat == nullptr ? length(r) : length((*deform_mat) * r));
    }

    double repulsion_energy(const size_t i, const Parameters& params, const mat3 * const deform_mat) {
        double energy = 0.0;
        for(size_t j = 0; j < atoms.size(); ++j){
            if(j == i)
                continue;

            double rij = periodic_distance(atoms[i].position, atoms[j].position, deform_mat);

            if(rij > r_cutoff())
                continue;

            double A0 = params.get_A0(atoms[i].type, atoms[j].type);
            double A1 = params.get_A1(atoms[i].type, atoms[j].type);
            double r0 = params.get_r0(atoms[i].type, atoms[j].type);
            double p = params.get_p(atoms[i].type, atoms[j].type);

            double mul = (A1 * (rij - r0) + A0);
            double power = -p * (rij / r0 - 1.0);
            double val = mul * exp(power);
            energy += val;
        }
        return energy;
    }

    double attraction_energy(const size_t i, const Parameters& params, const mat3 * const deform_mat) {
        double sum = 0.0;
        for(size_t j = 0; j < atoms.size(); ++j){
            if(j == i)
                continue;

            double rij = periodic_distance(atoms[i].position, atoms[j].position, deform_mat);
 
            if(rij > r_cutoff())
                continue;

            double xi = params.get_xi(atoms[i].type, atoms[j].type);
            double q = params.get_q(atoms[i].type, atoms[j].type);
            double r0 = params.get_r0(atoms[i].type, atoms[j].type);

            double val = xi * xi * exp(-2.0 * q * (rij / r0 - 1.0));
            sum += val;
        }

        double result = -sqrt(sum);
        return result;
    }

    double total_energy_full(const Parameters& params, const mat3 * const deform_mat) {
        double energy = 0.0;
        #ifdef _OPENMP
        #pragma omp parallel for num_threads(16) reduction(+:energy) schedule(static)
        #endif

        for(size_t i = 0; i < atoms.size(); ++i){
            energy += repulsion_energy(i, params, deform_mat) + attraction_energy(i, params, deform_mat);
        }
        return energy;
    }

    double cohesive_energy_full(const Parameters& params, const mat3 * const deform_mat) {
        return total_energy_full(params, deform_mat) / n_atoms();
    }

    double total_energy_uniform(const Parameters& params, const mat3 * const deform_mat) {
        return cohesive_energy_uniform(params, deform_mat) * n_atoms();
    }

    double cohesive_energy_uniform(const Parameters& params, const mat3 * const deform_mat) {
        return repulsion_energy(0, params, deform_mat) + attraction_energy(0, params, deform_mat);
    }

    double total_energy(const Parameters& params, const mat3 * const deform_mat = nullptr) {
        return is_uniform ? total_energy_uniform(params, deform_mat) : total_energy_full(params, deform_mat);
    }

    double cohesive_energy(const Parameters& params, const mat3 * const deform_mat = nullptr) {
        return is_uniform ? cohesive_energy_uniform(params, deform_mat) : cohesive_energy_full(params, deform_mat);
    }
};