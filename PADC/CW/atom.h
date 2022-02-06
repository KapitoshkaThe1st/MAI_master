#pragma once

#include "vec.h"

enum class AtomType {
    A, B
};

class Atom {
public:
    vec3 position;
    AtomType type;

public:
    Atom(const vec3& position, AtomType type = AtomType::B) : position(position), type{type} {}
};
